package me.roundaround.roundalib.config.manage;

import me.roundaround.roundalib.PathAccessor;
import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.manage.store.FileBackedConfigStore;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.fabricmc.api.EnvType;
import net.minecraft.text.Text;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class ModConfigImpl implements ModConfig {
  protected final String modId;
  protected final String configId;
  protected final int configVersion;
  protected final LinkedHashMap<String, ArrayList<ConfigOption<?>>> byGroup = new LinkedHashMap<>();
  protected final LinkedHashMap<ConfigPath, ConfigOption<?>> byPath = new LinkedHashMap<>();
  protected final HashMap<ConfigPath, EnvType> envTypes = new HashMap<>();
  protected final HashMap<ConfigPath, Boolean> noControls = new HashMap<>();
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

  protected abstract void registerOptions();

  public final void init() {
    if (this.isInitialized) {
      return;
    }
    this.onInit();
    this.isInitialized = true;
  }

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

  public void updateListeners() {
    this.byGroup.forEachOption(ConfigOption::update);
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

  protected void onInit() {
    this.initializeStore();
  }

  @Override
  public void clear() {
    this.byGroup.clear();
    this.byPath.clear();
    this.listeners.clear();
  }

  protected boolean updateConfigVersion(int version, com.electronwill.nightconfig.core.Config inMemoryConfigSnapshot) {
    return false;
  }

  protected <T extends ConfigOption<?>> T register(T option) {
    return this.register(option, null, null);
  }

  protected <T extends ConfigOption<?>> T register(T option, Predicate<T> storagePredicate, Predicate<T> guiPredicate) {
    option.setModId(this.modId);
    option.subscribePending((value) -> {
      this.updateListeners();
    });

    this.byGroup.add(option, storagePredicate, guiPredicate);
    this.byPath.put(option.getPath(), option);

    return option;
  }

  public static class ConfigGroups {
    private final LinkedHashMap<String, ConfigGroup> store = new LinkedHashMap<>();

    public <T extends ConfigOption<?>> boolean add(T option, Predicate<T> storagePredicate, Predicate<T> guiPredicate) {
      String groupId = option.getGroup();
      if (!this.store.containsKey(groupId)) {
        this.store.put(groupId, new ConfigGroup(groupId));
      }
      this.store.get(groupId).add(option, storagePredicate, guiPredicate);
      return true;
    }

    public void clear() {
      this.store.clear();
    }

    public void forEachGroup(Consumer<ConfigGroup> consumer) {
      this.store.forEach((groupId, group) -> consumer.accept(group));
    }

    public void forEachOption(Consumer<? super ConfigOption<?>> consumer) {
      this.store.forEach((groupId, group) -> group.forEach(consumer));
    }

    public Map<String, List<ConfigOption<?>>> all() {
      LinkedHashMap<String, List<ConfigOption<?>>> map = new LinkedHashMap<>(this.store.size());
      for (var entry : this.store.entrySet()) {
        map.put(entry.getKey(), entry.getValue().all())
      } return map;
    }
  }

  public static class ConfigGroup {
    private final ArrayList<ConfigOptionWithPredicates<?>> store = new ArrayList<>();
    private final String groupId;

    public ConfigGroup(String groupId) {
      this.groupId = groupId;
    }

    public String getGroupId() {
      return this.groupId;
    }

    public void clear() {
      this.store.clear();
    }

    public <T extends ConfigOption<?>> boolean add(T option, Predicate<T> storagePredicate, Predicate<T> guiPredicate) {
      return this.store.add(new ConfigOptionWithPredicates<>(option, storagePredicate, guiPredicate));
    }

    public void forEach(Consumer<? super ConfigOption<?>> consumer) {
      this.store.forEach(ConfigOptionWithPredicates::get);
    }

    public List<? extends ConfigOption<?>> all() {
      return this.store.stream().map(ConfigOptionWithPredicates::get).toList();
    }

    public List<? extends ConfigOption<?>> forStorage() {
      return this.store.stream()
          .filter(ConfigOptionWithPredicates::shouldLoadAndStore)
          .map(ConfigOptionWithPredicates::get)
          .toList();
    }

    public List<? extends ConfigOption<?>> forGui() {
      return this.store.stream()
          .filter(ConfigOptionWithPredicates::shouldShowGuiControl)
          .map(ConfigOptionWithPredicates::get)
          .toList();
    }
  }

  public static class ConfigOptionWithPredicates<T extends ConfigOption<?>> {
    private final T option;
    private final Predicate<T> storagePredicate;
    private final Predicate<T> guiPredicate;

    public ConfigOptionWithPredicates(
        T option, Predicate<T> storagePredicate, Predicate<T> guiPredicate
    ) {
      this.option = option;
      this.storagePredicate = storagePredicate == null ? always() : storagePredicate;
      this.guiPredicate = guiPredicate == null ? always() : guiPredicate;
    }

    public boolean shouldLoadAndStore() {
      return this.storagePredicate.test(this.option);
    }

    public boolean shouldShowGuiControl() {
      return this.guiPredicate.test(this.option);
    }

    public T get() {
      return this.option;
    }

    public Optional<T> getForStorage() {
      if (!this.storagePredicate.test(this.option)) {
        return Optional.empty();
      }
      return Optional.of(this.option);
    }

    public Optional<T> getForGui() {
      if (!this.guiPredicate.test(this.option)) {
        return Optional.empty();
      }
      return Optional.of(this.option);
    }

    private static <T> Predicate<T> always() {
      return (value) -> true;
    }
  }

  @FunctionalInterface
  public interface UpdateCallback {
    void update();
  }
}
