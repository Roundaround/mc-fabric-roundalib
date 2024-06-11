package me.roundaround.roundalib.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import me.roundaround.roundalib.LoggerOutputStream;
import me.roundaround.roundalib.RoundaLib;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.panic.Panic;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.PrintStream;
import java.util.*;
import java.util.function.Consumer;

public abstract class ModConfig {
  private final String modId;
  private final int configVersion;
  private final String configScreenI18nKey;
  private final boolean showGroupTitles;
  private final LinkedHashMap<String, LinkedList<ConfigOption<?>>> configOptions = new LinkedHashMap<>();
  private final HashMap<String, ConfigOption<?>> configOptionsById = new HashMap<>();
  private final HashSet<Consumer<ModConfig>> updateListeners = new HashSet<>();

  private int version;

  protected ModConfig(String modId) {
    this(modId, options(modId));
  }

  protected ModConfig(String modId, OptionsBuilder options) {
    this.modId = modId;
    this.configVersion = options.configVersion;
    this.configScreenI18nKey = options.configScreenI18nKey;
    this.showGroupTitles = options.showGroupTitles;
  }

  public void init() {
    this.loadFromFile();
    this.saveToFile();

    this.update();
  }

  public String getModId() {
    return this.modId;
  }

  public int getConfigVersion() {
    return this.configVersion;
  }

  public String getConfigScreenI18nKey() {
    return this.configScreenI18nKey;
  }

  public boolean getShowGroupTitles() {
    return this.showGroupTitles;
  }

  public LinkedHashMap<String, LinkedList<ConfigOption<?>>> getConfigOptions() {
    return this.configOptions;
  }

  public ConfigOption<?> getById(String key) {
    return this.configOptionsById.get(key);
  }

  public boolean isDirty() {
    return this.configOptions.values().stream().anyMatch((group) -> group.stream().anyMatch(ConfigOption::isDirty));
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

    this.configOptions.forEach((group, options) -> options.forEach((option) -> {
      String key = group + "." + option.getId();
      Object data = fileConfig.get(key);
      if (data != null) {
        option.deserialize(data);
      }
    }));
  }

  public void saveToFile() {
    if (this.version == this.configVersion && !this.isDirty()) {
      RoundaLib.LOGGER.info("Skipping saving config to file because nothing has changed.");
      return;
    }

    CommentedFileConfig fileConfig = CommentedFileConfig.builder(this.getConfigFile()).preserveInsertionOrder().build();

    fileConfig.setComment("configVersion", " Config version is auto-generated\n DO NOT CHANGE");
    fileConfig.set("configVersion", this.configVersion);

    this.configOptions.forEach((group, options) -> options.forEach((option) -> {
      String key = group + "." + option.getId();

      List<String> comment = option.getComment();
      if (!comment.isEmpty()) {
        // Prefix each line with space to get "# This is a comment"
        fileConfig.setComment(key, " " + String.join("\n ", comment));
      }
      fileConfig.set(key, option.serialize());
    }));

    fileConfig.save();
    fileConfig.close();

    this.configOptions.forEach((group, options) -> options.forEach(ConfigOption::commit));
  }

  public void update() {
    this.configOptions.forEach((group, options) -> options.forEach(ConfigOption::update));
    this.updateListeners.forEach((listener) -> listener.accept(this));
  }

  public void subscribe(Consumer<ModConfig> listener) {
    this.updateListeners.add(listener);
  }

  public void unsubscribe(Consumer<ModConfig> listener) {
    this.updateListeners.remove(listener);
  }

  protected boolean updateConfigVersion(int version, Config config) {
    return false;
  }

  protected String getPath(String id) {
    return String.format("%s.%s", this.getModId(), id);
  }

  protected String getPath(String group, String id) {
    return String.format("%s.%s.%s", this.getModId(), group, id);
  }

  protected <T extends ConfigOption<?>> T registerConfigOption(T configOption) {
    String key = this.modId;
    if (configOption.getGroup() != null) {
      key += "." + configOption.getGroup();
    }

    if (!this.configOptions.containsKey(key)) {
      this.configOptions.put(key, new LinkedList<>());
    }
    this.configOptions.get(key).add(configOption);

    this.configOptionsById.put(key, configOption);

    return configOption;
  }

  public void panic(Panic panic) {
    this.panic(panic, RoundaLib.LOGGER);
  }

  @SuppressWarnings("SameParameterValue")
  public void panic(Panic panic, Logger logger) {
    String modId = this.getModId();

    Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modId);
    String modName = modContainer.map(container -> container.getMetadata().getName()).orElse(null);
    String issueTracker = modContainer.flatMap(container -> container.getMetadata().getContact().get("issues"))
        .orElse(null);

    logger.error(
        "The mod \"{}\" has encountered an unrecoverable error in its config setup due to a misuse of the RoundaLib " +
            "library. Please report this as a bug to the mod's developer.", modName == null ? modId : modName);

    if (issueTracker != null) {
      logger.error("Issue tracker: {}", issueTracker);
    }

    // Try and immediately throw in order to add this panic to the stack trace
    try {
      throw panic;
    } catch (Panic p) {
      p.printStackTrace(new PrintStream(new LoggerOutputStream(logger, Level.FATAL)));
    }

    System.exit(1);
  }

  private File getConfigDirectory() {
    File dir = FabricLoader.getInstance().getConfigDir().toFile();

    if (!dir.exists() && !dir.mkdirs()) {
      RoundaLib.LOGGER.warn("Failed to create config directory '{}'", dir.getAbsolutePath());
    }

    return dir;
  }

  private File getConfigFile() {
    return new File(this.getConfigDirectory(), this.modId + ".toml");
  }

  public static OptionsBuilder options(String modId) {
    return new OptionsBuilder(modId);
  }

  public static class OptionsBuilder {
    private int configVersion;
    private String configScreenI18nKey;
    private boolean showGroupTitles;

    private OptionsBuilder(String modId) {
      this.configVersion = 1;
      this.configScreenI18nKey = modId + ".config.title";
      this.showGroupTitles = true;
    }

    public OptionsBuilder setConfigVersion(int configVersion) {
      this.configVersion = configVersion;
      return this;
    }

    public OptionsBuilder setConfigScreenI18nKey(String configScreenI18nKey) {
      this.configScreenI18nKey = configScreenI18nKey;
      return this;
    }

    public OptionsBuilder setShowGroupTitles(boolean showGroupTitles) {
      this.showGroupTitles = showGroupTitles;
      return this;
    }

    public OptionsBuilder hideGroupTitles() {
      return this.setShowGroupTitles(false);
    }
  }
}
