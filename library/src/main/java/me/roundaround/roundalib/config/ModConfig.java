package me.roundaround.roundalib.config;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import me.roundaround.roundalib.RoundaLib;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.fabricmc.loader.api.FabricLoader;

public abstract class ModConfig {
  private final String modId;
  private final int configVersion;
  private final String configScreenI18nKey;
  private final boolean showGroupTitles;
  private final LinkedHashMap<String, LinkedList<ConfigOption<?, ?>>> configOptions = new LinkedHashMap<>();

  private int version;

  protected ModConfig(String modId) {
    this(modId, options(modId));
  }

  protected ModConfig(String modId, OptionsBuilder options) {
    this.modId = modId;
    configVersion = options.configVersion;
    configScreenI18nKey = options.configScreenI18nKey;
    showGroupTitles = options.showGroupTitles;
  }

  public void init() {
    loadFromFile();
    saveToFile();
  }

  public String getModId() {
    return modId;
  }

  public int getConfigVersion() {
    return configVersion;
  }

  public String getConfigScreenI18nKey() {
    return configScreenI18nKey;
  }

  public boolean getShowGroupTitles() {
    return showGroupTitles;
  }

  public LinkedHashMap<String, LinkedList<ConfigOption<?, ?>>> getConfigOptions() {
    return configOptions;
  }

  public void loadFromFile() {
    CommentedFileConfig fileConfig = CommentedFileConfig
        .builder(getConfigFile())
        .preserveInsertionOrder()
        .build();

    fileConfig.load();
    fileConfig.close();

    version = fileConfig.getIntOrElse("configVersion", -1);
    CommentedConfig config = CommentedConfig.copy(fileConfig);
    if (updateConfigVersion(version, config)) {
      fileConfig.putAll(config);
    }

    configOptions.entrySet().forEach((entry) -> {
      entry.getValue().forEach((configOption) -> {
        String key = entry.getKey() + "." + configOption.getId();
        Object data = fileConfig.get(key);
        if (data != null) {
          configOption.deserialize(data);
        }
      });
    });
  }

  public void saveToFile() {
    if (version == configVersion && !isDirty()) {
      RoundaLib.LOGGER.info("Skipping saving config to file because nothing has changed.");
      return;
    }

    CommentedFileConfig fileConfig = CommentedFileConfig
        .builder(getConfigFile())
        .preserveInsertionOrder()
        .build();

    fileConfig.setComment("configVersion", " Config version is auto-generated\n DO NOT CHANGE");
    fileConfig.set("configVersion", configVersion);

    configOptions.entrySet().forEach((entry) -> {
      entry.getValue().forEach((configOption) -> {
        String key = entry.getKey() + "." + configOption.getId();

        List<String> comment = configOption.getComment();
        if (!comment.isEmpty()) {
          // Prefix each line with space to get "# This is a comment"
          fileConfig.setComment(key, " " + String.join("\n ", comment));
        }
        fileConfig.set(key, configOption.serialize());
      });
    });

    fileConfig.save();
    fileConfig.close();
  }

  protected boolean updateConfigVersion(int version, Config config) {
    return false;
  }

  protected <T extends ConfigOption<?, ?>> T registerConfigOption(T configOption) {
    return registerConfigOption(null, configOption);
  }

  protected <T extends ConfigOption<?, ?>> T registerConfigOption(String group, T configOption) {
    String key = modId;
    if (group != null) {
      key += "." + group;
    }

    if (!configOptions.containsKey(key)) {
      configOptions.put(key, new LinkedList<>());
    }
    configOptions.get(key).add(configOption);
    return configOption;
  }

  private File getConfigDirectory() {
    File dir = FabricLoader.getInstance().getConfigDir().toFile();

    if (!dir.exists() && !dir.mkdirs()) {
      RoundaLib.LOGGER.warn("Failed to create config directory '{}'", dir.getAbsolutePath());
    }

    return dir;
  }

  private File getConfigFile() {
    return new File(getConfigDirectory(), modId + ".toml");
  }

  private boolean isDirty() {
    return configOptions.values().stream().anyMatch((group) -> {
      return group.stream().anyMatch(ConfigOption::isDirty);
    });
  }

  public static OptionsBuilder options(String modId) {
    return new OptionsBuilder(modId);
  }

  public static class OptionsBuilder {
    private int configVersion;
    private String configScreenI18nKey;
    private boolean showGroupTitles;

    private OptionsBuilder(String modId) {
      configVersion = 1;
      configScreenI18nKey = modId + ".config.title";
      showGroupTitles = true;
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
      return setShowGroupTitles(false);
    }
  }
}
