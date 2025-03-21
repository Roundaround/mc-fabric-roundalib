package me.roundaround.roundalib.client.gui.icon;

import net.minecraft.util.Identifier;

public interface Icon {
  Identifier getTexture(String modId);

  int getSize();
}
