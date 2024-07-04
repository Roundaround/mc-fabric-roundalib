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

  @Override
  public boolean isActive() {
    return PathAccessor.getInstance().isWorldDirAccessible();
  }

  @Override
  protected void onInit() {
    MinecraftServerEvents.RESOURCE_MANAGER_CREATING.register((storage) -> {
      this.runFirstLoad();
    });
  }

  @Override
  protected Path getConfigDirectory() {
    return PathAccessor.getInstance().getPerWorldConfigDir();
  }
}
