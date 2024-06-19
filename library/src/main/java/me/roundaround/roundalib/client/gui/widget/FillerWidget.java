package me.roundaround.roundalib.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class FillerWidget implements Widget {
  private int x;
  private int y;
  private int width;
  private int height;

  public FillerWidget(int width, int height) {
    this(0, 0, width, height);
  }

  public FillerWidget(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public static FillerWidget ofWidth(int width) {
    return new FillerWidget(width, 0);
  }

  public static FillerWidget ofHeight(int height) {
    return new FillerWidget(0, height);
  }

  public static FillerWidget ofSize(int width, int height) {
    return new FillerWidget(width, height);
  }

  @Override
  public void setX(int x) {
    this.x = x;
  }

  @Override
  public void setY(int y) {
    this.y = y;
  }

  @Override
  public int getX() {
    return this.x;
  }

  @Override
  public int getY() {
    return this.y;
  }

  @Override
  public int getWidth() {
    return this.width;
  }

  @Override
  public int getHeight() {
    return this.height;
  }

  @Override
  public void forEachChild(Consumer<ClickableWidget> consumer) {
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public void setDimensions(int width, int height) {
    this.setWidth(width);
    this.setHeight(height);
  }

  public void setDimensionsAndPosition(int width, int height, int x, int y) {
    this.setDimensions(width, height);
    this.setPosition(x, y);
  }
}
