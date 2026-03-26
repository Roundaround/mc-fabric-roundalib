package me.roundaround.roundalib.client.gui.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public enum Axis {
  HORIZONTAL, VERTICAL;

  public String getId() {
    return this.name().toLowerCase();
  }

  public String getI18nKey(String modId) {
    return String.format("%s.roundalib.axis.%s", modId, this.getId());
  }

  public Component getDisplayText(String modId) {
    return Component.translatable(this.getI18nKey(modId));
  }

  public String getDisplayString(String modId) {
    return I18n.get(this.getI18nKey(modId));
  }
}
