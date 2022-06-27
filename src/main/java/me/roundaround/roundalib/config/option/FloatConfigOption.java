package me.roundaround.roundalib.config.option;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.text.Text;

public class FloatConfigOption extends ConfigOption<Float, FloatConfigOption.Builder> {
  private Optional<Float> minValue = Optional.empty();
  private Optional<Float> maxValue = Optional.empty();
  private List<Validator> validators = List.of();
  private boolean slider = false;
  private Optional<Integer> step = Optional.of(20);

  protected FloatConfigOption(Builder builder) {
    super(builder);

    minValue = builder.minValue;
    maxValue = builder.maxValue;

    List<Validator> allValidators = new ArrayList<>();
    if (minValue.isPresent()) {
      allValidators.add((float prev, float curr) -> curr >= minValue.get());
    }
    if (maxValue.isPresent()) {
      allValidators.add((float prev, float curr) -> curr <= maxValue.get());
    }
    if (!builder.customValidators.isEmpty()) {
      allValidators.addAll(builder.customValidators);
    }
    validators = List.copyOf(allValidators);

    slider = builder.slider;
    step = builder.step;
  }

  @Override
  public void deserialize(Object data) {
    // Getting around a weird issue where the default deserializes into a Double
    setValue(((Double) data).floatValue());
  }

  public Optional<Float> getMinValue() {
    return minValue;
  }

  public Optional<Float> getMaxValue() {
    return maxValue;
  }

  public boolean validateInput(float newValue) {
    return validators.stream().allMatch((validator) -> {
      return validator.apply(getValue(), newValue);
    });
  }

  public boolean useSlider() {
    return slider;
  }

  public int getStep() {
    return step.isEmpty() ? 20 : step.get();
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

  public static class Builder extends ConfigOption.Builder<Float, Builder> {
    private Optional<Float> minValue = Optional.empty();
    private Optional<Float> maxValue = Optional.empty();
    private List<Validator> customValidators = new ArrayList<>();
    private boolean slider = false;
    private Optional<Integer> step = Optional.of(20);

    private Builder(String id, String labelI18nKey) {
      super(id, labelI18nKey, 0f);
    }

    private Builder(String id, Text label) {
      super(id, label, 0f);
    }

    public Builder setDefaultValue(float defaultValue) {
      this.defaultValue = defaultValue;
      return this;
    }

    public Builder setMinValue(float minValue) {
      this.minValue = Optional.of(minValue);
      return this;
    }

    public Builder setMaxValue(float maxValue) {
      this.maxValue = Optional.of(maxValue);
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

    public Builder setStep(int step) {
      this.step = Optional.of(step);
      return this;
    }

    @Override
    public FloatConfigOption build() {
      return new FloatConfigOption(this);
    }
  }

  @FunctionalInterface
  public static interface Validator {
    boolean apply(float prev, float curr);
  }
}
