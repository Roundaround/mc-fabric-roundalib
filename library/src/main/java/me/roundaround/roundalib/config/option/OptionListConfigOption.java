package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.value.ListOptionValue;
import net.minecraft.text.Text;

public class OptionListConfigOption<T extends ListOptionValue<T>>
    extends ConfigOption<T, OptionListConfigOption.Builder<T>> {
  protected OptionListConfigOption(Builder<T> builder) {
    super(builder);
  }

  private OptionListConfigOption(OptionListConfigOption<T> other) {
    super(other);
  }

  @Override
  public void deserialize(Object data) {
    setValue(getValue().getFromId((String) data));
  }

  @Override
  public Object serialize() {
    return getValue().getId();
  }

  public void setNext() {
    setValue(getValue().getNext());
  }

  public void setPrev() {
    setValue(getValue().getPrev());
  }

  @Override
  public OptionListConfigOption<T> copy() {
    return new OptionListConfigOption<>(this);
  }

  public static <T extends ListOptionValue<T>, C extends ModConfig> OptionListConfigOption<T> defaultInstance(
      ModConfig config,
      String id,
      String labelI18nKey,
      T defaultValue) {
    return builder(config, id, labelI18nKey, defaultValue).build();
  }

  public static <T extends ListOptionValue<T>, C extends ModConfig> OptionListConfigOption<T> defaultInstance(
      ModConfig config,
      String id,
      Text label,
      T defaultValue) {
    return builder(config, id, label, defaultValue).build();
  }

  public static <T extends ListOptionValue<T>, C extends ModConfig> Builder<T> builder(
      ModConfig config,
      String id,
      String labelI18nKey,
      T defaultValue) {
    return new Builder<>(config, id, labelI18nKey, defaultValue);
  }

  public static <T extends ListOptionValue<T>> Builder<T> builder(
      ModConfig config,
      String id,
      Text label,
      T defaultValue) {
    return new Builder<>(config, id, label, defaultValue);
  }

  public static class Builder<T extends ListOptionValue<T>>
      extends ConfigOption.AbstractBuilder<T, Builder<T>> {
    private Builder(ModConfig config, String id, String labelI18nKey, T defaultValue) {
      super(config, id, labelI18nKey, defaultValue);
    }

    private Builder(ModConfig config, String id, Text label, T defaultValue) {
      super(config, id, label, defaultValue);
    }

    @Override
    public OptionListConfigOption<T> build() {
      return new OptionListConfigOption<>(this);
    }
  }
}
