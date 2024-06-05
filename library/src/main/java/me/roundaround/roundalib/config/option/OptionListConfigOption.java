package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.value.ListOptionValue;
import net.minecraft.text.Text;

import java.util.List;

public class OptionListConfigOption<T extends ListOptionValue<T>> extends ConfigOption<T> {
  private final List<T> values;

  protected OptionListConfigOption(Builder<T> builder) {
    super(builder);
    this.values = builder.values;
  }

  private OptionListConfigOption(OptionListConfigOption<T> other) {
    super(other);
    this.values = other.values;
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

  public List<T> getValues() {
    return this.values;
  }

  @Override
  public OptionListConfigOption<T> copy() {
    return new OptionListConfigOption<>(this);
  }

  public static <T extends ListOptionValue<T>, C extends ModConfig> OptionListConfigOption<T> defaultInstance(
      String modId, String id, String labelI18nKey, List<T> values, T defaultValue
  ) {
    return builder(modId, id, labelI18nKey, values, defaultValue).build();
  }

  public static <T extends ListOptionValue<T>, C extends ModConfig> OptionListConfigOption<T> defaultInstance(
      String modId, String id, Text label, List<T> values, T defaultValue
  ) {
    return builder(modId, id, label, values, defaultValue).build();
  }

  public static <T extends ListOptionValue<T>, C extends ModConfig> Builder<T> builder(
      String modId, String id, String labelI18nKey, List<T> values, T defaultValue
  ) {
    return new Builder<>(modId, id, labelI18nKey, values, defaultValue);
  }

  public static <T extends ListOptionValue<T>> Builder<T> builder(
      String modId, String id, Text label, List<T> values, T defaultValue
  ) {
    return new Builder<>(modId, id, label, values, defaultValue);
  }

  public static class Builder<T extends ListOptionValue<T>> extends ConfigOption.AbstractBuilder<T> {
    private final List<T> values;

    private Builder(String modId, String id, String labelI18nKey, List<T> values, T defaultValue) {
      super(modId, id, labelI18nKey, defaultValue);
      this.values = values;
    }

    private Builder(String modId, String id, Text label, List<T> values, T defaultValue) {
      super(modId, id, label, defaultValue);
      this.values = values;
    }

    @Override
    public OptionListConfigOption<T> build() {
      return new OptionListConfigOption<>(this);
    }
  }
}
