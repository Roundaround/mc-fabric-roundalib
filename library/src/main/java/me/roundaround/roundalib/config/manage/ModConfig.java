package me.roundaround.roundalib.config.manage;

import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.manage.store.ConfigStore;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public interface ModConfig extends ConfigStore {
  Text getLabel();

  ConfigOption<?> getByPath(String path);

  ConfigOption<?> getByPath(ConfigPath path);

  Map<String, List<ConfigOption<?>>> getByGroup();

  Map<String, List<ConfigOption<?>>> getByGroupWithGuiControl();

  default void forEachGroup(BiConsumer<String, List<ConfigOption<?>>> consumer) {
    this.getByGroup().forEach(consumer);
  }
}
