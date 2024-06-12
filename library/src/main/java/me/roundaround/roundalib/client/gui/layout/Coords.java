package me.roundaround.roundalib.client.gui.layout;

public record Coords(int x, int y) {
  public Coords movedUp(int amount) {
    return new Coords(this.x(), this.y() - amount);
  }

  public Coords movedDown(int amount) {
    return new Coords(this.x(), this.y() + amount);
  }

  public Coords movedLeft(int amount) {
    return new Coords(this.x() - amount, this.y());
  }

  public Coords movedRight(int amount) {
    return new Coords(this.x() + amount, this.y());
  }

  public Coords moved(Direction direction, int amount) {
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
