package me.roundaround.roundalib.client.gui;

public interface PositionalEntry {
  int getTop();
  int getHeight();

  default int getBottom() {
    return getTop() + getHeight();
  }
}
