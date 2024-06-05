package me.roundaround.roundalib.client.gui.layout;

public interface FourSided<N extends Number> {
  N getLeft();

  N getTop();

  N getRight();

  N getBottom();

  default boolean contains(double x, double y) {
    return x >= this.getLeft().doubleValue() && x <= this.getRight().doubleValue() &&
        y >= this.getTop().doubleValue() && y <= this.getBottom().doubleValue();
  }
}
