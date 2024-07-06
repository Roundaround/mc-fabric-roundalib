package me.roundaround.roundalib.config.manage;

import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.manage.store.ConfigStore;
import me.roundaround.roundalib.config.option.ConfigOption;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface ModConfig extends ConfigStore {
  void subscribe(Consumer<ModConfig> listener);

  void unsubscribe(Consumer<ModConfig> listener);

  ConfigOption<?> getByPath(String path);

  ConfigOption<?> getByPath(ConfigPath path);

  List<ConfigOption<?>> getAll();

  Map<String, List<ConfigOption<?>>> getAllByGroup();

  default void forEach(Consumer<ConfigOption<?>> consumer) {
    this.getAll().forEach(consumer);
  }

  default void forEachGroup(BiConsumer<String, List<ConfigOption<?>>> consumer) {
    this.getAllByGroup().forEach(consumer);
  }
}
