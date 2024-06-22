package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.client.gui.layout.Coords;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.LayoutWidget;

@Environment(EnvType.CLIENT)
public abstract class SizableLayoutWidget<T extends SizableLayoutWidget<T>> implements LayoutWidget {
  protected LayoutHook<T> layoutHook;
  protected int x;
  protected int y;
  protected int width;
  protected int height;

  protected SizableLayoutWidget(LayoutHook<T> layoutHook) {
    this(0, 0, layoutHook);
  }

  protected SizableLayoutWidget(int x, int y, LayoutHook<T> layoutHook) {
    this(x, y, 0, 0, layoutHook);
  }

  protected SizableLayoutWidget(int width, int height) {
    this(0, 0, width, height);
  }

  protected SizableLayoutWidget(int x, int y, int width, int height) {
    this(x, y, width, height, null);
  }

  protected SizableLayoutWidget(
      int x, int y, int width, int height, LayoutHook<T> layoutHook
  ) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.layoutHook = layoutHook;
  }

  @Override
  public void refreshPositions() {
    this.beforeRefreshPositions();
    LayoutWidget.super.refreshPositions();
  }

  protected void beforeRefreshPositions() {
    if (this.layoutHook != null) {
      this.layoutHook.run(this.self());
    }
  }

  @Override
  public void setX(int x) {
    this.x = x;
  }

  @Override
  public int getX() {
    return this.x;
  }

  @Override
  public void setY(int y) {
    this.y = y;
  }

  @Override
  public int getY() {
    return this.y;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  @Override
  public int getWidth() {
    return this.width;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  @Override
  public int getHeight() {
    return this.height;
  }

  public void setDimensions(int width, int height) {
    this.setWidth(width);
    this.setHeight(height);
  }

  public void setDimensionsAndPosition(int width, int height, int x, int y) {
    this.setDimensions(width, height);
    this.setPosition(x, y);
  }

  public void setLayoutHook(LayoutHook<T> layoutHook) {
    this.layoutHook = layoutHook;
  }

  @SuppressWarnings("unchecked")
  protected T self() {
    return (T) this;
  }

  @Environment(EnvType.CLIENT)
  @FunctionalInterface
  public interface DimensionsSupplier<T extends SizableLayoutWidget<T>> {
    Coords supply(T self);
  }
}
