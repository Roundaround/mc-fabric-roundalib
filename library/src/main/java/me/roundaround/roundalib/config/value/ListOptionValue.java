package me.roundaround.roundalib.config.value;

import me.roundaround.roundalib.config.ModConfig;
import net.minecraft.client.resource.language.I18n;

public interface ListOptionValue<T extends ListOptionValue<T>> {
  String getId();

  String getI18nKey(ModConfig config);

  default String getDisplayString(ModConfig config) {
    return I18n.translate(this.getI18nKey(config));
  }

  T getFromId(String id);

  T getNext();

  T getPrev();
}
