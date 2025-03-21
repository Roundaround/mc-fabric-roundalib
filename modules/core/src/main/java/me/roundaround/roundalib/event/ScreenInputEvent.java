package me.roundaround.roundalib.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ScreenInputEvent {
  Event<ScreenInputEvent> EVENT = EventFactory.createArrayBacked(ScreenInputEvent.class,
      (callbacks) -> (screen, keyCode, scanCode, modifiers) -> {
        for (ScreenInputEvent callback : callbacks) {
          if (callback.handle(screen, keyCode, scanCode, modifiers)) {
            return true;
          }
        }
        return false;
      }
  );

  boolean handle(Screen screen, int keyCode, int scanCode, int modifiers);
}
