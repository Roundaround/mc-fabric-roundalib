package me.roundaround.roundalib.client.gui.layout;

import net.minecraft.util.math.MathHelper;

@SuppressWarnings("unused")
public class FloatRect implements FourSided<Float> {
  private float left;
  private float top;
  private float right;
  private float bottom;

  private FloatRect(float left, float top, float right, float bottom) {
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
  }

  public static FloatRect zero() {
    return new FloatRect(0, 0, 0, 0);
  }

  public static FloatRect byBounds(float left, float top, float right, float bottom) {
    return new FloatRect(left, top, right, bottom);
  }

  public static FloatRect byDimensions(float left, float top, float width, float height) {
    return new FloatRect(left, top, left + width, top + height);
  }

  public static FloatRect copyOf(FloatRect other) {
    return new FloatRect(other.left, other.top, other.right, other.bottom);
  }

  public FloatRect copy() {
    return copyOf(this);
  }

  @Override
  public Float getLeft() {
    return this.left;
  }

  @Override
  public Float getTop() {
    return this.top;
  }

  @Override
  public Float getRight() {
    return this.right;
  }

  @Override
  public Float getBottom() {
    return this.bottom;
  }

  public float getWidth() {
    return this.right - this.left;
  }

  public float getHeight() {
    return this.bottom - this.top;
  }

  public void setLeft(float left) {
    this.left = left;
  }

  public void setTop(float top) {
    this.top = top;
  }

  public void setRight(float right) {
    this.right = right;
  }

  public void setBottom(float bottom) {
    this.bottom = bottom;
  }

  public void setWidth(float width) {
    this.setRight(this.getLeft() + width);
  }

  public void setHeight(float height) {
    this.setBottom(this.getTop() + height);
  }

  public void setByBounds(float left, float top, float right, float bottom) {
    this.setLeft(left);
    this.setTop(top);
    this.setRight(right);
    this.setBottom(bottom);
  }

  public void setByDimensions(float left, float top, float width, float height) {
    this.setLeft(left);
    this.setTop(top);
    this.setRight(left + width);
    this.setBottom(left + height);
  }

  public void set(FloatRect other) {
    this.setByBounds(other.left, other.top, other.right, other.bottom);
  }

  public FloatRect expand(FourSided<Float> by) {
    this.setLeft(this.getLeft() - by.getLeft());
    this.setTop(this.getTop() - by.getTop());
    this.setRight(this.getRight() + by.getRight());
    this.setBottom(this.getBottom() + by.getBottom());
    return this;
  }

  public FloatRect reduce(FourSided<Float> by) {
    this.setLeft(this.getLeft() + by.getLeft());
    this.setTop(this.getTop() + by.getTop());
    this.setRight(this.getRight() - by.getRight());
    this.setBottom(this.getBottom() - by.getBottom());
    return this;
  }

  public FloatRect expandInt(FourSided<Integer> by) {
    this.setLeft(this.getLeft() - by.getLeft());
    this.setTop(this.getTop() - by.getTop());
    this.setRight(this.getRight() + by.getRight());
    this.setBottom(this.getBottom() + by.getBottom());
    return this;
  }

  public FloatRect reduceInt(FourSided<Integer> by) {
    this.setLeft(this.getLeft() + by.getLeft());
    this.setTop(this.getTop() + by.getTop());
    this.setRight(this.getRight() - by.getRight());
    this.setBottom(this.getBottom() - by.getBottom());
    return this;
  }

  public IntRect round() {
    return IntRect.byBounds(Math.round(this.getLeft()), Math.round(this.getTop()), Math.round(this.getRight()),
        Math.round(this.getBottom())
    );
  }

  public IntRect roundOutward() {
    return IntRect.byBounds(MathHelper.floor(this.getLeft()), MathHelper.floor(this.getTop()),
        MathHelper.ceil(this.getRight()), MathHelper.ceil(this.getBottom())
    );
  }

  public IntRect toPixelBounds() {
    return IntRect.byBounds((int) this.left, (int) this.top, (int) this.right, (int) this.bottom);
  }
}
