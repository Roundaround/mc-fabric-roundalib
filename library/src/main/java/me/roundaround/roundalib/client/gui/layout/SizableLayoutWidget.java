package me.roundaround.roundalib.client.gui.layout;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.LayoutWidget;

@Environment(EnvType.CLIENT)
public abstract class SizableLayoutWidget implements LayoutWidget {
  protected int x;
  protected int y;
  protected int width;
  protected int height;

  protected SizableLayoutWidget(int width, int height) {
    this(0, 0, width, height);
  }

  protected SizableLayoutWidget(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
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

  public void setPositionAndDimensions(int x, int y, int width, int height) {
    this.setPosition(x, y);
    this.setDimensions(width, height);
  }
}
