package me.roundaround.roundalib.client.gui.util;

public record Dimensions(int width, int height) {
  public Dimensions scaledX(int scale) {
    return this.scaled(scale, 1);
  }

  public Dimensions scaledY(int scale) {
    return this.scaled(1, scale);
  }

  public Dimensions scaled(int scale) {
    return this.scaled(scale, scale);
  }

  public Dimensions scaled(int scaleX, int scaleY) {
    return new Dimensions(this.width() * scaleX, this.height() * scaleY);
  }

  public static Dimensions zero() {
    return new Dimensions(0, 0);
  }

  public static Dimensions one() {
    return new Dimensions(1, 1);
  }

  public static Dimensions of(int width, int height) {
    return new Dimensions(width, height);
  }
}
