package me.roundaround.roundalib.client.gui.layout;

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
}
