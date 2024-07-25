package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.util.Coords;
import me.roundaround.roundalib.client.gui.util.IntRect;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public class CrosshairWidget implements Drawable, Widget {
  private static final int DEFAULT_THICKNESS = 2;
  private static final int DEFAULT_GAP = GuiUtil.PADDING;
  private static final int DEFAULT_LENGTH = 4 * GuiUtil.PADDING;
  private static final int DEFAULT_COLOR = GuiUtil.CROSSHAIR_COLOR;

  private int x;
  private int y;
  private int thickness;
  private int gap;
  private int length;
  private int color;

  public CrosshairWidget(IntRect bounds) {
    this(0, 0);

    Coords centerCoords = getCenterCoords(bounds, DEFAULT_THICKNESS);
    this.setPosition(centerCoords.x(), centerCoords.y());
  }

  public CrosshairWidget(int x, int y) {
    this(x, y, DEFAULT_THICKNESS, DEFAULT_GAP, DEFAULT_LENGTH, DEFAULT_COLOR);
  }

  public CrosshairWidget(IntRect bounds, int thickness, int gap, int length, int color) {
    this(0, 0, thickness, gap, length, color);

    Coords centerCoords = getCenterCoords(bounds, thickness);
    this.setPosition(centerCoords.x(), centerCoords.y());
  }

  public CrosshairWidget(int x, int y, int thickness, int gap, int length, int color) {
    this.setPosition(x, y);
    this.setThickness(thickness);
    this.setGap(gap);
    this.setLength(length);
    this.setColor(color);
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    int left = this.x;
    int top = this.y;
    int right = this.x + this.thickness;
    int bottom = this.y + this.thickness;

    // Left
    context.fill(left - this.gap - this.length, top, left - this.gap, bottom, this.color);
    // Right
    context.fill(right + this.gap, top, right + this.gap + this.length, bottom, this.color);
    // Top
    context.fill(left, top - this.gap - this.length, right, top - this.gap, this.color);
    // Bottom
    context.fill(left, bottom + this.gap, right, bottom + this.gap + this.length, this.color);
    // Center
    context.fill(left, top, right, bottom, this.color);
  }

  @Override
  public void setX(int x) {
    this.x = x;
  }

  @Override
  public void setY(int y) {
    this.y = y;
  }

  public void centerOn(IntRect bounds) {
    Coords centerCoords = getCenterCoords(bounds, this.thickness);
    this.setPosition(centerCoords.x(), centerCoords.y());
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
    return this.thickness;
  }

  @Override
  public int getHeight() {
    return this.thickness;
  }

  @Override
  public void forEachChild(Consumer<ClickableWidget> consumer) {
  }

  public void setThickness(int thickness) {
    this.thickness = Math.max(1, thickness);
  }

  public void setGap(int gap) {
    this.gap = Math.max(0, gap);
  }

  public void setLength(int length) {
    this.length = Math.max(0, length);
  }

  public void setColor(int color) {
    this.color = color;
  }

  public static Coords getCenterCoords(IntRect bounds) {
    return getCenterCoords(bounds, DEFAULT_THICKNESS);
  }

  public static Coords getCenterCoords(IntRect bounds, int thickness) {
    int x = bounds.left() + MathHelper.floor((bounds.getWidth() - thickness - 0.5f) * 0.5f);
    int y = bounds.top() + MathHelper.floor((bounds.getHeight() - thickness - 0.5f) * 0.5f);
    return Coords.of(x, y);
  }
}
