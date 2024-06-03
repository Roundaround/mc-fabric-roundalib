package me.roundaround.roundalib.client.gui.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.widget.LayoutWidget;

public abstract class PositionalWidget implements Drawable, LayoutWidget {
  private int left;
  private int top;
  private int width;
  private int height;
  private boolean dirty = false;

  protected PositionalWidget(int left, int top, int width, int height) {
    this.left = left;
    this.top = top;
    this.width = width;
    this.height = height;
  }

  @Override
  public void refreshPositions() {
    LayoutWidget.super.refreshPositions();
    this.dirty = false;
  }

  @Override
  public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    if (this.dirty) {
      this.refreshPositions();
    }
    this.renderPositional(drawContext, mouseX, mouseY, delta);
  }

  public void renderPositional(DrawContext drawContext, int mouseX, int mouseY, float delta) {
  }

  public void markLayoutDirty() {
    this.dirty = true;
  }

  public void setLeft(int left) {
    this.left = left;
    this.dirty = true;
  }

  public void setTop(int top) {
    this.top = top;
    this.dirty = true;
  }

  @Override
  public void setPosition(int left, int top) {
    this.setLeft(left);
    this.setTop(top);
  }

  public void setWidth(int width) {
    this.width = width;
    this.dirty = true;
  }

  public void setHeight(int height) {
    this.height = height;
    this.dirty = true;
  }

  public void setDimensions(int width, int height) {
    this.setWidth(width);
    this.setHeight(height);
  }

  public int getLeft() {
    return this.left;
  }

  public int getTop() {
    return this.top;
  }

  public int getCenterX() {
    return this.getLeft() + this.getWidth() / 2;
  }

  public int getCenterY() {
    return this.getTop() + this.getHeight() / 2;
  }

  @Override
  public int getWidth() {
    return this.width;
  }

  @Override
  public int getHeight() {
    return this.height;
  }

  public int getRight() {
    return this.getLeft() + this.getWidth();
  }

  public int getBottom() {
    return this.getTop() + this.getHeight();
  }

  @Override
  public final void setX(int x) {
    this.setLeft(x);
  }

  @Override
  public final void setY(int y) {
    this.setTop(y);
  }

  @Override
  public final int getX() {
    return this.getLeft();
  }

  @Override
  public final int getY() {
    return this.getTop();
  }

  @Override
  public ScreenRect getNavigationFocus() {
    return new ScreenRect(this.getLeft(), this.getTop(), this.getWidth(), this.getHeight());
  }
}
