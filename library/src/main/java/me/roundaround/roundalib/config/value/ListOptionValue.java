package me.roundaround.roundalib.config.value;

import me.roundaround.roundalib.config.ModConfig;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

import java.util.List;

public interface ListOptionValue<T extends ListOptionValue<T>> {
  String getId();

  String getI18nKey(ModConfig config);

  default Text getDisplayText(ModConfig config) {
    return Text.translatable(this.getI18nKey(config));
  }

  default String getDisplayString(ModConfig config) {
    return I18n.translate(this.getI18nKey(config));
  }

  T getFromId(String id);

  T getNext();

  T getPrev();
}
