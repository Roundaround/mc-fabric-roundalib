package me.roundaround.roundalib.client.gui.icon;

import net.minecraft.resources.Identifier;

public interface Icon {
  Identifier getTexture(String modId);

  int getSize();
}
