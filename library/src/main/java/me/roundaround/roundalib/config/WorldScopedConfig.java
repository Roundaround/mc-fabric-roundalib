package me.roundaround.roundalib.config;

import me.roundaround.roundalib.PathAccessor;
import me.roundaround.roundalib.client.event.MinecraftServerEvents;

import java.nio.file.Path;

public abstract class WorldScopedConfig extends Config {
  protected WorldScopedConfig(String modId) {
    super(modId);
  }

  protected WorldScopedConfig(String modId, int configVersion) {
    super(modId, configVersion);
  }

  protected WorldScopedConfig(String modId, int configVersion, boolean prefixPaths) {
    super(modId, configVersion, prefixPaths);
  }

  @Override
  public void init() {
    MinecraftServerEvents.RESOURCE_MANAGER_CREATING.register((storage) -> {
      this.runFirstLoad();
    });
  }

  @Override
  protected Path getConfigDirectory() {
    return PathAccessor.getInstance().getPerWorldConfigDir();
  }
}
