package me.roundaround.roundalib.config.manage;

import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.option.ConfigOption;

import java.util.LinkedHashMap;

public abstract class ModConfigImpl<T extends OptionRegistryKey<?>> implements ModConfig {
  private final LinkedHashMap<ConfigPath, T> keys = new LinkedHashMap<>();

  protected ConfigOption<?> register(T key) {
    this.keys.put(key.get().getPath(), key);
    return key.get();
  }
}
