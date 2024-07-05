package me.roundaround.roundalib.config.manage.store;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import me.roundaround.roundalib.PathAccessor;
import me.roundaround.roundalib.RoundaLib;
import me.roundaround.roundalib.config.option.ConfigOption;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public interface FileBackedConfigStore extends ConfigStore {
  String getModId();

  boolean isInitialized();

  Path getConfigDirectory();

  default boolean isReady() {
    return this.isInitialized();
  }

  default void initializeStore() {
    this.syncWithStore();
  }

  default Path getConfigFile() {
    return PathAccessor.getInstance()
        .getConfigFile(this.getConfigDirectory(), this.getModId(), PathAccessor.ConfigFormat.TOML);
  }

  @Override
  default void readFromStore() {
    Path configPath = this.getConfigFile();
    if (configPath == null || Files.notExists(configPath)) {
      return;
    }

    CommentedFileConfig fileConfig = CommentedFileConfig.builder(configPath).preserveInsertionOrder().build();

    fileConfig.load();
    fileConfig.close();

    this.setStoreSuppliedVersion(fileConfig.getIntOrElse("configVersion", -1));
    CommentedConfig config = CommentedConfig.copy(fileConfig);
    if (this.performConfigUpdate(this.getStoreSuppliedVersion(), config)) {
      fileConfig.putAll(config);
    }

    this.getConfigOptionsForStore().forEach((option) -> {
      Object data = fileConfig.get(option.getPath().toString());
      if (data != null) {
        option.deserialize(data);
      }
    });
  }

  @Override
  default void writeToStore() {
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

    if (!this.isDirty()) {
      RoundaLib.LOGGER.info("Skipping saving {} config to file because nothing has changed.", this.getModId());
      return;
    }

    CommentedFileConfig fileConfig = CommentedFileConfig.builder(configPath).preserveInsertionOrder().build();

    fileConfig.setComment("configVersion", " Config version is auto-generated\n DO NOT CHANGE");
    fileConfig.set("configVersion", this.getVersion());

    Collection<ConfigOption<?>> options = this.getConfigOptionsForStore();
    options.forEach((option) -> {
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

    options.forEach(ConfigOption::commit);
  }
}
