package me.roundaround.roundalib.config.manage;

import me.roundaround.roundalib.config.option.ConfigOption;
import net.fabricmc.api.EnvType;

public abstract class KeyBuilder<T extends ConfigOption<?>> {
  protected final T option;
  protected EnvType envType = null;

  protected KeyBuilder(T option) {
    this.option = option;
  }

  public KeyBuilder<T> serverSideOnly() {
    this.envType = EnvType.SERVER;
    return this;
  }

  public KeyBuilder<T> clientSideOnly() {
    this.envType = EnvType.CLIENT;
    return this;
  }
}
