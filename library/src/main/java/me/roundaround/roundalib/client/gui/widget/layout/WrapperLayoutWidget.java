package me.roundaround.roundalib.client.gui.widget.layout;

import net.minecraft.client.gui.widget.Widget;

import java.util.function.Consumer;

public class WrapperLayoutWidget<T extends Widget> extends SizableLayoutWidget {
  protected final T widget;

  private final LayoutHookWithParent<WrapperLayoutWidget<T>, T> layoutHook;

  public WrapperLayoutWidget(T widget) {
    this(0, 0, 1, 1, widget, null);
  }

  public WrapperLayoutWidget(
      int x, int y, int width, int height, T widget, LayoutHookWithParent<WrapperLayoutWidget<T>, T> layoutHook
  ) {
    super(x, y, width, height);

    this.widget = widget;
    this.layoutHook = layoutHook;
  }

  public T getWidget() {
    return this.widget;
  }

  @Override
  public void refreshPositions() {
    if (this.layoutHook != null) {
      this.layoutHook.run(this, this.widget);
    }
    super.refreshPositions();
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
    consumer.accept(this.widget);
  }

  public static class Builder<T extends Widget> {
    private final T widget;

    private LayoutHookWithParent<WrapperLayoutWidget<T>, T> layoutHook;
    private int x;
    private int y;
    private int width = 1;
    private int height = 1;

    public Builder(T widget) {
      this.widget = widget;
    }

    public Builder<T> setX(int x) {
      this.x = x;
      return this;
    }

    public Builder<T> setY(int y) {
      this.y = y;
      return this;
    }

    public Builder<T> setPos(int x, int y) {
      this.x = x;
      this.y = y;
      return this;
    }

    public Builder<T> setWidth(int width) {
      this.width = width;
      return this;
    }

    public Builder<T> setHeight(int height) {
      this.height = height;
      return this;
    }

    public Builder<T> setDimensions(int width, int height) {
      this.width = width;
      this.height = height;
      return this;
    }

    public Builder<T> setPositionAndDimensions(int x, int y, int width, int height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      return this;
    }

    public Builder<T> setLayoutHook(LayoutHookWithParent<WrapperLayoutWidget<T>, T> layoutHook) {
      this.layoutHook = layoutHook;
      return this;
    }

    public WrapperLayoutWidget<T> build() {
      return new WrapperLayoutWidget<>(this.x, this.y, this.width, this.height, this.widget, this.layoutHook);
    }
  }
}
