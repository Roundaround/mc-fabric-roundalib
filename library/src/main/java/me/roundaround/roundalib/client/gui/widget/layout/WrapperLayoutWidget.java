package me.roundaround.roundalib.client.gui.widget.layout;

import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.Widget;

import java.util.function.Consumer;

public class WrapperLayoutWidget<T extends Widget> implements LayoutWidget {
  protected final T widget;

  private final LayoutHook<T> layoutHook;

  public WrapperLayoutWidget(T widget) {
    this(widget, null);
  }

  public WrapperLayoutWidget(T widget, LayoutHook<T> layoutHook) {
    this.widget = widget;
    this.layoutHook = layoutHook;
  }

  public T getWidget() {
    return this.widget;
  }

  @Override
  public void refreshPositions() {
    if (this.layoutHook != null) {
      this.layoutHook.run(this.widget);
    }
    LayoutWidget.super.refreshPositions();
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
    consumer.accept(this.widget);
  }

  @Override
  public void setX(int x) {
    this.widget.setX(x);
  }

  @Override
  public void setY(int y) {
    this.widget.setY(y);
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
  public int getWidth() {
    return this.widget.getWidth();
  }

  @Override
  public int getHeight() {
    return this.widget.getHeight();
  }
}
