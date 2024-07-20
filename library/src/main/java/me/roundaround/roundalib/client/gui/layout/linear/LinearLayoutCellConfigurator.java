package me.roundaround.roundalib.client.gui.layout.linear;

import me.roundaround.roundalib.client.gui.layout.LayoutHookWithParent;
import me.roundaround.roundalib.client.gui.util.Alignment;
import me.roundaround.roundalib.client.gui.util.Spacing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.Widget;

@Environment(EnvType.CLIENT)
public interface LinearLayoutCellConfigurator<T extends Widget> {
  T getWidget();

  void layoutHook(LayoutHookWithParent<LinearLayoutWidget, T> layoutHook);

  void margin(Spacing margin);

  void align(Alignment align);

  default void alignStart() {
    this.align(Alignment.START);
  }

  default void alignCenter() {
    this.align(Alignment.CENTER);
  }

  default void alignEnd() {
    this.align(Alignment.END);
  }
}
