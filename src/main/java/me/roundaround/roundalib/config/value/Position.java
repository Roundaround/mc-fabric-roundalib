package me.roundaround.roundalib.config.value;

public record Position(int x, int y) {
  public Position movedUp(int amount) {
    return new Position(x(), y() - amount);
  }
  
  public Position movedDown(int amount) {
    return new Position(x(), y() + amount);
  }
  
  public Position movedLeft(int amount) {
    return new Position(x() - amount, y());
  }
  
  public Position movedRight(int amount) {
    return new Position(x() + amount, y());
  }

  @Override
  public String toString() {
    return serialize(this);
  }

  public static Position deserialize(String serialized) {
    String[] split = serialized.substring(1, serialized.length() - 1).split(",");
    int x = Integer.parseInt(split[0]);
    int y = Integer.parseInt(split[1]);
    return new Position(x, y);
  }

  public static String serialize(Position value) {
    return String.format("(%d,%d)", value.x(), value.y());
  }
}
