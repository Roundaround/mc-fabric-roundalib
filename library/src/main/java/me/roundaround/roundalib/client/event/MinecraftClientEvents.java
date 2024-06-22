package me.roundaround.roundalib.client.event;

import me.roundaround.roundalib.event.EventBus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface MinecraftClientEvents {
  EventBus<MinecraftClientEvents> AFTER_INIT_EVENT_BUS = new EventBus<>(
      (listeners) -> () -> listeners.forEach(MinecraftClientEvents::handle));
  EventBus<MinecraftClientEvents> ON_CLOSE_EVENT_BUS = new EventBus<>(
      (listeners) -> () -> listeners.forEach(MinecraftClientEvents::handle));
  EventBus<MinecraftClientEvents> ON_INPUT_EVENT_BUS = new EventBus<>(
      (listeners) -> () -> listeners.forEach(MinecraftClientEvents::handle));

  void handle();
}
