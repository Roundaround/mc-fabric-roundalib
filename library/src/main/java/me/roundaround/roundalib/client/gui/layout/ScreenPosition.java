package me.roundaround.roundalib.client.gui.layout;

public record ScreenPosition(int x, int y) {
  public ScreenPosition movedUp(int amount) {
    return new ScreenPosition(this.x(), this.y() - amount);
  }

  public ScreenPosition movedDown(int amount) {
    return new ScreenPosition(this.x(), this.y() + amount);
  }

  public ScreenPosition movedLeft(int amount) {
    return new ScreenPosition(this.x() - amount, this.y());
  }

  public ScreenPosition movedRight(int amount) {
    return new ScreenPosition(this.x() + amount, this.y());
  }

  public ScreenPosition moved(Direction direction, int amount) {
    return switch (direction) {
      case LEFT -> this.movedLeft(amount);
      case UP -> this.movedUp(amount);
      case RIGHT -> this.movedRight(amount);
      case DOWN -> this.movedDown(amount);
    };
  }

  public enum Direction {
    LEFT, UP, RIGHT, DOWN
  }
}
