package me.roundaround.roundalib.client.gui.util.positioning;

import net.minecraft.client.gui.widget.Widget;

public class AbsolutePositionMode<T extends Widget> implements PositionMode {
  private final T widget;

  public AbsolutePositionMode(T widget) {
    this.widget = widget;
  }

  @Override
  public int getX() {
    return this.widget.getX();
  }

  @Override
  public int getY() {
    return this.widget.getY();
  }

  @Override
  public void setX(int x) {
    this.widget.setX(x);
  }

  @Override
  public void setY(int y) {
    this.widget.setY(y);
  }
}
