package me.roundaround.roundalib.event;

import java.util.Arrays;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.Screen;

public interface HandleScreenInputCallback {
  Event<HandleScreenInputCallback> EVENT = EventFactory.createArrayBacked(HandleScreenInputCallback.class,
      (listeners) -> (screen, keyCode, scanCode, modifiers) -> Arrays
          .stream(listeners)
          .anyMatch((listener) -> listener
              .interact(screen, keyCode, scanCode, modifiers)));

  boolean interact(Screen screen, int keyCode, int scanCode, int modifiers);
}
