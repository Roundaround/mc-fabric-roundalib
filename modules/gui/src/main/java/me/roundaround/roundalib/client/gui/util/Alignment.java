package me.roundaround.roundalib.client.gui.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

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

  public int getPosInContainer(int base, int containerSize, int elementSize) {
    return base + (int) ((containerSize - elementSize) * this.floatValue);
  }

  public float floatValue() {
    return this.floatValue;
  }

  public String getId() {
    return this.name().toLowerCase();
  }

  public String getI18nKey(String modId) {
    return String.format("%s.roundalib.alignment.%s", modId, this.getId());
  }

  public Text getDisplayText(String modId) {
    return Text.translatable(this.getI18nKey(modId));
  }

  public String getDisplayString(String modId) {
    return I18n.translate(this.getI18nKey(modId));
  }

  public String getI18nKey(String modId, Axis axis) {
    return String.format("%s.roundalib.alignment.%s.%s", modId, this.getId(), axis.getId());
  }

  public Text getDisplayText(String modId, Axis axis) {
    return Text.translatable(this.getI18nKey(modId, axis));
  }

  public String getDisplayString(String modId, Axis axis) {
    return I18n.translate(this.getI18nKey(modId, axis));
  }
}
