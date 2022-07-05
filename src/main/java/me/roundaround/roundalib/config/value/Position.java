package me.roundaround.roundalib.config.value;

public record Position(int x, int y) {
  public Position movedUp(int amount) {
    return new Position(x() - amount, y());
  }
  
  public Position movedDown(int amount) {
    return new Position(x() + amount, y());
  }
  
  public Position movedLeft(int amount) {
    return new Position(x(), y() - amount);
  }
  
  public Position movedRight(int amount) {
    return new Position(x(), y() + amount);
  }
}
