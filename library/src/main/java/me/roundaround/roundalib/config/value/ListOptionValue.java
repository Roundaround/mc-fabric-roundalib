package me.roundaround.roundalib.config.value;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

public interface ListOptionValue<T extends ListOptionValue<T>> {
  String getId();

  String getI18nKey(String modId);

  default Text getDisplayText(String modId) {
    return Text.translatable(this.getI18nKey(modId));
  }

  default String getDisplayString(String modId) {
    return I18n.translate(this.getI18nKey(modId));
  }

  T getFromId(String id);

  T getNext();

  T getPrev();
}
