package me.roundaround.roundalib.client.gui.widget.drawable;

import me.roundaround.roundalib.client.gui.util.GuiUtil;
import me.roundaround.roundalib.client.gui.util.Coords;
import me.roundaround.roundalib.client.gui.util.IntRect;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

public class CrosshairWidget extends DrawableWidget {
  private static final int DEFAULT_THICKNESS = 2;
  private static final int DEFAULT_GAP = GuiUtil.PADDING;
  private static final int DEFAULT_LENGTH = 4 * GuiUtil.PADDING;
  private static final int DEFAULT_COLOR = GuiUtil.CROSSHAIR_COLOR;

  private int thickness;
  private int gap;
  private int length;
  private int color;

  public CrosshairWidget() {
    this(0, 0);
  }

  public CrosshairWidget(IntRect bounds) {
    this(0, 0);
    this.centerOn(bounds);
  }

  public CrosshairWidget(int x, int y) {
    this(x, y, DEFAULT_THICKNESS, DEFAULT_GAP, DEFAULT_LENGTH, DEFAULT_COLOR);
  }

  public CrosshairWidget(IntRect bounds, int thickness, int gap, int length, int color) {
    this(0, 0, thickness, gap, length, color);
    this.centerOn(bounds);
  }

  public CrosshairWidget(int thickness, int gap, int length, int color) {
    this(0, 0, thickness, gap, length, color);
  }

  public CrosshairWidget(int x, int y, int thickness, int gap, int length, int color) {
    super(x, y, 0, 0);

    this.thickness = Math.max(1, thickness);
    this.gap = Math.max(0, gap);
    this.length = Math.max(0, length);
    this.color = color;

    int size = getSize(this.thickness, this.gap, this.length);
    this.setDimensions(size, size);
  }

  @Override
  public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    int left = this.getX() + this.length + this.gap;
    int top = this.getY() + this.length + this.gap;
    int right = this.getRight() - this.length - this.gap;
    int bottom = this.getBottom() - this.length - this.gap;

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

  public void centerOn(IntRect bounds) {
    Coords centerCoords = getCenterCoords(bounds, this.thickness, this.gap, this.length);
    this.setPosition(centerCoords.x(), centerCoords.y());
  }

  public void setThickness(int thickness) {
    this.thickness = Math.max(1, thickness);
    int size = getSize(this.thickness, this.gap, this.length);
    this.setDimensions(size, size);
  }

  public void setGap(int gap) {
    this.gap = Math.max(0, gap);
    int size = getSize(this.thickness, this.gap, this.length);
    this.setDimensions(size, size);
  }

  public void setLength(int length) {
    this.length = Math.max(0, length);
    int size = getSize(this.thickness, this.gap, this.length);
    this.setDimensions(size, size);
  }

  public void setColor(int color) {
    this.color = color;
  }

  public static Coords getCenterCoords(IntRect bounds, int thickness, int gap, int length) {
    int size = getSize(thickness, gap, length);
    int x = bounds.left() + MathHelper.floor((bounds.getWidth() - size - 0.5f) * 0.5f);
    int y = bounds.top() + MathHelper.floor((bounds.getHeight() - size - 0.5f) * 0.5f);
    return Coords.of(x, y);
  }

  public static int getSize(int thickness, int gap, int length) {
    return thickness + 2 * (gap + length);
  }
}
