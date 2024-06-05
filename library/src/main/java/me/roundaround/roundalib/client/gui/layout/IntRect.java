package me.roundaround.roundalib.client.gui.layout;

@SuppressWarnings("unused")
public class IntRect implements FourSided<Integer> {
  private int left;
  private int top;
  private int right;
  private int bottom;

  private IntRect(int left, int top, int right, int bottom) {
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
  }

  public static IntRect zero() {
    return new IntRect(0, 0, 0, 0);
  }

  public static IntRect byBounds(int left, int top, int right, int bottom) {
    return new IntRect(left, top, right, bottom);
  }

  public static IntRect byDimensions(int left, int top, int width, int height) {
    return new IntRect(left, top, left + width, top + height);
  }

  public static IntRect copyOf(IntRect other) {
    return new IntRect(other.left, other.top, other.right, other.bottom);
  }

  public IntRect copy() {
    return copyOf(this);
  }

  @Override
  public Integer getLeft() {
    return this.left;
  }

  @Override
  public Integer getTop() {
    return this.top;
  }

  @Override
  public Integer getRight() {
    return this.right;
  }

  @Override
  public Integer getBottom() {
    return this.bottom;
  }

  public int getWidth() {
    return this.right - this.left;
  }

  public int getHeight() {
    return this.bottom - this.top;
  }

  public void setLeft(int left) {
    this.left = left;
  }

  public void setTop(int top) {
    this.top = top;
  }

  public void setRight(int right) {
    this.right = right;
  }

  public void setBottom(int bottom) {
    this.bottom = bottom;
  }

  public void setWidth(int width) {
    this.setRight(this.getLeft() + width);
  }

  public void setHeight(int height) {
    this.setBottom(this.getTop() + height);
  }

  public void setByBounds(int left, int top, int right, int bottom) {
    this.setLeft(left);
    this.setTop(top);
    this.setRight(right);
    this.setBottom(bottom);
  }

  public void setByDimensions(int left, int top, int width, int height) {
    this.setLeft(left);
    this.setTop(top);
    this.setRight(left + width);
    this.setBottom(left + height);
  }

  public void set(IntRect other) {
    this.setByBounds(other.left, other.top, other.right, other.bottom);
  }

  public IntRect expand(FourSided<Integer> by) {
    this.setLeft(this.getLeft() - by.getLeft());
    this.setTop(this.getTop() - by.getTop());
    this.setRight(this.getRight() + by.getRight());
    this.setBottom(this.getBottom() + by.getBottom());
    return this;
  }

  public IntRect reduce(FourSided<Integer> by) {
    this.setLeft(this.getLeft() + by.getLeft());
    this.setTop(this.getTop() + by.getTop());
    this.setRight(this.getRight() - by.getRight());
    this.setBottom(this.getBottom() - by.getBottom());
    return this;
  }
}
