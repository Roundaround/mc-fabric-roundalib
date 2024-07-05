package me.roundaround.roundalib.config.manage;

import me.roundaround.roundalib.config.option.ConfigOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public abstract class OptionRegistryKey<T extends ConfigOption<?>> {
  protected final T option;
  protected final EnvType envType;

  protected OptionRegistryKey(T option) {
    this(option, null);
  }

  protected OptionRegistryKey(T option, EnvType envType) {
    this.option = option;
    this.envType = envType;
  }

  public T get() {
    return this.option;
  }

  public boolean isActive() {
    if (this.envType == null) {
      return true;
    }
    return this.envType == FabricLoader.getInstance().getEnvironmentType();
  }
}
