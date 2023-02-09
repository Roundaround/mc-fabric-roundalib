package me.roundaround.roundalib.test.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface HandleInputCallback {
  Event<HandleInputCallback> EVENT = EventFactory.createArrayBacked(HandleInputCallback.class,
      (listeners) -> () -> {
        for (HandleInputCallback listener : listeners) {
          listener.interact();
        }
      });

  void interact();
}
