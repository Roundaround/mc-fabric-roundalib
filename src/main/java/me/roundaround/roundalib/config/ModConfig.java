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
  private final String modId;
  private final int configVersion;
  private final String configScreenI18nKey;
  private final boolean showGroupTitles;
  private final LinkedHashMap<String, LinkedList<ConfigOption<?>>> configOptions = new LinkedHashMap<>();
  private final IdentifiableResourceReloadListener reloadListener = new SimpleSynchronousResourceReloadListener() {
    @Override
    public Identifier getFabricId() {
      return new Identifier(modId, modId + "_reload");
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

    // Wait for all resources (including i18n lang files) to finish loading,
    // then attempt to save the config to file.
    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
      ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(reloadListener);
    } else {
      ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(reloadListener);
    }
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

  public LinkedHashMap<String, LinkedList<ConfigOption<?>>> getConfigOptions() {
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
      RoundaLibMod.LOGGER.info("Skipping saving config to file because nothing has changed.");
      return;
    }

    // TODO: Write own TOML file parsing/editing/saving so that we can make the
    // files prettier :)
    CommentedFileConfig fileConfig = CommentedFileConfig
        .builder(getConfigFile())
        .preserveInsertionOrder()
        .build();

    fileConfig.setComment("configVersion", new TranslatableText("roundalib.version_comment").getString());
    fileConfig.set("configVersion", configVersion);

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

  protected <T extends ConfigOption<?>> T registerConfigOption(T configOption) {
    return registerConfigOption(null, configOption);
  }

  protected <T extends ConfigOption<?>> T registerConfigOption(String group, T configOption) {
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
      RoundaLibMod.LOGGER.warn("Failed to create config directory '{}'", dir.getAbsolutePath());
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
