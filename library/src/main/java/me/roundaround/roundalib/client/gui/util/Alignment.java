package me.roundaround.roundalib.client.gui.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum Alignment {
  START(0f), CENTER(0.5f), END(1f);

  private final float floatValue;

  Alignment(float floatValue) {
    this.floatValue = floatValue;
  }

  public int getPos(int base, int size) {
    return base - (int) (size * this.floatValue);
  }

  public float floatValue() {
    return this.floatValue;
  }
}
