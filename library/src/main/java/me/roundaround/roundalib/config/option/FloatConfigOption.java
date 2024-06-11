package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ModConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class FloatConfigOption extends ConfigOption<Float> {
  private final Float minValue;
  private final Float maxValue;
  private final boolean slider;
  private final float step;

  protected FloatConfigOption(Builder builder) {
    super(builder);

    this.minValue = builder.minValue;
    this.maxValue = builder.maxValue;
    this.slider = builder.slider;
    this.step = builder.step;

    List<Validator> allValidators = new ArrayList<>();
    if (this.minValue.isPresent()) {
      allValidators.add((float prev, float curr) -> curr >= minValue.get());
    }
    if (this.maxValue.isPresent()) {
      allValidators.add((float prev, float curr) -> curr <= maxValue.get());
    }
    if (!builder.validators.isEmpty()) {
      allValidators.addAll(builder.validators);
    }
    this.validators = List.copyOf(allValidators);

    this.slider = builder.slider;
    this.step = builder.step;
    this.valueDisplayFunction = builder.valueDisplayFunction;
  }

  @Override
  public void deserialize(Object data) {
    // Getting around a weird issue where the default deserializes into a Double
    setValue(((Double) data).floatValue());
  }

  public Float getMinValue() {
    return this.minValue;
  }

  public Float getMaxValue() {
    return this.maxValue;
  }

  public boolean validateInput(float newValue) {
    return this.validators.stream().allMatch((validator) -> {
      return validator.apply(getPendingValue(), newValue);
    });
  }

  public boolean useSlider() {
    return this.slider;
  }

  public int getStep() {
    return this.step.isEmpty() ? 20 : this.step.get();
  }

  @Override
  public boolean areValuesEqual(Float a, Float b) {
    return Math.abs(a - b) < 0x1.0p-10f;
  }

  public static Builder builder(ModConfig modConfig, String id) {
    return new Builder(modConfig, id);
  }

  public static Builder sliderBuilder(ModConfig modConfig, String id) {
    return builder(modConfig, id).setUseSlider(true);
  }

  public static class Builder extends ConfigOption.AbstractBuilder<Float, Builder> {
    private final List<Validator> validators = new ArrayList<>();

    private Float minValue = null;
    private Float maxValue = null;
    private boolean slider = false;
    private float step = 20;
    private Function<Float, String> valueDisplayFunction = (Float value) -> String.format("%.2f", value);

    private Builder(ModConfig modConfig, String id) {
      super(modConfig, id);
    }

    public Builder setMinValue(float minValue) {
      this.minValue = minValue;
      return this;
    }

    public Builder setMaxValue(float maxValue) {
      this.maxValue = maxValue;
      return this;
    }

    public Builder addCustomValidator(Validator validator) {
      validators.add(validator);
      return this;
    }

    public Builder setUseSlider(boolean slider) {
      this.slider = slider;
      return this;
    }

    public Builder setStep(float step) {
      this.step = step;
      return this;
    }

    public Builder setValueDisplayFunction(Function<Float, String> valueDisplayFunction) {
      this.valueDisplayFunction = valueDisplayFunction;
      return this;
    }

    @Override
    public FloatConfigOption build() {
      return new FloatConfigOption(this);
    }
  }

  @FunctionalInterface
  public interface Validator {
    boolean apply(float prev, float curr);
  }
}
