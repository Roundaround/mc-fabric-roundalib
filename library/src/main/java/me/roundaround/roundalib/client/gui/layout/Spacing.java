package me.roundaround.roundalib.client.gui.layout;

@SuppressWarnings("unused")
public record Spacing(Integer top, Integer right, Integer bottom, Integer left) implements FourSided<Integer> {
  public static Spacing zero() {
    return of(0);
  }

  public static Spacing of(int space) {
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

  public int getVertical() {
    return this.top() + this.bottom();
  }

  public int getHorizontal() {
    return this.left() + this.right();
  }

  public Spacing setVertical(int space) {
    return Spacing.of(space, this.right, space, this.left);
  }

  public Spacing setHorizontal(int space) {
    return Spacing.of(this.top, space, this.bottom, space);
  }

  public Spacing expand(FourSided<Integer> by) {
    return Spacing.of(this.top - by.top(), this.right + by.right(), this.bottom + by.bottom(), this.left - by.left());
  }

  public Spacing reduce(FourSided<Integer> by) {
    return Spacing.of(this.top + by.top(), this.right - by.right(), this.bottom - by.bottom(), this.left + by.left());
  }
}
