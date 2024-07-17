package me.roundaround.roundalib.asset.icon;

import net.minecraft.util.Identifier;

public interface Icon {
  Identifier getTexture(String modId);

  int getSize();
}
