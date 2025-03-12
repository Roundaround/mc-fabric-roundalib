package me.roundaround.roundalib.config.value;

import java.util.List;

public record Position(int x, int y) {
  public Position movedUp(int amount) {
    return new Position(this.x(), this.y() - amount);
  }

  public Position movedDown(int amount) {
    return new Position(this.x(), this.y() + amount);
  }

  public Position movedLeft(int amount) {
    return new Position(this.x() - amount, this.y());
  }

  public Position movedRight(int amount) {
    return new Position(this.x() + amount, this.y());
  }

  public Position moved(Direction direction, int amount) {
    return switch (direction) {
      case LEFT -> this.movedLeft(amount);
      case UP -> this.movedUp(amount);
      case RIGHT -> this.movedRight(amount);
      case DOWN -> this.movedDown(amount);
    };
  }

  @Override
  public String toString() {
    return String.format("(%d,%d)", this.x(), this.y());
  }

  public static Position fromString(String data) {
    String[] split = data.substring(1, data.length() - 1).split(",");
    int x = Integer.parseInt(split[0]);
    int y = Integer.parseInt(split[1]);
    return new Position(x, y);
  }

  public static Position fromList(List<Integer> data) {
    return new Position(data.get(0), data.get(1));
  }

  public enum Direction {
    LEFT, UP, RIGHT, DOWN
  }
}
