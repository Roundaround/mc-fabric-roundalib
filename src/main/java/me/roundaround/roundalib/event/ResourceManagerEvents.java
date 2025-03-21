package me.roundaround.roundalib.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.world.level.storage.LevelStorage;

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
    void beforeResourceManagerCreated(LevelStorage.Session session);
  }

  @FunctionalInterface
  public interface Created {
    void afterResourceManagerCreated(LevelStorage.Session session, ResourcePackManager manager);
  }

  private ResourceManagerEvents() {
  }
}
