package me.roundaround.roundalib.config.option;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import me.roundaround.roundalib.config.ModConfig;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class IntConfigOption extends ConfigOption<Integer, IntConfigOption.Builder> {
  private Optional<Integer> minValue = Optional.empty();
  private Optional<Integer> maxValue = Optional.empty();
  private Optional<Integer> step = Optional.of(1);
  private List<Validator> validators = List.of();
  private boolean slider = false;
  private Function<Integer, String> valueDisplayFunction = (Integer value) -> value.toString();

  protected IntConfigOption(Builder builder) {
    super(builder);

    this.minValue = builder.minValue;
    this.maxValue = builder.maxValue;
    this.step = builder.step;

    List<Validator> allValidators = new ArrayList<>();
    if (this.minValue.isPresent()) {
      allValidators.add((int prev, int curr) -> curr >= this.minValue.get());
    }
    if (this.maxValue.isPresent()) {
      allValidators.add((int prev, int curr) -> curr <= this.maxValue.get());
    }
    if (!builder.customValidators.isEmpty()) {
      allValidators.addAll(builder.customValidators);
    }
    this.validators = List.copyOf(allValidators);

    this.slider = builder.slider;
    this.valueDisplayFunction = builder.valueDisplayFunction;
  }

  private IntConfigOption(IntConfigOption other) {
    super(other);

    this.minValue = other.minValue;
    this.maxValue = other.maxValue;
    this.step = other.step;
    this.validators = other.validators;
    this.slider = other.slider;
    this.valueDisplayFunction = other.valueDisplayFunction;
  }

  public Optional<Integer> getMinValue() {
    return this.minValue;
  }

  public Optional<Integer> getMaxValue() {
    return this.maxValue;
  }

  public int getStep() {
    return this.step.isEmpty() ? 1 : this.step.get();
  }

  public boolean increment() {
    return step(1);
  }

  public boolean decrement() {
    return step(-1);
  }

  public boolean canIncrement() {
    if (this.step.isEmpty()) {
      return false;
    }

    return getValue() < maxValue.orElse(Integer.MAX_VALUE);
  }

  public boolean canDecrement() {
    if (this.step.isEmpty()) {
      return false;
    }

    return getValue() > this.minValue.orElse(Integer.MIN_VALUE);
  }

  public boolean showStepButtons() {
    return this.step.isPresent();
  }

  public boolean useSlider() {
    return this.slider;
  }

  private boolean step(int mult) {
    if (this.step.isEmpty()) {
      return false;
    }

    int newValue = MathHelper.clamp(getValue() + this.step.get() * mult,
        this.minValue.orElse(Integer.MIN_VALUE),
        this.maxValue.orElse(Integer.MAX_VALUE));

    if (newValue == getValue()) {
      return false;
    }

    setValue(newValue);
    return true;
  }

  public String getValueAsString() {
    return this.valueDisplayFunction.apply(getValue());
  }

  public boolean validateInput(int newValue) {
    return this.validators.stream().allMatch((validator) -> {
      return validator.apply(getValue(), newValue);
    });
  }

  @Override
  public IntConfigOption copy() {
    return new IntConfigOption(this);
  }

  public static Builder builder(ModConfig config, String id, String labelI18nKey) {
    return new Builder(config, id, labelI18nKey);
  }

  public static Builder builder(ModConfig config, String id, Text label) {
    return new Builder(config, id, label);
  }

  public static Builder sliderBuilder(ModConfig config, String id, String labelI18nKey) {
    return builder(config, id, labelI18nKey).setUseSlider(true);
  }

  public static Builder sliderBuilder(ModConfig config, String id, Text label) {
    return builder(config, id, label).setUseSlider(true);
  }

  public static class Builder extends ConfigOption.AbstractBuilder<Integer, Builder> {
    private Optional<Integer> minValue = Optional.empty();
    private Optional<Integer> maxValue = Optional.empty();
    private Optional<Integer> step = Optional.of(1);
    private List<Validator> customValidators = new ArrayList<>();
    private boolean slider = false;
    private Function<Integer, String> valueDisplayFunction = (Integer value) -> value.toString();

    private Builder(ModConfig config, String id, String labelI18nKey) {
      super(config, id, labelI18nKey, 0);
    }

    private Builder(ModConfig config, String id, Text label) {
      super(config, id, label, 0);
    }

    public Builder setDefaultValue(int defaultValue) {
      this.defaultValue = defaultValue;
      return this;
    }

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

    public Builder setUseSlider(boolean slider) {
      this.slider = slider;
      return this;
    }

    public Builder setValueDisplayFunction(Function<Integer, String> valueDisplayFunction) {
      this.valueDisplayFunction = valueDisplayFunction;
      return this;
    }

    @Override
    public IntConfigOption build() {
      if (this.slider && (this.minValue.isEmpty() || this.maxValue.isEmpty())) {
        throw new IllegalStateException();
      }
      return new IntConfigOption(this);
    }
  }

  @FunctionalInterface
  public static interface Validator {
    boolean apply(int prev, int curr);
  }
}
