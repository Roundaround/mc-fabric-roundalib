package me.roundaround.roundalib.client.gui.icon;

import net.minecraft.util.Identifier;

public class CustomIcon implements Icon {
  private final String name;
  private final int size;

  public CustomIcon(String name, int size) {
    this.name = name;
    this.size = size;
  }

  @Override
  public Identifier getTexture(String modId) {
    return Identifier.of(modId, "icon/" + this.name);
  }

  @Override
  public int getSize() {
    return this.size;
  }
}
