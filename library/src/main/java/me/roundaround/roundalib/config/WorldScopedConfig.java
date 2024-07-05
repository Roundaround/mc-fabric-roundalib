package me.roundaround.roundalib.config;

import me.roundaround.roundalib.PathAccessor;
import me.roundaround.roundalib.client.event.MinecraftServerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import java.nio.file.Path;

public abstract class WorldScopedConfig extends Config {
  protected WorldScopedConfig(String modId) {
    super(modId);
  }

  protected WorldScopedConfig(String modId, String configId) {
    super(modId, configId);
  }

  protected WorldScopedConfig(String modId, int configVersion) {
    super(modId, configVersion);
  }

  protected WorldScopedConfig(String modId, String configId, int configVersion) {
    super(modId, configId, configVersion);
  }

  @Override
  public boolean canShowInGui() {
    return PathAccessor.getInstance().isWorldDirAccessible() && super.canShowInGui();
  }

  @Override
  protected void onInit() {
    MinecraftServerEvents.RESOURCE_MANAGER_CREATING.register((storage) -> {
      this.syncWithFile();
    });
    ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
      this.clear();
    });
  }

  @Override
  protected Path getConfigDirectory() {
    return PathAccessor.getInstance().getPerWorldConfigDir();
  }
}
