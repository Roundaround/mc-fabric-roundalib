package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.value.EnumValue;

import java.util.List;

public class EnumConfigOption<T extends EnumValue<T>> extends ConfigOption<T> {
  private final List<T> values;

  protected EnumConfigOption(Builder<T> builder) {
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

  public static <T extends EnumValue<T>> Builder<T> builder(ConfigPath path, List<T> values) {
    return new Builder<>(path, values);
  }

  public static class Builder<T extends EnumValue<T>> extends ConfigOption.AbstractBuilder<T, EnumConfigOption<T>, Builder<T>> {
    private final List<T> values;

    private Builder(ConfigPath path, List<T> values) {
      super(path);
      this.values = values;

      this.setDefaultValue(values.getFirst());
    }

    @Override
    protected EnumConfigOption<T> buildInternal() {
      return new EnumConfigOption<>(this);
    }
  }
}
