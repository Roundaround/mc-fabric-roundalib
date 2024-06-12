package me.roundaround.roundalib.client.gui.layout;

@SuppressWarnings("unused")
public class Spacing implements FourSided<Integer> {
  private int top;
  private int right;
  private int bottom;
  private int left;

  private Spacing(int top, int right, int bottom, int left) {
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
  }

  public static Spacing zero() {
    return constant(0);
  }

  public static Spacing constant(int space) {
    return new Spacing(space, space, space, space);
  }

  public static Spacing of(int vertical, int horizontal) {
    return new Spacing(vertical, horizontal, vertical, horizontal);
  }

  public static Spacing of(int top, int horizontal, int bottom) {
    return new Spacing(top, horizontal, bottom, horizontal);
  }

  public static Spacing of(int top, int right, int bottom, int left) {
    return new Spacing(top, right, bottom, left);
  }

  public static Spacing copyOf(Spacing other) {
    return new Spacing(other.top, other.right, other.bottom, other.left);
  }

  public Spacing copy() {
    return copyOf(this);
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

  @Override
  public Integer getLeft() {
    return this.left;
  }

  public int getVertical() {
    return this.getTop() + this.getBottom();
  }

  public int getHorizontal() {
    return this.getLeft() + this.getRight();
  }

  public void setTop(int space) {
    this.top = space;
  }

  public void setRight(int space) {
    this.right = space;
  }

  public void setBottom(int space) {
    this.bottom = space;
  }

  public void setLeft(int space) {
    this.left = space;
  }

  public void set(int top, int right, int bottom, int left) {
    this.setTop(top);
    this.setRight(right);
    this.setBottom(bottom);
    this.setLeft(left);
  }

  public void set(int top, int horizontal, int bottom) {
    this.set(top, horizontal, bottom, horizontal);
  }

  public void set(int vertical, int horizontal) {
    this.set(vertical, horizontal, vertical, horizontal);
  }

  public void set(int space) {
    this.set(space, space, space, space);
  }

  public void set(Spacing other) {
    this.set(other.top, other.right, other.bottom, other.left);
  }

  public void setVertical(int space) {
    this.setTop(space);
    this.setBottom(space);
  }

  public void setHorizontal(int space) {
    this.setLeft(space);
    this.setRight(space);
  }

  public Spacing expand(FourSided<Integer> by) {
    this.setTop(this.getTop() - by.getTop());
    this.setRight(this.getRight() + by.getRight());
    this.setBottom(this.getBottom() + by.getBottom());
    this.setLeft(this.getLeft() - by.getLeft());
    return this;
  }

  public Spacing reduce(FourSided<Integer> by) {
    this.setTop(this.getTop() + by.getTop());
    this.setRight(this.getRight() - by.getRight());
    this.setBottom(this.getBottom() - by.getBottom());
    this.setLeft(this.getLeft() + by.getLeft());
    return this;
  }
}