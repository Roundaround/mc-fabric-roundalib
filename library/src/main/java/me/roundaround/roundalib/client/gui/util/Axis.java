package me.roundaround.roundalib.client.gui.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public enum Axis {
  HORIZONTAL, VERTICAL;

  public String getId() {
    return this.name().toLowerCase();
  }

  public String getI18nKey(String modId) {
    return String.format("%s.roundalib.axis.%s", modId, this.getId());
  }

  public Text getDisplayText(String modId) {
    return Text.translatable(this.getI18nKey(modId));
  }

  public String getDisplayString(String modId) {
    return I18n.translate(this.getI18nKey(modId));
  }
}
