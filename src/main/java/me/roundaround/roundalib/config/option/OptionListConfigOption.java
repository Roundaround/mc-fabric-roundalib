package me.roundaround.roundalib.config.option;

import java.util.Optional;

import me.roundaround.roundalib.config.value.ListOptionValue;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class OptionListConfigOption<T extends ListOptionValue<T>> extends ConfigOption<T> {
  protected OptionListConfigOption(Builder<T> builder) {
    super(builder);
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

  public static <T extends ListOptionValue<T>> OptionListConfigOption<T> defaultInstance(
      String id,
      String labelI18nKey,
      T defaultValue) {
    return builder(id, labelI18nKey, defaultValue).build();
  }

  public static <T extends ListOptionValue<T>> OptionListConfigOption<T> defaultInstance(
      String id,
      Text label,
      T defaultValue) {
    return builder(id, label, defaultValue).build();
  }

  private static <T extends ListOptionValue<T>> Builder<T> builder(
      String id,
      String labelI18nKey,
      T defaultValue) {
    return new Builder<T>(id, labelI18nKey, defaultValue);
  }

  private static <T extends ListOptionValue<T>> Builder<T> builder(
      String id,
      Text label,
      T defaultValue) {
    return new Builder<T>(id, label, defaultValue);
  }

  public static class Builder<T extends ListOptionValue<T>> extends ConfigOption.Builder<T> {
    private Builder(String id, String labelI18nKey, T defaultValue) {
      super(id, labelI18nKey, defaultValue);
    }

    private Builder(String id, Text label, T defaultValue) {
      super(id, label, defaultValue);
    }

    @Override
    public Builder<T> setComment(String i18nKey) {
      comment = Optional.of(new TranslatableText(i18nKey));
      return this;
    }

    @Override
    public Builder<T> setComment(Text comment) {
      this.comment = Optional.of(comment);
      return this;
    }

    @Override
    public Builder<T> setUseLabelAsCommentFallback(boolean useLabelAsCommentFallback) {
      this.useLabelAsCommentFallback = useLabelAsCommentFallback;
      return this;
    }

    @Override
    public OptionListConfigOption<T> build() {
      return new OptionListConfigOption<>(this);
    }
  }
}
