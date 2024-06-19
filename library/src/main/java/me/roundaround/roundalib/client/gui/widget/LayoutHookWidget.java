package me.roundaround.roundalib.client.gui.widget;

import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.Widget;

import java.util.function.Consumer;

public class LayoutHookWidget<T extends LayoutWidget> implements LayoutWidget {
  private final T wrapped;

  private PreLayoutHook<T> preLayoutHook = PreLayoutHook.noop();
  private PostLayoutHook<T> postLayoutHook = PostLayoutHook.noop();

  protected LayoutHookWidget(T wrapped) {
    assert wrapped != null;
    this.wrapped = wrapped;
  }

  public static <T extends LayoutWidget> LayoutHookWidget<T> from(T wrapped) {
    return new LayoutHookWidget<>(wrapped);
  }

  public LayoutHookWidget<T> withPreLayoutHook(PreLayoutHook<T> preLayoutHook) {
    this.preLayoutHook = preLayoutHook;
    return this;
  }

  public LayoutHookWidget<T> withPostLayoutHook(PostLayoutHook<T> postLayoutHook) {
    this.postLayoutHook = postLayoutHook;
    return this;
  }

  public T getWrapped() {
    return this.wrapped;
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
    this.wrapped.forEachElement(consumer);
  }

  @Override
  public void forEachChild(Consumer<ClickableWidget> consumer) {
    this.wrapped.forEachChild(consumer);
  }

  @Override
  public void refreshPositions() {
    this.preLayoutHook.run(this.wrapped);
    this.wrapped.refreshPositions();
    this.postLayoutHook.run(this.wrapped);
  }

  @Override
  public void setX(int x) {
    this.wrapped.setX(x);
  }

  @Override
  public void setY(int y) {
    this.wrapped.setY(y);
  }

  @Override
  public int getX() {
    return this.wrapped.getX();
  }

  @Override
  public int getY() {
    return this.wrapped.getY();
  }

  @Override
  public int getWidth() {
    return this.wrapped.getWidth();
  }

  @Override
  public int getHeight() {
    return this.wrapped.getHeight();
  }
}
