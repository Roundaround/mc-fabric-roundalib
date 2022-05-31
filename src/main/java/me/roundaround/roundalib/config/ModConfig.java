package me.roundaround.roundalib.config;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Optional;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;

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
  private final LinkedHashMap<String, LinkedList<ConfigOption<?, ?>>> configOptions = new LinkedHashMap<>();
  private final IdentifiableResourceReloadListener reloadListener = new SimpleSynchronousResourceReloadListener() {
    @Override
    public Identifier getFabricId() {
      return new Identifier(modInfo.getModId(), modInfo.getModId() + "_reload");
    }

    @Override
    public void reload(ResourceManager manager) {
      if (saved) {
        return;
      }
      saveToFile();
      saved = true;
    }
  };

  private int version;
  private boolean saved = false;

  protected ModConfig(ModInfo modInfo) {
    this.modInfo = modInfo;
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
          // Prefix comment with space to get "# This is a comment"
          fileConfig.setComment(key, " " + comment.get().getString());
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
    String key = modInfo.getModId();
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
