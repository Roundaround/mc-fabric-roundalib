package me.roundaround.roundalib.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import me.roundaround.roundalib.RoundaLib;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.panic.Panic;
import org.apache.logging.log4j.Logger;

import java.io.Serial;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public abstract class ModConfig {
  protected final String modId;
  protected final int configVersion;
  protected final boolean prefixPaths;
  protected final ConfigGroups groups;
  protected final ConfigGroups groupsForGui;
  protected final HashMap<String, ConfigOption<?>> options = new HashMap<>();
  protected final HashSet<Consumer<ModConfig>> updateListeners = new HashSet<>();

  protected int version;

  protected ModConfig(String modId) {
    this(modId, 1);
  }

  protected ModConfig(String modId, int configVersion) {
    this(modId, configVersion, false);
  }

  protected ModConfig(String modId, int configVersion, boolean prefixPaths) {
    this.modId = modId;
    this.configVersion = configVersion;
    this.prefixPaths = prefixPaths;
    this.groups = new ConfigGroups(this.modId);
    this.groupsForGui = new ConfigGroups(this.modId);
  }

  public abstract void init();

  protected abstract Path getConfigDirectory();

  public String getModId() {
    return this.modId;
  }

  public int getConfigVersion() {
    return this.configVersion;
  }

  public String getPath(String group) {
    if (!this.prefixPaths) {
      return group;
    }

    String path = this.modId;
    if (group != null && !group.isBlank()) {
      path += "." + group;
    }
    return path;
  }

  public String getPath(String group, String id) {
    String basePath = this.getPath(group);
    if (basePath == null || basePath.isBlank()) {
      return id;
    }
    return basePath + "." + id;
  }

  public ConfigGroups getGroups() {
    return this.groups;
  }

  public ConfigGroups getGroupsForGui() {
    return this.groupsForGui;
  }

  public ConfigOption<?> getByPath(String path) {
    return this.options.get(path);
  }

  public boolean isDirty() {
    return this.groups.values().stream().anyMatch((group) -> group.stream().anyMatch(ConfigOption::isDirty));
  }

  public void loadFromFile() {
    CommentedFileConfig fileConfig = CommentedFileConfig.builder(this.getConfigFile()).preserveInsertionOrder().build();

    fileConfig.load();
    fileConfig.close();

    this.version = fileConfig.getIntOrElse("configVersion", -1);
    CommentedConfig config = CommentedConfig.copy(fileConfig);
    if (this.updateConfigVersion(this.version, config)) {
      fileConfig.putAll(config);
    }

    this.groups.forEachOption((option) -> {
      Object data = fileConfig.get(option.getPath());
      if (data != null) {
        option.deserialize(data);
      }
    });
  }

  public void saveToFile() {
    if (this.version == this.configVersion && !this.isDirty()) {
      RoundaLib.LOGGER.info("Skipping saving {} config to file because nothing has changed.", this.getModId());
      return;
    }

    CommentedFileConfig fileConfig = CommentedFileConfig.builder(this.getConfigFile()).preserveInsertionOrder().build();

    fileConfig.setComment("configVersion", " Config version is auto-generated\n DO NOT CHANGE");
    fileConfig.set("configVersion", this.configVersion);

    this.groups.forEachOption((option) -> {
      String path = option.getPath();
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

  public void subscribe(Consumer<ModConfig> listener) {
    this.updateListeners.add(listener);
  }

  public void unsubscribe(Consumer<ModConfig> listener) {
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

  protected boolean updateConfigVersion(int version, Config config) {
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
    return this.getConfigDirectory().resolve(this.modId + ".toml");
  }

  public static class ConfigGroups extends LinkedHashMap<String, ConfigGroup> {
    @Serial
    private static final long serialVersionUID = 6066982994690812870L;

    private final String modId;

    public ConfigGroups(String modId) {
      super();
      this.modId = modId;
    }

    public boolean add(ConfigOption<?> option) {
      String key = this.getCategorizationKey(option);
      if (!this.containsKey(key)) {
        this.put(key, new ConfigGroup(key));
      }
      this.get(key).add(option);
      return true;
    }

    public void forEachGroup(Consumer<ConfigGroup> consumer) {
      this.forEach((key, group) -> consumer.accept(group));
    }

    public void forEachOption(Consumer<? super ConfigOption<?>> consumer) {
      this.forEach((key, group) -> group.forEach(consumer));
    }

    private String getCategorizationKey(ConfigOption<?> option) {
      String key = this.modId;
      if (option.getGroup() != null) {
        key += "." + option.getGroup();
      }
      return key;
    }
  }

  public static class ConfigGroup extends ArrayList<ConfigOption<?>> {
    @Serial
    private static final long serialVersionUID = -5553233377025439167L;

    private final String id;

    public ConfigGroup(String id) {
      super();
      this.id = id;
    }

    public String getId() {
      return this.id;
    }
  }
}
