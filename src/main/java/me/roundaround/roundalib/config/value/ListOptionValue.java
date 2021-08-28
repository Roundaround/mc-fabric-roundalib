package me.roundaround.roundalib.config.value;

import net.minecraft.client.resource.language.I18n;

public interface ListOptionValue<T extends ListOptionValue<T>> {
  String getId();

  String getI18nKey();

  default String getDisplayString() {
    return I18n.translate(this.getI18nKey());
  }

  T getFromId(String id);

  T getNext();

  T getPrev();
}
