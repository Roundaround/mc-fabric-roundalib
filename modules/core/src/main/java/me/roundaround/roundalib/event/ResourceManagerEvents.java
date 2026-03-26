package me.roundaround.roundalib.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;

public final class ResourceManagerEvents {
  public static Event<Creating> CREATING = EventFactory.createArrayBacked(
      Creating.class, (callbacks) -> (session) -> {
        for (Creating callback : callbacks) {
          callback.beforeResourceManagerCreated(session);
        }
      }
  );

  public static Event<Created> CREATED = EventFactory.createArrayBacked(
      Created.class, (callbacks) -> (session, manager) -> {
        for (Created callback : callbacks) {
          callback.afterResourceManagerCreated(session, manager);
        }
      }
  );

  @FunctionalInterface
  public interface Creating {
    void beforeResourceManagerCreated(LevelStorageSource.LevelStorageAccess session);
  }

  @FunctionalInterface
  public interface Created {
    void afterResourceManagerCreated(LevelStorageSource.LevelStorageAccess session, PackRepository manager);
  }

  private ResourceManagerEvents() {
  }
}
