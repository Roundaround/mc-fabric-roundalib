package me.roundaround.roundalib.config.option;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.gui.control.TextInputControl;

public class StringConfigOption extends ConfigOption<String, TextInputControl> {
  private Options options;

  public StringConfigOption(String id, String labelI18nKey, String defaultValue) {
    this(id, labelI18nKey, defaultValue, Options.getDefault());
  }

  public StringConfigOption(String id, String labelI18nKey, String defaultValue, Options options) {
    super(id, labelI18nKey, defaultValue);
    this.options = options;
  }

  @Override
  public String deserializeFromJson(JsonElement data) {
    return data.getAsString();
  }

  @Override
  public JsonElement serializeToJson() {
    return new JsonPrimitive(this.getValue());
  }

  @Override
  public TextInputControl createControl(OptionRow parent, int top, int left, int height, int width) {
    return new TextInputControl(this, parent, top, left, height, width);
  }

  public boolean validateInput(String newValue) {
    // TODO: Return a result object with details about which validator failed,
    // show a tooltip with error?
    return options.validators.stream().allMatch((validator) -> {
      return validator.apply(getValue(), newValue);
    });
  }

  public static class Options {
    private Optional<Integer> minLength = Optional.empty();
    private Optional<Integer> maxLength = Optional.empty();
    private Optional<Pattern> regex = Optional.empty();
    private List<Validator> validators = List.of();

    private Options() {
    }

    private Options(Builder builder) {
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

    public static Options getDefault() {
      return builder().build();
    }

    public static Builder builder() {
      return new Builder();
    }

    public List<Validator> getValidators() {
      return validators;
    }

    public final static class Builder {
      private Optional<Integer> minLength = Optional.empty();
      private Optional<Integer> maxLength = Optional.empty();
      private Optional<Pattern> regex = Optional.empty();
      private List<Validator> customValidators = new ArrayList<>();

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

      public Options build() {
        return new Options(this);
      }
    }
  }

  @FunctionalInterface
  public static interface Validator {
    boolean apply(String prev, String curr);
  }
}
