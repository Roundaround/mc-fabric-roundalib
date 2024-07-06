package me.roundaround.roundalib.config.manage;

import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.manage.store.ConfigStore;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface ModConfig extends ConfigStore {
  Text getLabel();

  ConfigOption<?> getByPath(String path);

  ConfigOption<?> getByPath(ConfigPath path);

  Map<String, List<ConfigOption<?>>> getByGroup();

  Map<String, List<ConfigOption<?>>> getByGroupWithGuiControl();

  void subscribe(Consumer<ModConfig> listener);

  void unsubscribe(Consumer<ModConfig> listener);

  Collection<Consumer<ModConfig>> getListeners();

  @Override
  default void refresh() {
    ConfigStore.super.refresh();
    this.getListeners().forEach((consumer) -> consumer.accept(this));
  }

  default void forEachGroup(BiConsumer<String, List<ConfigOption<?>>> consumer) {
    this.getByGroup().forEach(consumer);
  }
}
