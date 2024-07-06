package me.roundaround.roundalib.config.manage;

import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.fabricmc.api.EnvType;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ModConfigImpl implements ModConfig {
  protected final String modId;
  protected final String configId;
  protected final int configVersion;
  protected final LinkedHashMap<String, ArrayList<ConfigOption<?>>> byGroup = new LinkedHashMap<>();
  protected final LinkedHashMap<ConfigPath, ConfigOption<?>> byPath = new LinkedHashMap<>();
  protected final HashMap<ConfigPath, EnvType> envTypes = new HashMap<>();
  protected final HashSet<ConfigPath> noControls = new HashSet<>();
  protected final HashSet<Consumer<ModConfig>> listeners = new HashSet<>();

  protected int versionFromFile;
  protected boolean isInitialized = false;

  protected ModConfigImpl(String modId) {
    this(modId, 1);
  }

  protected ModConfigImpl(String modId, String configId) {
    this(modId, configId, 1);
  }

  protected ModConfigImpl(String modId, int configVersion) {
    this(modId, modId, configVersion);
  }

  protected ModConfigImpl(String modId, String configId, int configVersion) {
    this.modId = modId;
    this.configId = configId;
    this.configVersion = configVersion;
  }

  public final void init() {
    if (this.isInitialized) {
      return;
    }
    this.onInit();
    this.isInitialized = true;
  }

  protected void onInit() {
    this.initializeStore();
  }

  protected abstract void registerOptions();

  public Text getLabel() {
    return Text.translatable(this.getModId() + "." + this.getConfigId() + ".title");
  }

  @Override
  public String getModId() {
    return this.modId;
  }

  @Override
  public int getVersion() {
    return this.configVersion;
  }

  public String getConfigId() {
    return this.configId;
  }

  @Override
  public ConfigOption<?> getByPath(String path) {
    return this.getByPath(ConfigPath.parse(path));
  }

  @Override
  public ConfigOption<?> getByPath(ConfigPath path) {
    return this.byPath.get(path);
  }

  @Override
  public void refresh() {
    ModConfig.super.refresh();
    this.listeners.forEach((listener) -> listener.accept(this));
  }

  @Override
  public void subscribe(Consumer<ModConfig> listener) {
    this.listeners.add(listener);
  }

  @Override
  public void unsubscribe(Consumer<ModConfig> listener) {
    this.listeners.remove(listener);
  }

  @Override
  public void clear() {
    this.byGroup.clear();
    this.byPath.clear();
    this.listeners.clear();
  }

  @Override
  public List<ConfigOption<?>> getAll() {
    return List.copyOf(this.byPath.values());
  }

  @Override
  public Map<String, List<ConfigOption<?>>> getAllByGroup() {
    LinkedHashMap<String, List<ConfigOption<?>>> map = new LinkedHashMap<>();
    this.byGroup.forEach((group, options) -> {
      map.put(group, List.copyOf(options));
    });
    return Collections.unmodifiableMap(map);
  }

  protected boolean updateConfigVersion(int version, com.electronwill.nightconfig.core.Config inMemoryConfigSnapshot) {
    return false;
  }

  protected <T extends ConfigOption<?>> T register(T option) {
    option.setModId(this.modId);
    option.subscribePending((value) -> this.refresh());

    this.byGroup.computeIfAbsent(option.getGroup(), (group) -> new ArrayList<>()).add(option);
    this.byPath.put(option.getPath(), option);

    return option;
  }

  protected <T extends ConfigOption<?>, B extends RegistrationBuilderImpl<T, B>> Function<B, T> createCommitHandler(T option) {
    return (builder) -> {
      T outOption = this.register(option);
      ConfigPath path = outOption.getPath();
      if (builder.envType != null) {
        this.envTypes.put(path, builder.envType);
      }
      if (!builder.showGui) {
        this.noControls.add(path);
      }
      return outOption;
    };
  }

  @SuppressWarnings("unchecked")
  protected <T extends ConfigOption<?>, B extends RegistrationBuilderImpl<T, B>> BiFunction<T, Function<B, T>, B> getRegistrationBuilderFactory() {
    return (option, onCommit) -> {
      return (B) new RegistrationBuilderImpl<>(option, onCommit);
    };
  }

  protected <T extends ConfigOption<?>, B extends RegistrationBuilderImpl<T, B>> RegistrationBuilder<T> buildRegistration(
      T option
  ) {
    return new RegistrationBuilderImpl<T, B>(option, this.createCommitHandler(option));
  }

  @FunctionalInterface
  public interface UpdateCallback {
    void update();
  }

  @FunctionalInterface
  public interface RegistrationBuilder<T extends ConfigOption<?>> {
    T commit();
  }

  public static class RegistrationBuilderImpl<T extends ConfigOption<?>, B extends RegistrationBuilderImpl<T, B>> implements
      RegistrationBuilder<T> {
    protected final T option;
    protected final Function<B, T> onCommit;

    protected EnvType envType = null;
    protected boolean showGui = true;

    public RegistrationBuilderImpl(T option, Function<B, T> onCommit) {
      this.option = option;
      this.onCommit = onCommit;
    }

    @SuppressWarnings("unchecked")
    public B self() {
      return (B) this;
    }

    public B clientOnly() {
      this.envType = EnvType.CLIENT;
      return this.self();
    }

    public B serverOnly() {
      this.envType = EnvType.SERVER;
      return this.self();
    }

    public B noGuiControl() {
      this.showGui = false;
      return this.self();
    }

    @Override
    public T commit() {
      return this.onCommit.apply(this.self());
    }
  }
}
