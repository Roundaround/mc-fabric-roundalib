package me.roundaround.roundalib.config.manage.store;

import me.roundaround.roundalib.RoundaLib;
import me.roundaround.roundalib.config.io.ConfigDoc;
import me.roundaround.roundalib.config.io.ConfigSerializer;
import me.roundaround.roundalib.config.io.JsoncSerializer;
import me.roundaround.roundalib.config.io.PropertiesSerializer;
import me.roundaround.roundalib.config.io.TomlSerializer;
import me.roundaround.roundalib.config.io.YamlSerializer;
import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.util.PathAccessor;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FileBackedConfigStore extends ConfigStore {
  Path getConfigDirectory();

  default String getFileName() {
    return this.getModId();
  }

  default PathAccessor.ConfigFormat getConfigFormat() {
    return PathAccessor.ConfigFormat.TOML;
  }

  /**
   * Returns a read-only store whose values should seed this store's options before
   * the primary file is read. Intended for legacy migration: the legacy store is
   * fully loaded first, matching options (by path) are copied as initial values,
   * then the primary file read overwrites them where the file exists.
   *
   * <p>The returned store should implement {@link ReadOnlyFileStore} so it never
   * creates or overwrites files on disk.
   */
  default Optional<? extends FileBackedConfigStore> getLegacyStore() {
    return Optional.empty();
  }

  default Path getConfigFile() {
    return PathAccessor.getInstance()
        .getConfigFile(this.getConfigDirectory(), this.getFileName(), this.getConfigFormat());
  }

  @Override
  default void readFromStore() {
    // Seed option values from the legacy store (if any) before reading the primary file.
    // This allows transparent migration: legacy values act as defaults that the primary
    // file overwrites when it exists.
    this.getLegacyStore().ifPresent(legacy -> {
      legacy.syncWithStore();

      Map<ConfigPath, ConfigOption<?>> legacyByPath = new HashMap<>();
      legacy.getAll().forEach(opt -> legacyByPath.put(opt.getPath(), opt));

      this.getAll().forEach(opt -> {
        ConfigOption<?> legacyOpt = legacyByPath.get(opt.getPath());
        if (legacyOpt != null) {
          opt.deserialize(legacyOpt.serialize());
        }
      });
    });

    Path configPath = this.getConfigFile();
    if (configPath == null || Files.notExists(configPath)) {
      return;
    }

    ConfigDoc doc;
    try (Reader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
      doc = serializerFor(configPath).read(reader);
    } catch (IOException e) {
      RoundaLib.LOGGER.error("Failed to read config file: {}", configPath.toAbsolutePath());
      return;
    }

    this.setStoreSuppliedVersion(doc.getIntOrElse("configVersion", -1));
    this.performConfigUpdate(this.getStoreSuppliedVersion(), doc);

    this.getAll().forEach((option) -> {
      Object data = doc.get(option.getPath().toString());
      if (data != null) {
        option.deserialize(data);
      } else {
        option.markDirty();
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
      RoundaLib.LOGGER.debug("Skipping saving {} config to file because nothing has changed.", this.getModId());
      return;
    }

    ConfigDoc doc = new ConfigDoc();
    doc.setComment("configVersion", " Config version is auto-generated\n DO NOT CHANGE");
    doc.set("configVersion", this.getVersion());

    Collection<ConfigOption<?>> options = this.getAll();
    options.forEach((option) -> {
      String path = option.getPath().toString();
      List<String> comment = option.getComment();
      if (!comment.isEmpty()) {
        doc.setComment(path, " " + String.join("\n ", comment));
      }
      doc.set(path, option.serialize());
    });

    try (Writer writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8)) {
      serializerFor(configPath).write(doc, writer);
    } catch (IOException e) {
      RoundaLib.LOGGER.error("Failed to write config file: {}", configPath.toAbsolutePath());
      return;
    }

    options.forEach(ConfigOption::commit);
  }

  private static ConfigSerializer serializerFor(Path path) {
    String name = path.getFileName().toString();
    if (name.endsWith(".yaml") || name.endsWith(".yml")) return new YamlSerializer();
    if (name.endsWith(".jsonc") || name.endsWith(".json")) return new JsoncSerializer();
    if (name.endsWith(".properties")) return new PropertiesSerializer();
    return new TomlSerializer();
  }
}
