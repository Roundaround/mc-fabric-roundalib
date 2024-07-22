package me.roundaround.roundalib.client.gui.util;

import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.widget.Widget;

@SuppressWarnings("unused")
public record IntRect(Integer left, Integer top, Integer right, Integer bottom) implements FourSided<Integer> {
  public static IntRect zero() {
    return byBounds(0, 0, 0, 0);
  }

  public static IntRect byBounds(int left, int top, int right, int bottom) {
    return new IntRect(left, top, right, bottom);
  }

  public static IntRect byDimensions(int left, int top, int width, int height) {
    return new IntRect(left, top, left + width, top + height);
  }

  public static IntRect fromWidget(Widget widget) {
    return IntRect.byDimensions(widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
  }

  public static IntRect fromScreenRect(ScreenRect screenRect) {
    return IntRect.byDimensions(screenRect.getLeft(), screenRect.getTop(), screenRect.width(), screenRect.height());
  }

  public int getWidth() {
    return this.right - this.left;
  }

  public int getHeight() {
    return this.bottom - this.top;
  }

  public IntRect expandLeft(int amount) {
    return this.expand(amount, 0, 0, 0);
  }

  public IntRect expandTop(int amount) {
    return this.expand(0, amount, 0, 0);
  }

  public IntRect expandRight(int amount) {
    return this.expand(0, 0, amount, 0);
  }

  public IntRect expandBottom(int amount) {
    return this.expand(0, 0, 0, amount);
  }

  public IntRect expand(int amount) {
    return this.expand(amount, amount, amount, amount);
  }

  public IntRect expand(FourSided<Integer> other) {
    return this.expand(other.left(), other.top(), other.right(), other.bottom());
  }

  public IntRect expand(int left, int top, int right, int bottom) {
    return IntRect.byBounds(this.left - left, this.top - top, this.right + right, this.bottom + bottom);
  }

  public IntRect reduce(int amount) {
    return this.expand(-amount);
  }

  public IntRect reduce(FourSided<Integer> other) {
    return this.expand(-other.left(), -other.top(), -other.right(), -other.bottom());
  }

  public IntRect shift(int amountX, int amountY) {
    return IntRect.byBounds(this.left + amountX, this.top + amountY, this.right + amountX, this.bottom + amountY);
  }

  public IntRect shiftLeft(int amount) {
    return this.shift(-amount, 0);
  }

  public IntRect shiftUp(int amount) {
    return this.shift(0, -amount);
  }

  public IntRect shiftRight(int amount) {
    return this.shift(amount, 0);
  }

  public IntRect shiftDown(int amount) {
    return this.shift(0, amount);
  }

  public ScreenRect toScreenRect() {
    return new ScreenRect(this.left, this.top, this.getWidth(), this.getHeight());
  }
}
