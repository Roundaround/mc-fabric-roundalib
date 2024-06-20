package me.roundaround.roundalib.client.gui.widget;

import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.Widget;

import java.util.function.Consumer;

public class LayoutHookWidget<T extends Widget> implements LayoutWidget {
  private final T wrapped;

  private LayoutHook preLayoutHook = LayoutHook.noop();
  private LayoutHook postLayoutHook = LayoutHook.noop();

  protected LayoutHookWidget(T wrapped) {
    this.wrapped = wrapped;
  }

  public static LayoutHookWidget<?> empty(LayoutHook preLayoutHook) {
    return new LayoutHookWidget<>(null).withPreLayoutHook(preLayoutHook);
  }

  public static <T extends Widget> LayoutHookWidget<T> wrapping(T wrapped) {
    return new LayoutHookWidget<>(wrapped);
  }

  public LayoutHookWidget<T> withPreLayoutHook(LayoutHook preLayoutHook) {
    this.preLayoutHook = preLayoutHook;
    return this;
  }

  public LayoutHookWidget<T> withPostLayoutHook(LayoutHook postLayoutHook) {
    this.postLayoutHook = postLayoutHook;
    return this;
  }

  public T getWrapped() {
    return this.wrapped;
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
    if (this.wrapped != null && this.wrapped instanceof LayoutWidget layoutWidget) {
      layoutWidget.forEachElement(consumer);
    }
  }

  @Override
  public void forEachChild(Consumer<ClickableWidget> consumer) {
    if (this.wrapped != null) {
      this.wrapped.forEachChild(consumer);
    }
  }

  @Override
  public void refreshPositions() {
    this.preLayoutHook.run();
    if (this.wrapped != null && this.wrapped instanceof LayoutWidget layoutWidget) {
      layoutWidget.refreshPositions();
    }
    this.postLayoutHook.run();
  }

  @Override
  public void setX(int x) {
    if (this.wrapped != null) {
      this.wrapped.setX(x);
    }
  }

  @Override
  public void setY(int y) {
    if (this.wrapped != null) {
      this.wrapped.setY(y);
    }
  }

  @Override
  public int getX() {
    if (this.wrapped != null) {
      return this.wrapped.getX();
    }
    return 0;
  }

  @Override
  public int getY() {
    if (this.wrapped != null) {
      return this.wrapped.getY();
    }
    return 0;
  }

  @Override
  public int getWidth() {
    if (this.wrapped != null) {
      return this.wrapped.getWidth();
    }
    return 0;
  }

  @Override
  public int getHeight() {
    if (this.wrapped != null) {
      return this.wrapped.getHeight();
    }
    return 0;
  }
}
