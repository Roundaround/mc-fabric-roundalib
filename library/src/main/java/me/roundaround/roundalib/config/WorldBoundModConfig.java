package me.roundaround.roundalib.config;

import java.nio.file.Path;

public abstract class WorldBoundModConfig extends ModConfig {
  private Path currentPath = null;

  protected WorldBoundModConfig(String modId) {
    super(modId);
  }

  protected WorldBoundModConfig(String modId, int configVersion) {
    super(modId, configVersion);
  }

  protected WorldBoundModConfig(String modId, int configVersion, boolean prefixPaths) {
    super(modId, configVersion, prefixPaths);
  }

  @Override
  public void init() {
  }

  @Override
  protected Path getConfigDirectory() {
    return this.currentPath;
  }
}
