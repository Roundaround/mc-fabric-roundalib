package me.roundaround.roundalib.config.value;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public interface EnumValue<T extends EnumValue<T>> {
  String getId();

  String getI18nKey(String modId);

  default Component getDisplayText(String modId) {
    return Component.translatable(this.getI18nKey(modId));
  }

  default String getDisplayString(String modId) {
    return I18n.get(this.getI18nKey(modId));
  }

  T getFromId(String id);

  T getNext();

  T getPrev();
}
