package me.roundaround.roundalib.client.gui;

import net.minecraft.client.gui.Drawable;

public interface DrawableBuilder<T extends Drawable> {
  T build();
}
