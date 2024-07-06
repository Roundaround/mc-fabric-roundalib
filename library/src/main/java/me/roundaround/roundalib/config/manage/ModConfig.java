package me.roundaround.roundalib.config.manage;

import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.manage.store.FileBackedConfigStore;
import me.roundaround.roundalib.config.option.ConfigOption;

import java.util.List;
import java.util.function.Consumer;

public interface ModConfig extends FileBackedConfigStore {
  void subscribe(Consumer<ModConfig> listener);

  void unsubscribe(Consumer<ModConfig> listener);

  ConfigOption<?> getByPath(String path);

  ConfigOption<?> getByPath(ConfigPath path);

  List<ConfigOption<?>> getAll();

  default void forEach(Consumer<ConfigOption<?>> consumer) {
    this.getAll().forEach(consumer);
  }
}
