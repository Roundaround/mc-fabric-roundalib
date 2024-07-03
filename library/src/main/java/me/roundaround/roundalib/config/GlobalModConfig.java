package me.roundaround.roundalib.config;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public abstract class GlobalModConfig extends ModConfig {
  protected GlobalModConfig(String modId) {
    super(modId);
  }

  protected GlobalModConfig(String modId, int configVersion) {
    super(modId, configVersion);
  }

  protected GlobalModConfig(String modId, int configVersion, boolean prefixPaths) {
    super(modId, configVersion, prefixPaths);
  }

  @Override
  public void init() {
    this.runFirstLoad();
  }

  @Override
  protected Path getConfigDirectory() {
    return FabricLoader.getInstance().getConfigDir();
  }
}
