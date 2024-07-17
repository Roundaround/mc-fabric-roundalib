package me.roundaround.roundalib.config.manage;

import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.ConnectedWorldContext;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class ModConfigImpl implements ModConfig {
  protected final String modId;
  protected final String configId;
  protected final int configVersion;
  protected final LinkedHashMap<String, ArrayList<ConfigOption<?>>> byGroup = new LinkedHashMap<>();
  protected final LinkedHashMap<ConfigPath, ConfigOption<?>> byPath = new LinkedHashMap<>();
  protected final HashMap<ConfigPath, EnvType> envType = new HashMap<>();
  protected final HashSet<ConfigPath> noGuiControl = new HashSet<>();
  protected final HashSet<ConfigPath> singlePlayerOnly = new HashSet<>();
  protected final List<Consumer<ModConfig>> listeners = new ArrayList<>();

  protected int storeSuppliedVersion;
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
    this.initializeStore();
    this.isInitialized = true;
  }

  protected abstract void registerOptions();

  @Override
  public void syncWithStore() {
    this.clear();
    this.registerOptions();
    ModConfig.super.syncWithStore();
  }

  @Override
  public boolean isInitialized() {
    return this.isInitialized;
  }

  @Override
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
  public void subscribe(Consumer<ModConfig> listener) {
    this.listeners.add(listener);
  }

  @Override
  public void unsubscribe(Consumer<ModConfig> listener) {
    this.listeners.remove(listener);
  }

  @Override
  public Collection<Consumer<ModConfig>> getListeners() {
    return Collections.unmodifiableList(this.listeners);
  }

  @Override
  public void clear() {
    this.byGroup.clear();
    this.byPath.clear();
    this.listeners.clear();
  }

  protected boolean isActive(ConfigOption<?> option) {
    ConfigPath path = option.getPath();

    EnvType envType = this.envType.get(path);
    if (envType != null && envType != FabricLoader.getInstance().getEnvironmentType()) {
      return false;
    }

    return !this.singlePlayerOnly.contains(path) || ConnectedWorldContext.isSinglePlayer();
  }

  protected boolean shouldShowGuiControl(ConfigOption<?> option) {
    if (!this.isActive(option)) {
      return false;
    }
    return !this.noGuiControl.contains(option.getPath());
  }

  @Override
  public List<ConfigOption<?>> getAll() {
    return this.byPath.values().stream().filter(this::isActive).toList();
  }

  protected Map<String, List<ConfigOption<?>>> getByGroup(Predicate<ConfigOption<?>> predicate) {
    LinkedHashMap<String, List<ConfigOption<?>>> map = new LinkedHashMap<>();
    this.byGroup.forEach((group, options) -> {
      List<ConfigOption<?>> filtered = options.stream().filter(predicate).toList();
      if (!filtered.isEmpty()) {
        map.put(group, filtered);
      }
    });
    return Collections.unmodifiableMap(map);
  }

  @Override
  public Map<String, List<ConfigOption<?>>> getByGroup() {
    return this.getByGroup(this::isActive);
  }

  @Override
  public Map<String, List<ConfigOption<?>>> getByGroupWithGuiControl() {
    return this.getByGroup(this::shouldShowGuiControl);
  }

  @Override
  public void setStoreSuppliedVersion(int version) {
    this.storeSuppliedVersion = version;
  }

  @Override
  public int getStoreSuppliedVersion() {
    return this.storeSuppliedVersion;
  }

  protected <T extends ConfigOption<?>> T register(T option) {
    option.setModId(this.modId);
    option.subscribePending((value) -> this.refresh());

    this.byGroup.computeIfAbsent(option.getGroup(), (group) -> new ArrayList<>()).add(option);
    this.byPath.put(option.getPath(), option);

    return option;
  }

  protected <T extends ConfigOption<?>> RegistrationBuilder<T> buildRegistration(T option) {
    return new RegistrationBuilder<>(option, (builder) -> {
      T outOption = this.register(option);
      ConfigPath path = outOption.getPath();
      if (builder.envType != null) {
        this.envType.put(path, builder.envType);
      }
      if (builder.noGuiControl) {
        this.noGuiControl.add(path);
      }
      if (builder.singlePlayerOnly) {
        this.singlePlayerOnly.add(path);
      }
      return outOption;
    });
  }

  public static class RegistrationBuilder<T extends ConfigOption<?>> {
    protected final T option;
    protected final Function<? super RegistrationBuilder<T>, T> onCommit;

    protected EnvType envType = null;
    protected boolean noGuiControl = false;
    protected boolean singlePlayerOnly = false;

    public RegistrationBuilder(T option, Function<? super RegistrationBuilder<T>, T> onCommit) {
      this.option = option;
      this.onCommit = onCommit;
    }

    public RegistrationBuilder<T> clientOnly() {
      this.envType = EnvType.CLIENT;
      return this;
    }

    public RegistrationBuilder<T> serverOnly() {
      this.envType = EnvType.SERVER;
      return this;
    }

    public RegistrationBuilder<T> noGuiControl() {
      this.noGuiControl = true;
      return this;
    }

    public RegistrationBuilder<T> singlePlayerOnly() {
      this.envType = EnvType.CLIENT;
      this.singlePlayerOnly = true;
      return this;
    }

    public T commit() {
      return this.onCommit.apply(this);
    }
  }
}
