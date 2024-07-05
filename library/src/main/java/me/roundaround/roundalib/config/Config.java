package me.roundaround.roundalib.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import me.roundaround.roundalib.PathAccessor;
import me.roundaround.roundalib.RoundaLib;
import me.roundaround.roundalib.config.manage.store.ConfigStore;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Config implements ConfigStore {
  protected final String modId;
  protected final String configId;
  protected final int configVersion;
  protected final LinkedHashMap<String, ArrayList<ConfigOption<?>>> byGroup = new LinkedHashMap<>();
  protected final LinkedHashMap<ConfigPath, ConfigOption<?>> byPath = new LinkedHashMap<>();
  protected final HashMap<ConfigPath, Predicate<ConfigOption<?>>> storagePredicates = new HashMap<>();
  protected final HashMap<ConfigPath, Predicate<ConfigOption<?>>> guiPredicates = new HashMap<>();
  protected final HashSet<Consumer<Config>> listeners = new HashSet<>();

  protected int versionFromFile;
  protected boolean isInitialized = false;

  protected Config(String modId) {
    this(modId, 1);
  }

  protected Config(String modId, String configId) {
    this(modId, configId, 1);
  }

  protected Config(String modId, int configVersion) {
    this(modId, modId, configVersion);
  }

  protected Config(String modId, String configId, int configVersion) {
    this.modId = modId;
    this.configId = configId;
    this.configVersion = configVersion;
  }

  protected abstract Path getConfigDirectory();

  protected abstract void registerOptions();

  public final void init() {
    if (this.isInitialized) {
      return;
    }
    this.onInit();
    this.isInitialized = true;
  }

  public boolean canShowInGui() {
    return this.isInitialized && FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
  }

  public Text getLabel() {
    return Text.translatable(this.getModId() + "." + this.getConfigId() + ".title");
  }

  public String getModId() {
    return this.modId;
  }

  public String getConfigId() {
    return this.configId;
  }

  public int getConfigVersion() {
    return this.configVersion;
  }

  public Map<String, List<ConfigOption<?>>> getGroupedForGui() {
    return this.getGroupedAndFiltered(new HashMap<>());
  }

  public Map<String, List<ConfigOption<?>>> getGroupedForStorage() {
    return this.getGroupedAndFiltered(this.storagePredicates);
  }

  private Map<String, List<ConfigOption<?>>> getGroupedAndFiltered(HashMap<ConfigPath, Predicate<ConfigOption<?>>> predicateLookup) {
    LinkedHashMap<String, List<ConfigOption<?>>> map = new LinkedHashMap<>();
    this.byGroup.forEach((group, options) -> {
      List<ConfigOption<?>> filtered = options.stream()
          .filter((option) -> predicateLookup.computeIfAbsent(option.getPath(), (p) -> (o) -> true).test(option))
          .toList();
      if (!filtered.isEmpty()) {
        map.put(group, filtered);
      }
    });
    return Collections.unmodifiableMap(map);
  }

  public ConfigOption<?> getByPath(String path) {
    return this.getByPath(ConfigPath.parse(path));
  }

  public ConfigOption<?> getByPath(ConfigPath path) {
    return this.byPath.get(path);
  }

  public boolean isDirty() {
    return this.byPath.values().stream().anyMatch(ConfigOption::isDirty);
  }

  public void readFile() {
    Path configPath = this.getConfigFile();
    if (configPath == null || Files.notExists(configPath)) {
      return;
    }

    CommentedFileConfig fileConfig = CommentedFileConfig.builder(configPath).preserveInsertionOrder().build();

    fileConfig.load();
    fileConfig.close();

    this.versionFromFile = fileConfig.getIntOrElse("configVersion", -1);
    CommentedConfig config = CommentedConfig.copy(fileConfig);
    if (this.updateConfigVersion(this.versionFromFile, config)) {
      fileConfig.putAll(config);
    }

    this.byGroup.forEachOption((option) -> {
      Object data = fileConfig.get(option.getPath().toString());
      if (data != null) {
        option.deserialize(data);
      }
    });
  }

  public void writeToFile() {
    Path configPath = this.getConfigFile();
    if (configPath == null) {
      return;
    }

    if (Files.notExists(configPath)) {
      try {
        Files.createDirectories(configPath.getParent());
        Files.createFile(configPath);
      } catch (IOException e) {
        RoundaLib.LOGGER.error("Failed to create config file or its directory tree: {}", configPath.toAbsolutePath());
        return;
      }
    }

    if (this.versionFromFile == this.configVersion && !this.isDirty()) {
      RoundaLib.LOGGER.info("Skipping saving {} config to file because nothing has changed.", this.getModId());
      return;
    }

    CommentedFileConfig fileConfig = CommentedFileConfig.builder(configPath).preserveInsertionOrder().build();

    fileConfig.setComment("configVersion", " Config version is auto-generated\n DO NOT CHANGE");
    fileConfig.set("configVersion", this.configVersion);

    this.byGroup.forEachOption((option) -> {
      String path = option.getPath().toString();
      List<String> comment = option.getComment();
      if (!comment.isEmpty()) {
        // Prefix each line with space to get "# This is a comment"
        fileConfig.setComment(path, " " + String.join("\n ", comment));
      }
      fileConfig.set(path, option.serialize());
    });

    fileConfig.save();
    fileConfig.close();

    this.byGroup.forEachOption(ConfigOption::commit);
  }

  public void updateListeners() {
    this.byGroup.forEachOption(ConfigOption::update);
    this.listeners.forEach((listener) -> listener.accept(this));
  }

  public void subscribe(Consumer<Config> listener) {
    this.listeners.add(listener);
  }

  public void unsubscribe(Consumer<Config> listener) {
    this.listeners.remove(listener);
  }

  protected void onInit() {
    this.syncWithFile();
  }

  protected void syncWithFile() {
    this.versionFromFile = -1;
    this.registerOptions();
    this.readFile();
    this.writeToFile();
    this.updateListeners();
  }

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

  protected Path getConfigFile() {
    return PathAccessor.getInstance()
        .getConfigFile(this.getConfigDirectory(), this.modId, PathAccessor.ConfigFormat.TOML);
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
