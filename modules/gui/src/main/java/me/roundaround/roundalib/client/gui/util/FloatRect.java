package me.roundaround.roundalib.client.gui.util;

import net.minecraft.util.Mth;

@SuppressWarnings("unused")
public record FloatRect(Float left, Float top, Float right, Float bottom) implements FourSided<Float> {
  public static FloatRect zero() {
    return byBounds(0, 0, 0, 0);
  }

  public static FloatRect byBounds(float left, float top, float right, float bottom) {
    return new FloatRect(left, top, right, bottom);
  }

  public static FloatRect byDimensions(float left, float top, float width, float height) {
    return new FloatRect(left, top, left + width, top + height);
  }

  public static FloatRect fromIntRect(IntRect intRect) {
    return intRect.toFloatRect();
  }

  public float getWidth() {
    return this.right - this.left;
  }

  public float getHeight() {
    return this.bottom - this.top;
  }

  public IntRect round() {
    return IntRect.byBounds(Math.round(this.left()), Math.round(this.top()), Math.round(this.right()),
        Math.round(this.bottom()));
  }

  public IntRect roundOutward() {
    return IntRect.byBounds(Mth.floor(this.left()), Mth.floor(this.top()), Mth.ceil(this.right()),
        Mth.ceil(this.bottom()));
  }

  public IntRect truncate() {
    return IntRect.byBounds(this.left.intValue(), this.top.intValue(), this.right.intValue(), this.bottom.intValue());
  }

  public FloatRect expand(float amount) {
    return FloatRect.byBounds(this.left - amount, this.top - amount, this.right + amount, this.bottom + amount);
  }

  public FloatRect expand(FourSided<Float> other) {
    return FloatRect.byBounds(this.left - other.left(), this.top - other.top(), this.right + other.right(),
        this.bottom + other.bottom());
  }

  public FloatRect reduce(float amount) {
    return FloatRect.byBounds(this.left + amount, this.top + amount, this.right - amount, this.bottom - amount);
  }

  public FloatRect reduce(FourSided<Float> other) {
    return FloatRect.byBounds(this.left + other.left(), this.top + other.top(), this.right - other.right(),
        this.bottom - other.bottom());
  }
}
