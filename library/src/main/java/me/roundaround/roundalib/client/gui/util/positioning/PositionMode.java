package me.roundaround.roundalib.client.gui.util.positioning;

public interface PositionMode {
  int getX();

  int getY();

  void setX(int x);

  void setY(int y);

  default void setPosition(int x, int y) {
    this.setX(x);
    this.setY(y);
  }
}
