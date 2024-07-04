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

  @Override
  public boolean isActive() {
    return this.isInitialized;
  }

  @Override
  protected void onInit() {
    this.runFirstLoad();
  }

  @Override
  protected Path getConfigDirectory() {
    return PathAccessor.getInstance().getConfigDir();
  }
}
