package me.roundaround.roundalib.config.manage.store;

import me.roundaround.roundalib.PathAccessor;
import me.roundaround.roundalib.client.event.MinecraftServerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import java.nio.file.Path;

public interface WorldScopedFileStore extends FileBackedConfigStore {
  @Override
  default boolean isReady() {
    return FileBackedConfigStore.super.isReady() && PathAccessor.getInstance().isWorldDirAccessible();
  }

  @Override
  default void initializeStore() {
    MinecraftServerEvents.RESOURCE_MANAGER_CREATING.register((storage) -> {
      this.syncWithStore();
    });
    ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
      this.clear();
    });
  }

  @Override
  default Path getConfigDirectory() {
    return PathAccessor.getInstance().getPerWorldConfigDir();
  }
}
