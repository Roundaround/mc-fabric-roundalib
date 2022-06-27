package me.roundaround.roundalib.config.option;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class IntConfigOption extends ConfigOption<Integer, IntConfigOption.Builder> {
  private Optional<Integer> minValue = Optional.empty();
  private Optional<Integer> maxValue = Optional.empty();
  private Optional<Integer> step = Optional.of(1);
  private List<Validator> validators = List.of();
  private boolean slider = false;

  protected IntConfigOption(Builder builder) {
    super(builder);

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

    slider = builder.slider;
  }

  public Optional<Integer> getMinValue() {
    return minValue;
  }

  public Optional<Integer> getMaxValue() {
    return maxValue;
  }

  public int getStep() {
    return step.isEmpty() ? 1 : step.get();
  }

  public boolean increment() {
    return step(1);
  }

  public boolean decrement() {
    return step(-1);
  }

  public boolean canIncrement() {
    if (step.isEmpty()) {
      return false;
    }

    return getValue() < maxValue.orElse(Integer.MAX_VALUE);
  }

  public boolean canDecrement() {
    if (step.isEmpty()) {
      return false;
    }

    return getValue() > minValue.orElse(Integer.MIN_VALUE);
  }

  public boolean showStepButtons() {
    return step.isPresent();
  }

  public boolean useSlider() {
    return slider;
  }

  private boolean step(int mult) {
    if (step.isEmpty()) {
      return false;
    }

    int newValue = MathHelper.clamp(getValue() + step.get() * mult,
        minValue.orElse(Integer.MIN_VALUE),
        maxValue.orElse(Integer.MAX_VALUE));

    if (newValue == getValue()) {
      return false;
    }

    setValue(newValue);
    return true;
  }

  public boolean validateInput(int newValue) {
    return validators.stream().allMatch((validator) -> {
      return validator.apply(getValue(), newValue);
    });
  }

  public static Builder builder(String id, String labelI18nKey) {
    return new Builder(id, labelI18nKey);
  }

  public static Builder builder(String id, Text label) {
    return new Builder(id, label);
  }

  public static Builder sliderBuilder(String id, String labelI18nKey) {
    return builder(id, labelI18nKey).setUseSlider(true);
  }

  public static Builder sliderBuilder(String id, Text label) {
    return builder(id, label).setUseSlider(true);
  }

  public static class Builder extends ConfigOption.Builder<Integer, Builder> {
    private Optional<Integer> minValue = Optional.empty();
    private Optional<Integer> maxValue = Optional.empty();
    private Optional<Integer> step = Optional.of(1);
    private List<Validator> customValidators = new ArrayList<>();
    private boolean slider = false;

    private Builder(String id, String labelI18nKey) {
      super(id, labelI18nKey, 0);
    }

    private Builder(String id, Text label) {
      super(id, label, 0);
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

    @Override
    public IntConfigOption build() {
      if (slider && (minValue.isEmpty() || maxValue.isEmpty())) {
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
