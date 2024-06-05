package me.roundaround.roundalib.config.option;

import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class StringConfigOption extends ConfigOption<String> {
  private Optional<Integer> minLength = Optional.empty();
  private Optional<Integer> maxLength = Optional.empty();
  private Optional<Pattern> regex = Optional.empty();
  private List<Validator> validators = List.of();

  protected StringConfigOption(Builder builder) {
    super(builder);

    this.minLength = builder.minLength;
    this.maxLength = builder.maxLength;
    this.regex = builder.regex;

    List<Validator> allValidators = new ArrayList<>();
    if (this.minLength.isPresent()) {
      allValidators.add((String prev, String curr) -> curr != null && curr.length() >= this.minLength.get());
    }
    if (maxLength.isPresent()) {
      allValidators.add((String prev, String curr) -> curr != null && curr.length() <= this.maxLength.get());
    }
    if (this.regex.isPresent()) {
      allValidators.add((String prev, String curr) -> curr != null && this.regex.get().matcher(curr).find());
    }
    if (!builder.customValidators.isEmpty()) {
      allValidators.addAll(builder.customValidators);
    }
    this.validators = List.copyOf(allValidators);
  }

  private StringConfigOption(StringConfigOption other) {
    super(other);

    this.minLength = other.minLength;
    this.maxLength = other.maxLength;
    this.regex = other.regex;
    this.validators = other.validators;
  }

  public boolean validateInput(String newValue) {
    return this.validators.stream().allMatch((validator) -> {
      return validator.apply(getValue(), newValue);
    });
  }

  @Override
  public StringConfigOption copy() {
    return new StringConfigOption(this);
  }

  public static StringConfigOption defaultInstance(String modId, String id, String labelI18nKey, String defaultValue) {
    return builder(modId, id, labelI18nKey).setDefaultValue(defaultValue).build();
  }

  public static StringConfigOption defaultInstance(String modId, String id, Text label, String defaultValue) {
    return builder(modId, id, label).setDefaultValue(defaultValue).build();
  }

  public static Builder builder(String modId, String id, String labelI18nKey) {
    return new Builder(modId, id, labelI18nKey);
  }

  public static Builder builder(String modId, String id, Text label) {
    return new Builder(modId, id, label);
  }

  public static class Builder extends ConfigOption.AbstractBuilder<String> {
    private Optional<Integer> minLength = Optional.empty();
    private Optional<Integer> maxLength = Optional.empty();
    private Optional<Pattern> regex = Optional.empty();
    private List<Validator> customValidators = new ArrayList<>();

    private Builder(String modId, String id, String labelI18nKey) {
      super(modId, id, labelI18nKey, "");
    }

    private Builder(String modId, String id, Text label) {
      super(modId, id, label, "");
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
      this.customValidators.add(validator);
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
