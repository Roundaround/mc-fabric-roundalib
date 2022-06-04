package me.roundaround.roundalib.config.option;

import java.util.Optional;

import me.roundaround.roundalib.config.value.ListOptionValue;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class OptionListConfigOption extends ConfigOption<ListOptionValue<?>> {
  protected OptionListConfigOption(Builder builder) {
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

  public static OptionListConfigOption defaultInstance(String id, String labelI18nKey,
      ListOptionValue<?> defaultValue) {
    return builder(id, labelI18nKey, defaultValue).build();
  }

  public static OptionListConfigOption defaultInstance(String id, Text label, ListOptionValue<?> defaultValue) {
    return builder(id, label, defaultValue).build();
  }

  private static Builder builder(String id, String labelI18nKey, ListOptionValue<?> defaultValue) {
    return new Builder(id, labelI18nKey, defaultValue);
  }

  private static Builder builder(String id, Text label, ListOptionValue<?> defaultValue) {
    return new Builder(id, label, defaultValue);
  }

  public static class Builder extends ConfigOption.Builder<ListOptionValue<?>> {
    private Builder(String id, String labelI18nKey, ListOptionValue<?> defaultValue) {
      super(id, labelI18nKey, defaultValue);
    }

    private Builder(String id, Text label, ListOptionValue<?> defaultValue) {
      super(id, label, defaultValue);
    }

    @Override
    public Builder setComment(String i18nKey) {
      comment = Optional.of(new TranslatableText(i18nKey));
      return this;
    }

    @Override
    public Builder setComment(Text comment) {
      this.comment = Optional.of(comment);
      return this;
    }

    @Override
    public Builder setUseLabelAsCommentFallback(boolean useLabelAsCommentFallback) {
      this.useLabelAsCommentFallback = useLabelAsCommentFallback;
      return this;
    }

    @Override
    public OptionListConfigOption build() {
      return new OptionListConfigOption(this);
    }
  }
}
