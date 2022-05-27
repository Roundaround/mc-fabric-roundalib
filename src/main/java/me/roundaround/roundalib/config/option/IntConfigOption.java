package me.roundaround.roundalib.config.option;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.gui.control.IntInputControl;
import net.minecraft.util.math.MathHelper;

public class IntConfigOption extends ConfigOption<Integer, IntInputControl> {
  private Options options;

  public IntConfigOption(String id, String labelI18nKey, Integer defaultValue) {
    this(id, labelI18nKey, defaultValue, Options.getDefault());
  }

  public IntConfigOption(String id, String labelI18nKey, Integer defaultValue, Options options) {
    super(id, labelI18nKey, defaultValue);
    this.options = options;
  }

  @Override
  public Integer deserializeFromJson(JsonElement data) {
    return data.getAsInt();
  }

  @Override
  public JsonElement serializeToJson() {
    return new JsonPrimitive(this.getValue());
  }

  @Override
  public IntInputControl createControl(OptionRow parent, int top, int left, int height, int width) {
    return new IntInputControl(this, parent, top, left, height, width);
  }

  public boolean increment() {
    return step(1);
  }

  public boolean decrement() {
    return step(-1);
  }

  public boolean canIncrement() {
    if (options.step.isEmpty()) {
      return false;
    }

    return getValue() < options.maxValue.orElse(Integer.MAX_VALUE);
  }

  public boolean canDecrement() {
    if (options.step.isEmpty()) {
      return false;
    }

    return getValue() > options.minValue.orElse(Integer.MIN_VALUE);
  }

  public boolean showStepButtons() {
    return options.step.isPresent();
  }

  private boolean step(int mult) {
    if (options.step.isEmpty()) {
      return false;
    }

    int newValue = MathHelper.clamp(getValue() + options.step.get() * mult,
        options.minValue.orElse(Integer.MIN_VALUE),
        options.maxValue.orElse(Integer.MAX_VALUE));

    if (newValue == getValue()) {
      return false;
    }

    setValue(newValue);
    return true;
  }

  public boolean validateInput(int newValue) {
    return options.validators.stream().allMatch((validator) -> {
      return validator.apply(getValue(), newValue);
    });
  }

  public static class Options {
    private Optional<Integer> minValue = Optional.empty();
    private Optional<Integer> maxValue = Optional.empty();
    private Optional<Integer> step = Optional.of(1);
    private List<Validator> validators = List.of();

    private Options() {
    }

    private Options(Builder builder) {
      minValue = builder.minValue;
      maxValue = builder.maxValue;
      step = builder.step;

      List<Validator> allValidators = new ArrayList<>();
      if (minValue.isPresent()) {
        allValidators.add((int prev, int curr) -> curr >= minValue.get());
      }
      if (maxValue.isPresent()) {
        allValidators.add((int prev, int curr) -> curr <= maxValue.get());
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

    public Optional<Integer> getStep() {
      return step;
    }

    public List<Validator> getValidators() {
      return validators;
    }

    public final static class Builder {
      private Optional<Integer> minValue = Optional.empty();
      private Optional<Integer> maxValue = Optional.empty();
      private Optional<Integer> step = Optional.of(1);
      private List<Validator> customValidators = new ArrayList<>();

      public Builder setMinValue(int minValue) {
        this.minValue = Optional.of(minValue);
        return this;
      }

      public Builder setMaxValue(int maxValue) {
        this.maxValue = Optional.of(maxValue);
        return this;
      }

      public Builder setStep(int step) {
        this.step = Optional.of(step);
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
    boolean apply(int prev, int curr);
  }
}
