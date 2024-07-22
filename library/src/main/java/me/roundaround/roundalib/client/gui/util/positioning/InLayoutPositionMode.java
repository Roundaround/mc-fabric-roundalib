package me.roundaround.roundalib.client.gui.util.positioning;

import net.minecraft.client.gui.widget.Widget;

public class InLayoutPositionMode<T extends Widget> extends AbsolutePositionMode<T> {
  public InLayoutPositionMode(T widget) {
    super(widget);
  }
}
