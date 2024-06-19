package me.roundaround.roundalib.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface LayoutHook {
  void run();

  static LayoutHook noop() {
    return () -> {
    };
  }
}
