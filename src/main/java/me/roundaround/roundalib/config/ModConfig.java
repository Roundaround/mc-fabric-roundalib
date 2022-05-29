package me.roundaround.roundalib.config;

import java.io.File;
import java.util.Optional;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import me.roundaround.roundalib.RoundaLibMod;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.util.ModInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public abstract class ModConfig {
  private final ModInfo modInfo;
  private final ImmutableMap<String, ImmutableList<ConfigOption<?, ?>>> configOptions;
  private final IdentifiableResourceReloadListener reloadListener = new SimpleSynchronousResourceReloadListener() {
    @Override
    public Identifier getFabricId() {
      return new Identifier(modInfo.getModId(), modInfo.getModId() + "_reload");
    }

    @Override
    public void reload(ResourceManager manager) {
      saveToFile();
    }
  };

  private int version;

  // TODO: Make `ConfigGroup extends ImmutableMap<String, ImmutableList<>>`
  protected ModConfig(ModInfo modInfo, ImmutableList<ConfigOption<?, ?>> configOptions) {
    this.modInfo = modInfo;
    this.configOptions = ImmutableMap.of(modInfo.getModId(), configOptions);
  }

  protected boolean updateConfigVersion(int version, Config config) {
    return false;
  }

  public void init() {
    loadFromFile();

    // Wait for all resources (including i18n lang files) to finish loading,
    // then attempt to save the config to file.
    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
      ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(reloadListener);
    } else {
      ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(reloadListener);
    }
  }

  public ModInfo getModInfo() {
    return modInfo;
  }

  public ImmutableMap<String, ImmutableList<ConfigOption<?, ?>>> getConfigOptions() {
    return configOptions;
  }

  public ImmutableList<ConfigOption<?, ?>> getConfigOptionsAsFlatList() {
    return configOptions.values()
        .stream()
        .flatMap(ImmutableList<ConfigOption<?, ?>>::stream)
        .collect(ImmutableList.toImmutableList());
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
    if (version == modInfo.getConfigVersion() && !isDirty()) {
      RoundaLibMod.LOGGER.info("Skipping saving config to file because nothing has changed.");
      return;
    }

    // TODO: Write own TOML file parsing/editing/saving so that we can make the
    // files prettier :)
    CommentedFileConfig fileConfig = CommentedFileConfig
        .builder(getConfigFile())
        .preserveInsertionOrder()
        .build();

    fileConfig.setComment("configVersion", new TranslatableText("config.version_comment").getString());
    fileConfig.set("configVersion", modInfo.getConfigVersion());

    configOptions.entrySet().forEach((entry) -> {
      entry.getValue().forEach((configOption) -> {
        String key = entry.getKey() + "." + configOption.getId();

        Optional<Text> comment = Optional.empty();
        if (configOption.getComment().isPresent()) {
          comment = configOption.getComment();
        } else if (configOption.getUseLabelAsCommentFallback()) {
          comment = Optional.of(configOption.getLabel());
        }

        if (comment.isPresent()) {
          fileConfig.setComment(key, comment.get().getString());
        }
        fileConfig.set(key, configOption.getValue());
      });
    });

    fileConfig.save();
    fileConfig.close();
  }

  private File getConfigDirectory() {
    File dir = FabricLoader.getInstance().getConfigDir().toFile();

    if (!dir.exists() && !dir.mkdirs()) {
      RoundaLibMod.LOGGER.warn("Failed to create config directory '{}'", dir.getAbsolutePath());
    }

    return dir;
  }

  private File getConfigFile() {
    return new File(getConfigDirectory(), modInfo.getModId() + ".toml");
  }

  private boolean isDirty() {
    return configOptions.values().stream().anyMatch((group) -> {
      return group.stream().anyMatch(ConfigOption::isDirty);
    });
  }
}
