package me.roundaround.roundalib.client.gui.layout;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface DefaultLayoutMargin {
  Spacing getDefaultLayoutMargin();
}
