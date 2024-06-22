package me.roundaround.roundalib.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.Widget;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class LayoutHookWidget<T extends Widget> implements LayoutWidget {
  private final T wrapped;

  private LayoutHook<T> preLayoutHook = LayoutHook.noop();
  private LayoutHook<T> postLayoutHook = LayoutHook.noop();

  protected LayoutHookWidget(T wrapped) {
    this.wrapped = wrapped;
  }

  public static <T extends Widget> LayoutHookWidget<T> empty(NoParamLayoutHook layoutHook) {
    return new LayoutHookWidget<>((T) null).withPreLayoutHook((self) -> layoutHook.run());
  }

  public static <T extends Widget> LayoutHookWidget<T> wrapping(T wrapped) {
    return new LayoutHookWidget<>(wrapped);
  }

  public LayoutHookWidget<T> withPreLayoutHook(LayoutHook<T> preLayoutHook) {
    this.preLayoutHook = preLayoutHook;
    return this;
  }

  public LayoutHookWidget<T> withPostLayoutHook(LayoutHook<T> postLayoutHook) {
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
    this.preLayoutHook.run(this.wrapped);
    if (this.wrapped != null && this.wrapped instanceof LayoutWidget layoutWidget) {
      layoutWidget.refreshPositions();
    }
    this.postLayoutHook.run(this.wrapped);
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

  @Environment(EnvType.CLIENT)
  @FunctionalInterface
  public interface NoParamLayoutHook {
    void run();
  }
}
