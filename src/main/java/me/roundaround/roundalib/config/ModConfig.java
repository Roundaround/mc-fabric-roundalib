package me.roundaround.roundalib.config;

import java.io.File;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.collect.ImmutableList;

import me.roundaround.roundalib.RoundaLibMod;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.util.ModInfo;
import net.fabricmc.loader.api.FabricLoader;

public abstract class ModConfig {
  private final ModInfo modInfo;
  private final HashMap<String, ConfigOption<?, ?>> configOptions = new HashMap<>();

  private int previousConfigVersion;

  protected ModConfig(ModInfo modInfo) {
    this.modInfo = modInfo;

    this.configOptions.putAll(
        this.getConfigOptions().stream()
            .collect(Collectors.toMap(ConfigOption::getId, Function.identity())));
  }

  public abstract ImmutableList<ConfigOption<?, ?>> getConfigOptions();

  public void init() {
    this.loadFromFile();
    this.saveToFile();
  }

  public void loadFromFile() {
    // TODO: Migrate to TOML: https://github.com/TheElectronWill/Night-Config
    CommentedFileConfig fileConfig = CommentedFileConfig.builder(this.getConfigFile(".toml")).concurrent().build();
    fileConfig.load();

    previousConfigVersion = fileConfig.getIntOrElse("config_version", 1);
    // TODO: Upgrade versions as necessary.

    this.configOptions.values().forEach((configOption) -> {
      Object data = fileConfig.get(configOption.getId());
      if (data != null) {
        configOption.deserialize(data);
      }
    });
  }

  public void saveToFile() {
    if (previousConfigVersion == this.modInfo.getConfigVersion()
        && this.configOptions.values().stream().noneMatch(ConfigOption::isDirty)) {
      RoundaLibMod.LOGGER.info("Skipping saving config to file because nothing has changed.");
      return;
    }

    CommentedFileConfig fileConfig = CommentedFileConfig.builder(this.getConfigFile(".toml")).concurrent().build();
    this.configOptions.values().forEach((configOption) -> {
      fileConfig.setComment(configOption.getId(), configOption.getLabel().getString());
      fileConfig.set(configOption.getId(), configOption.getValue());
    });
    fileConfig.save();
  }

  public File getConfigDirectory() {
    File dir = FabricLoader.getInstance().getConfigDir().toFile();

    if (!dir.exists() && !dir.mkdirs()) {
      RoundaLibMod.LOGGER.warn("Failed to create config directory '{}'", dir.getAbsolutePath());
    }

    return dir;
  }

  public String getConfigFileName(String extension) {
    return this.modInfo.getModId() + extension;
  }

  public File getConfigFile() {
    return getConfigFile(".json");
  }

  public File getConfigFile(String extension) {
    return new File(this.getConfigDirectory(), this.getConfigFileName(extension));
  }

  public ModInfo getModInfo() {
    return this.modInfo;
  }
}
