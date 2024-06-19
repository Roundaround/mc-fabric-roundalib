package me.roundaround.roundalib.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.Widget;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface LayoutHookWithRefs<P extends Widget, S extends Widget> {
  void run(P parent, S self);

  static <P extends Widget, S extends Widget> LayoutHookWithRefs<P, S> noop() {
    return (P parent, S self) -> {
    };
  }
}
