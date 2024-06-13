package me.roundaround.roundalib.client.gui.layout;

public interface FourSided<N extends Number> {
  N left();

  N top();

  N right();

  N bottom();

  default boolean contains(double x, double y) {
    return x >= this.left().doubleValue() && x <= this.right().doubleValue() && y >= this.top().doubleValue() &&
        y <= this.bottom().doubleValue();
  }
}
