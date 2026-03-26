package me.roundaround.roundalib.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ScreenInputEvent {
  Event<ScreenInputEvent> EVENT = EventFactory.createArrayBacked(
      ScreenInputEvent.class, (callbacks) -> (screen, input) -> {
        for (ScreenInputEvent callback : callbacks) {
          if (callback.handle(screen, input)) {
            return true;
          }
        }
        return false;
      }
  );

  boolean handle(Screen screen, KeyEvent input);
}
