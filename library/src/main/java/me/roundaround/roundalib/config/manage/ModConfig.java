package me.roundaround.roundalib.config.manage;

import me.roundaround.roundalib.config.option.ConfigOption;

import java.util.List;
import java.util.function.Consumer;

public interface ModConfig {
  void subscribe(Consumer<ModConfig> listener);

  void unsubscribe(Consumer<ModConfig> listener);

  List<ConfigOption<?>> getAllOptions();

  default void forEach(Consumer<ConfigOption<?>> consumer) {
    this.getAllOptions().forEach(consumer);
  }
}
