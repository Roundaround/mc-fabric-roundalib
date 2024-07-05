package me.roundaround.roundalib.config;

import me.roundaround.roundalib.PathAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public abstract class GameScopedConfig extends Config {
  protected GameScopedConfig(String modId) {
    super(modId);
  }

  protected GameScopedConfig(String modId, String configId) {
    super(modId, configId);
  }

  protected GameScopedConfig(String modId, int configVersion) {
    super(modId, configVersion);
  }

  protected GameScopedConfig(String modId, String configId, int configVersion) {
    super(modId, configId, configVersion);
  }

  @Override
  public boolean canShowInGui() {
    return this.isInitialized && super.canShowInGui();
  }

  @Override
  protected Path getConfigDirectory() {
    return PathAccessor.getInstance().getConfigDir();
  }
}
