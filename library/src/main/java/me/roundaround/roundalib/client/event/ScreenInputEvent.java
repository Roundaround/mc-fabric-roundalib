package me.roundaround.roundalib.client.event;

import me.roundaround.roundalib.event.EventBus;
import net.minecraft.client.gui.screen.Screen;

@FunctionalInterface
public interface ScreenInputEvent {
  EventBus<ScreenInputEvent> EVENT_BUS = new EventBus<>(
      (listeners) -> (screen, keyCode, scanCode, modifiers) -> listeners.stream()
          .anyMatch((listener) -> listener.handle(screen, keyCode, scanCode, modifiers)));

  boolean handle(Screen screen, int keyCode, int scanCode, int modifiers);
}
