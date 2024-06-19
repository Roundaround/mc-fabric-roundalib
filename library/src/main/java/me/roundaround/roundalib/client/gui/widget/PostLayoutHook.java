package me.roundaround.roundalib.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.LayoutWidget;

@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface PostLayoutHook<T extends LayoutWidget> {
  void run(T wrapped);

  static <T extends LayoutWidget> PostLayoutHook<T> noop() {
    return (wrapped) -> {
    };
  }
}
