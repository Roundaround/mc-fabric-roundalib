package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.value.ListOptionValue;

import java.util.List;

public class OptionListConfigOption<T extends ListOptionValue<T>> extends ConfigOption<T> {
  private final List<T> values;

  protected OptionListConfigOption(Builder<T> builder) {
    super(builder);
    this.values = builder.values;
  }

  @Override
  public void deserialize(Object data) {
    this.setValue(this.getDefaultValue().getFromId((String) data));
  }

  @Override
  public Object serialize() {
    return this.getPendingValue().getId();
  }

  public void setNext() {
    this.setValue(this.getPendingValue().getNext());
  }

  public void setPrev() {
    this.setValue(this.getPendingValue().getPrev());
  }

  public List<T> getValues() {
    return this.values;
  }

  public static <T extends ListOptionValue<T>> Builder<T> builder(
      ModConfig modConfig, String id, List<T> values
  ) {
    return new Builder<>(modConfig, id, values);
  }

  public static class Builder<T extends ListOptionValue<T>> extends ConfigOption.AbstractBuilder<T, Builder<T>> {
    private final List<T> values;

    private Builder(ModConfig modConfig, String id, List<T> values) {
      super(modConfig, id);
      this.values = values;

      this.setDefaultValue(values.getFirst());
    }

    @Override
    public OptionListConfigOption<T> build() {
      return new OptionListConfigOption<>(this);
    }
  }
}
