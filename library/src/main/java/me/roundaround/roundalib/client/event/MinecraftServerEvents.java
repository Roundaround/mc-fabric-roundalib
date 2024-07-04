package me.roundaround.roundalib.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.world.level.storage.LevelStorage;

public class MinecraftServerEvents {
  public static Event<ResourceManagerCreating> RESOURCE_MANAGER_CREATING = EventFactory.createArrayBacked(
      ResourceManagerCreating.class, (callbacks) -> (session) -> {
        for (ResourceManagerCreating callback : callbacks) {
          callback.beforeResourceManagerCreated(session);
        }
      });

  public static Event<ResourceManagerCreated> RESOURCE_MANAGER_CREATED = EventFactory.createArrayBacked(
      ResourceManagerCreated.class, (callbacks) -> (session, manager) -> {
        for (ResourceManagerCreated callback : callbacks) {
          callback.afterResourceManagerCreated(session, manager);
        }
      });

  @FunctionalInterface
  public interface ResourceManagerCreating {
    void beforeResourceManagerCreated(LevelStorage.Session session);
  }

  @FunctionalInterface
  public interface ResourceManagerCreated {
    void afterResourceManagerCreated(LevelStorage.Session session, ResourcePackManager manager);
  }
}
