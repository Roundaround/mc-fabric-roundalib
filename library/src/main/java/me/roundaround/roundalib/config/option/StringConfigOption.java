package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ModConfig;

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
    if (this.maxLength.isPresent()) {
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

  public boolean validateInput(String newValue) {
    return this.validators.stream().allMatch((validator) -> {
      return validator.apply(getPendingValue(), newValue);
    });
  }

  public static Builder builder(ModConfig modConfig, String id) {
    return new Builder(modConfig, id);
  }

  public static class Builder extends ConfigOption.AbstractBuilder<String, Builder> {
    private Optional<Integer> minLength = Optional.empty();
    private Optional<Integer> maxLength = Optional.empty();
    private Optional<Pattern> regex = Optional.empty();
    private List<Validator> customValidators = new ArrayList<>();

    private Builder(ModConfig modConfig, String id) {
      super(modConfig, id);
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
