package me.roundaround.roundalib.client.gui.layout;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface LayoutHookWithParent<P, S> {
  void run(P parent, S self);

  static <P, S> LayoutHookWithParent<P, S> noop() {
    return (P parent, S self) -> {
    };
  }
}
