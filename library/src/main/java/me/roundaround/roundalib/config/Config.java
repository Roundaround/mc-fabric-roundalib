package me.roundaround.roundalib.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import me.roundaround.roundalib.PathAccessor;
import me.roundaround.roundalib.RoundaLib;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.panic.Panic;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public abstract class Config {
  protected final String modId;
  protected final String configId;
  protected final int configVersion;
  protected final ConfigGroups groups;
  protected final HashMap<ConfigPath, ConfigOption<?>> options = new HashMap<>();
  protected final HashSet<Consumer<Config>> updateListeners = new HashSet<>();

  protected int version;
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
    this.groups = new ConfigGroups();
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
    return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
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

  public ConfigGroups getGroups() {
    return this.groups;
  }

  public ConfigOption<?> getByPath(String path) {
    return this.getByPath(ConfigPath.parse(path));
  }

  public ConfigOption<?> getByPath(ConfigPath path) {
    return this.options.get(path);
  }

  public boolean isDirty() {
    return this.options.values().stream().anyMatch(ConfigOption::isDirty);
  }

  public void readFile() {
    Path configPath = this.getConfigFile();
    if (configPath == null || Files.notExists(configPath)) {
      return;
    }

    CommentedFileConfig fileConfig = CommentedFileConfig.builder(configPath).preserveInsertionOrder().build();

    fileConfig.load();
    fileConfig.close();

    this.version = fileConfig.getIntOrElse("configVersion", -1);
    CommentedConfig config = CommentedConfig.copy(fileConfig);
    if (this.updateConfigVersion(this.version, config)) {
      fileConfig.putAll(config);
    }

    this.groups.forEachOption((option) -> {
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

    if (this.version == this.configVersion && !this.isDirty()) {
      RoundaLib.LOGGER.info("Skipping saving {} config to file because nothing has changed.", this.getModId());
      return;
    }

    CommentedFileConfig fileConfig = CommentedFileConfig.builder(configPath).preserveInsertionOrder().build();

    fileConfig.setComment("configVersion", " Config version is auto-generated\n DO NOT CHANGE");
    fileConfig.set("configVersion", this.configVersion);

    this.groups.forEachOption((option) -> {
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

    this.groups.forEachOption(ConfigOption::commit);
  }

  public void updateListeners() {
    this.groups.forEachOption(ConfigOption::update);
    this.updateListeners.forEach((listener) -> listener.accept(this));
  }

  public void subscribe(Consumer<Config> listener) {
    this.updateListeners.add(listener);
  }

  public void unsubscribe(Consumer<Config> listener) {
    this.updateListeners.remove(listener);
  }

  public void panic(Panic panic) {
    this.panic(panic, RoundaLib.LOGGER);
  }

  public void panic(Panic panic, Logger logger) {
    Panic.panic(panic, this.modId, logger);
  }

  protected void onInit() {
    this.syncWithFile();
  }

  protected void syncWithFile() {
    this.version = -1;
    this.registerOptions();
    this.readFile();
    this.writeToFile();
    this.updateListeners();
  }

  public void clear() {
    this.groups.clear();
    this.options.clear();
    this.updateListeners.clear();
  }

  protected boolean updateConfigVersion(int version, com.electronwill.nightconfig.core.Config inMemoryConfigSnapshot) {
    return false;
  }

  protected <T extends ConfigOption<?>> T register(T option) {
    return this.register(option, (Collection<GuiContext>) null);
  }

  protected <T extends ConfigOption<?>> T register(T option, GuiContext... allowedContexts) {
    return this.register(option, Set.of(allowedContexts));
  }

  protected <T extends ConfigOption<?>> T register(T option, Collection<GuiContext> allowedContexts) {
    option.setModId(this.modId);
    option.subscribePending((value) -> {
      this.updateListeners();
    });

    this.groups.add(option, allowedContexts);
    this.options.put(option.getPath(), option);

    return option;
  }

  protected Path getConfigFile() {
    return PathAccessor.getInstance()
        .getConfigFile(this.getConfigDirectory(), this.modId, PathAccessor.ConfigFormat.TOML);
  }

  public static class ConfigGroups {
    private final LinkedHashMap<String, ConfigGroup> store = new LinkedHashMap<>();

    public boolean add(ConfigOption<?> option) {
      return this.add(option, null);
    }

    public boolean add(ConfigOption<?> option, Collection<GuiContext> allowedContexts) {
      String groupId = option.getGroup();
      if (!this.store.containsKey(groupId)) {
        this.store.put(groupId, new ConfigGroup(groupId));
      }
      this.store.get(groupId).add(option, allowedContexts);
      return true;
    }

    public void clear() {
      this.store.clear();
    }

    public void forEachGroup(Consumer<ConfigGroup> consumer) {
      this.store.forEach((groupId, group) -> consumer.accept(group));
    }

    public void forEachGroup(Consumer<ConfigGroup> consumer, Collection<GuiContext> contexts) {
      this.store.forEach((groupId, group) -> {
        if (!group.isEmpty(contexts)) {
          consumer.accept(group);
        }
      });
    }

    public void forEachOption(Consumer<? super ConfigOption<?>> consumer) {
      this.store.forEach((groupId, group) -> group.forEach(consumer));
    }

    public void forEachOption(Consumer<? super ConfigOption<?>> consumer, Collection<GuiContext> contexts) {
      this.store.forEach((groupId, group) -> group.forEach(consumer, contexts));
    }

    public boolean isEmpty() {
      return this.store.isEmpty();
    }

    public boolean isEmpty(Collection<GuiContext> contexts) {
      return this.store.values().stream().allMatch((group) -> group.isEmpty(contexts));
    }

    public int size() {
      return this.store.size();
    }

    public int size(Collection<GuiContext> contexts) {
      return (int) this.store.values().stream().filter((group) -> !group.isEmpty(contexts)).count();
    }
  }

  public static class ConfigGroup {
    private final ArrayList<ContextualConfigOption<?>> store = new ArrayList<>();
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

    public boolean add(ConfigOption<?> option) {
      return this.add(option, null);
    }

    public boolean add(ConfigOption<?> option, Collection<GuiContext> allowedContexts) {
      return this.store.add(new ContextualConfigOption<>(option, allowedContexts));
    }

    public void forEach(Consumer<? super ConfigOption<?>> consumer) {
      this.store.forEach(ContextualConfigOption::get);
    }

    public void forEach(Consumer<? super ConfigOption<?>> consumer, Collection<GuiContext> contexts) {
      this.store.forEach((entry) -> entry.get(contexts).ifPresent(consumer));
    }

    public boolean isEmpty() {
      return this.store.isEmpty();
    }

    public boolean isEmpty(Collection<GuiContext> contexts) {
      return this.store.stream().noneMatch((entry) -> entry.matchesContext(contexts));
    }

    public int size() {
      return this.store.size();
    }

    public int size(Collection<GuiContext> contexts) {
      return (int) this.store.stream().filter((entry) -> entry.matchesContext(contexts)).count();
    }
  }

  public static class ContextualConfigOption<T> {
    private final ConfigOption<T> option;
    private final Set<GuiContext> allowedContexts;

    public ContextualConfigOption(ConfigOption<T> option) {
      this(option, null);
    }

    public ContextualConfigOption(ConfigOption<T> option, Collection<GuiContext> allowedContexts) {
      this.option = option;
      this.allowedContexts = allowedContexts == null || allowedContexts.isEmpty() ?
          Set.of(GuiContext.ALWAYS) :
          Set.copyOf(allowedContexts);
    }

    public boolean matchesContext(Collection<GuiContext> contexts) {
      return this.allowedContexts.stream().anyMatch(contexts::contains);
    }

    public Optional<ConfigOption<T>> get(Collection<GuiContext> contexts) {
      if (!this.matchesContext(contexts)) {
        return Optional.empty();
      }
      return Optional.of(this.option);
    }

    public ConfigOption<T> get() {
      return this.option;
    }
  }

  public enum GuiContext {
    ALWAYS, NOT_IN_GAME, INTEGRATED_SERVER, DEDICATED_SERVER, NEVER
  }

  @FunctionalInterface
  public interface UpdateCallback {
    void update();
  }
}
