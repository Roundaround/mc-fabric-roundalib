package me.roundaround.roundalib.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import me.roundaround.roundalib.PathAccessor;
import me.roundaround.roundalib.RoundaLib;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.panic.Panic;
import org.apache.logging.log4j.Logger;

import java.io.Serial;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public abstract class Config {
  protected final String modId;
  protected final int configVersion;
  protected final ConfigGroups groups;
  protected final ConfigGroups groupsForGui;
  protected final HashMap<ConfigPath, ConfigOption<?>> options = new HashMap<>();
  protected final HashSet<Consumer<Config>> updateListeners = new HashSet<>();

  protected int version;
  protected boolean isInitialized = false;

  protected Config(String modId) {
    this(modId, 1);
  }

  protected Config(String modId, int configVersion) {
    this.modId = modId;
    this.configVersion = configVersion;
    this.groups = new ConfigGroups();
    this.groupsForGui = new ConfigGroups();
  }

  public abstract boolean isActive();

  protected abstract void onInit();

  protected abstract Path getConfigDirectory();

  public final void init() {
    if (this.isInitialized) {
      return;
    }
    this.onInit();
    this.isInitialized = true;
  }

  public String getModId() {
    return this.modId;
  }

  public int getConfigVersion() {
    return this.configVersion;
  }

  public ConfigGroups getGroups() {
    return this.groups;
  }

  public ConfigGroups getGroupsForGui() {
    return this.groupsForGui;
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

  public void loadFromFile() {
    Path configPath = this.getConfigFile();
    if (configPath == null) {
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

  public void saveToFile() {
    Path configPath = this.getConfigFile();
    if (configPath == null) {
      return;
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

    this.groups.forEach((group, options) -> options.forEach(ConfigOption::commit));
  }

  public void update() {
    this.groups.forEach((group, options) -> options.forEach(ConfigOption::update));
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

  protected void runFirstLoad() {
    this.loadFromFile();
    this.saveToFile();

    this.update();
  }

  protected boolean updateConfigVersion(int version, com.electronwill.nightconfig.core.Config inMemoryConfigSnapshot) {
    return false;
  }

  protected <T extends ConfigOption<?>> T registerConfigOption(T option) {
    this.groups.add(option);
    this.options.put(option.getPath(), option);

    if (option.hasGuiControl()) {
      this.groupsForGui.add(option);
    }

    return option;
  }

  protected Path getConfigFile() {
    return PathAccessor.getInstance()
        .getConfigFile(this.getConfigDirectory(), this.modId, PathAccessor.ConfigFormat.TOML);
  }

  public static class ConfigGroups extends LinkedHashMap<String, ConfigGroup> {
    @Serial
    private static final long serialVersionUID = 6066982994690812870L;

    public boolean add(ConfigOption<?> option) {
      String groupId = option.getGroup();
      if (!this.containsKey(groupId)) {
        this.put(groupId, new ConfigGroup(groupId));
      }
      this.get(groupId).add(option);
      return true;
    }

    public void forEachGroup(Consumer<ConfigGroup> consumer) {
      this.forEach((groupId, group) -> consumer.accept(group));
    }

    public void forEachOption(Consumer<? super ConfigOption<?>> consumer) {
      this.forEach((groupId, group) -> group.forEach(consumer));
    }
  }

  public static class ConfigGroup extends ArrayList<ConfigOption<?>> {
    @Serial
    private static final long serialVersionUID = -5553233377025439167L;

    private final String groupId;

    public ConfigGroup(String groupId) {
      super();
      this.groupId = groupId;
    }

    public String getGroupId() {
      return this.groupId;
    }
  }
}
