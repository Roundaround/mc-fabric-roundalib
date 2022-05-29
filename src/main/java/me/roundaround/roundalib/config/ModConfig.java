package me.roundaround.roundalib.config;

import java.io.File;
import java.util.Optional;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.collect.ImmutableList;

import me.roundaround.roundalib.RoundaLibMod;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.util.ModInfo;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

public abstract class ModConfig {
  private final ModInfo modInfo;
  private final ImmutableList<ConfigOption<?, ?>> configOptions;

  private int previousConfigVersion;

  protected ModConfig(ModInfo modInfo, ImmutableList<ConfigOption<?, ?>> configOptions) {
    this.modInfo = modInfo;
    this.configOptions = configOptions;
  }

  public void init() {
    loadFromFile();
    saveToFile();
  }

  public void loadFromFile() {
    CommentedFileConfig fileConfig = CommentedFileConfig
        .builder(getConfigFile())
        .preserveInsertionOrder()
        .build();

    fileConfig.load();
    fileConfig.close();

    previousConfigVersion = fileConfig.getIntOrElse("configVersion", 1);
    // TODO: Upgrade versions as necessary.

    configOptions.forEach((configOption) -> {
      Object data = fileConfig.get(configOption.getId());
      if (data != null) {
        configOption.deserialize(data);
      }
    });
  }

  public void saveToFile() {
    if (previousConfigVersion == modInfo.getConfigVersion()
        && configOptions.stream().noneMatch(ConfigOption::isDirty)) {
      RoundaLibMod.LOGGER.info("Skipping saving config to file because nothing has changed.");
      return;
    }

    // TODO: Write own TOML file parsing/editing/saving so that we can make the
    // files prettier :)
    CommentedFileConfig fileConfig = CommentedFileConfig
        .builder(getConfigFile())
        .preserveInsertionOrder()
        .build();

    fileConfig.set("configVersion", modInfo.getConfigVersion());

    configOptions.forEach((configOption) -> {
      Optional<Text> comment = configOption.getComment().isPresent() ? configOption.getComment()
          : (configOption.getUseLabelAsCommentFallback() ? Optional.of(configOption.getLabel()) : Optional.empty());
      if (comment.isPresent()) {
        fileConfig.setComment(configOption.getId(), comment.get().getString());
      }
      fileConfig.set(configOption.getId(), configOption.getValue());
    });

    fileConfig.save();
    fileConfig.close();
  }

  public File getConfigDirectory() {
    File dir = FabricLoader.getInstance().getConfigDir().toFile();

    if (!dir.exists() && !dir.mkdirs()) {
      RoundaLibMod.LOGGER.warn("Failed to create config directory '{}'", dir.getAbsolutePath());
    }

    return dir;
  }

  public String getConfigFileName() {
    return modInfo.getModId() + ".toml";
  }

  public File getConfigFile() {
    return new File(getConfigDirectory(), getConfigFileName());
  }

  public ModInfo getModInfo() {
    return modInfo;
  }

  public ImmutableList<ConfigOption<?, ?>> getConfigOptions() {
    return configOptions;
  }
}
