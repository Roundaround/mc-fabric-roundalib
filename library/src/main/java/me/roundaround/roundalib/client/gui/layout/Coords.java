package me.roundaround.roundalib.client.gui.layout;

public record Coords(int x, int y) {
  public int u() {
    return this.x();
  }

  public int v() {
    return this.y();
  }

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

  public Coords scaledX(int scale) {
    return this.scaled(scale, 1);
  }

  public Coords scaledY(int scale) {
    return this.scaled(1, scale);
  }

  public Coords scaled(int scale) {
    return this.scaled(scale, scale);
  }

  public Coords scaled(int scaleX, int scaleY) {
    return new Coords(this.x() * scaleX, this.y() * scaleY);
  }

  public static Coords zero() {
    return new Coords(0, 0);
  }

  public static Coords one() {
    return new Coords(1, 1);
  }

  public static Coords at(int x, int y) {
    return new Coords(x, y);
  }

  public static Coords of(int pos) {
    return new Coords(pos, pos);
  }

  public enum Direction {
    LEFT, UP, RIGHT, DOWN
  }
}
