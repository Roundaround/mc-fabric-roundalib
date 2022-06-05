package me.roundaround.roundalib.config.option;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import net.minecraft.text.Text;

public class StringConfigOption extends ConfigOption<String> {
  private Optional<Integer> minLength = Optional.empty();
  private Optional<Integer> maxLength = Optional.empty();
  private Optional<Pattern> regex = Optional.empty();
  private List<Validator> validators = List.of();

  protected StringConfigOption(Builder builder) {
    super(builder);

    minLength = builder.minLength;
    maxLength = builder.maxLength;
    regex = builder.regex;

    List<Validator> allValidators = new ArrayList<>();
    if (minLength.isPresent()) {
      allValidators.add((String prev, String curr) -> curr != null && curr.length() >= minLength.get());
    }
    if (maxLength.isPresent()) {
      allValidators.add((String prev, String curr) -> curr != null && curr.length() <= maxLength.get());
    }
    if (regex.isPresent()) {
      allValidators.add((String prev, String curr) -> curr != null && regex.get().matcher(curr).find());
    }
    if (!builder.customValidators.isEmpty()) {
      allValidators.addAll(builder.customValidators);
    }
    validators = List.copyOf(allValidators);
  }

  public boolean validateInput(String newValue) {
    // TODO: Return a result object with details about which validator failed,
    // show a tooltip with error?
    return validators.stream().allMatch((validator) -> {
      return validator.apply(getValue(), newValue);
    });
  }

  public static StringConfigOption defaultInstance(String id, String labelI18nKey, String defaultValue) {
    return builder(id, labelI18nKey).setDefaultValue(defaultValue).build();
  }

  public static StringConfigOption defaultInstance(String id, Text label, String defaultValue) {
    return builder(id, label).setDefaultValue(defaultValue).build();
  }

  public static Builder builder(String id, String labelI18nKey) {
    return new Builder(id, labelI18nKey);
  }

  public static Builder builder(String id, Text label) {
    return new Builder(id, label);
  }

  public static class Builder extends ConfigOption.Builder<String> {
    private Optional<Integer> minLength = Optional.empty();
    private Optional<Integer> maxLength = Optional.empty();
    private Optional<Pattern> regex = Optional.empty();
    private List<Validator> customValidators = new ArrayList<>();

    private Builder(String id, String labelI18nKey) {
      super(id, labelI18nKey, "");
    }

    private Builder(String id, Text label) {
      super(id, label, "");
    }

    public Builder setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
      return this;
    }

    public Builder setMinLength(int minLength) {
      this.minLength = Optional.of(minLength);
      return this;
    }

    public Builder setMaxLength(int maxLength) {
      this.maxLength = Optional.of(maxLength);
      return this;
    }

    public Builder setRegex(Pattern regex) {
      this.regex = Optional.of(regex);
      return this;
    }

    public Builder addCustomValidator(Validator validator) {
      customValidators.add(validator);
      return this;
    }

    public Builder setComment(String comment) {
      this.comment = Optional.of(comment);
      return this;
    }

    @Override
    public Builder setUseLabelAsCommentFallback(boolean useLabelAsCommentFallback) {
      this.useLabelAsCommentFallback = useLabelAsCommentFallback;
      return this;
    }

    @Override
    public StringConfigOption build() {
      return new StringConfigOption(this);
    }
  }

  @FunctionalInterface
  public static interface Validator {
    boolean apply(String prev, String curr);
  }
}
