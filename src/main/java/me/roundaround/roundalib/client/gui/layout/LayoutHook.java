package me.roundaround.roundalib.client.gui.layout;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface LayoutHook<T> {
  void run(T self);

  static <T> LayoutHook<T> noop() {
    return (self) -> {
    };
  }
}
