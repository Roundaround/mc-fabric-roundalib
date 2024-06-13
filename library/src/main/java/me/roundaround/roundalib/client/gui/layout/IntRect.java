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

  public IntRect expand(int amount) {
    return IntRect.byBounds(this.left - amount, this.top - amount, this.right + amount, this.bottom + amount);
  }

  public IntRect expand(FourSided<Integer> other) {
    return IntRect.byBounds(this.left - other.left(), this.top - other.top(), this.right + other.right(),
        this.bottom + other.bottom()
    );
  }

  public IntRect reduce(int amount) {
    return IntRect.byBounds(this.left + amount, this.top + amount, this.right - amount, this.bottom - amount);
  }

  public IntRect reduce(FourSided<Integer> other) {
    return IntRect.byBounds(this.left + other.left(), this.top + other.top(), this.right - other.right(),
        this.bottom - other.bottom()
    );
  }
}
