package me.roundaround.roundalib.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public final class MinecraftClientEvents {
  public static Event<Init> INIT = EventFactory.createArrayBacked(
      Init.class, (callbacks) -> (client) -> {
        for (Init callback : callbacks) {
          callback.onInit(client);
        }
      }
  );

  public static Event<Close> CLOSE = EventFactory.createArrayBacked(
      Close.class, (callbacks) -> (client) -> {
        for (Close callback : callbacks) {
          callback.onClose(client);
        }
      }
  );

  public static Event<HandleInput> HANDLE_INPUT = EventFactory.createArrayBacked(
      HandleInput.class, (callbacks) -> (client) -> {
        for (HandleInput callback : callbacks) {
          callback.onHandleInput(client);
        }
      }
  );

  @FunctionalInterface
  public interface Init {
    void onInit(MinecraftClient client);
  }

  @FunctionalInterface
  public interface Close {
    void onClose(MinecraftClient client);
  }

  @FunctionalInterface
  public interface HandleInput {
    void onHandleInput(MinecraftClient client);
  }

  private MinecraftClientEvents() {
  }
}
