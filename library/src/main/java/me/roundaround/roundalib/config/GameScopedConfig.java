package me.roundaround.roundalib.config;

import me.roundaround.roundalib.PathAccessor;

import java.nio.file.Path;

public abstract class GameScopedConfig extends Config {
  protected GameScopedConfig(String modId) {
    super(modId);
  }

  protected GameScopedConfig(String modId, int configVersion) {
    super(modId, configVersion);
  }

  protected GameScopedConfig(String modId, int configVersion, boolean prefixPaths) {
    super(modId, configVersion, prefixPaths);
  }

  @Override
  public void init() {
    this.runFirstLoad();
  }

  @Override
  protected Path getConfigDirectory() {
    return PathAccessor.getInstance().getConfigDir();
  }
}
