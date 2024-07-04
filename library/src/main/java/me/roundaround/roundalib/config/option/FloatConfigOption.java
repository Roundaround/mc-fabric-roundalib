package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.Config;
import me.roundaround.roundalib.config.panic.IllegalArgumentPanic;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;

public class FloatConfigOption extends ConfigOption<Float> {
  private final Float minValue;
  private final Float maxValue;
  private final boolean slider;
  private final Float step;

  protected FloatConfigOption(Builder builder) {
    super(builder);

    this.minValue = builder.minValue;
    this.maxValue = builder.maxValue;
    this.slider = builder.slider;
    this.step = builder.step;
  }

  @Override
  public void deserialize(Object data) {
    // Getting around a weird issue where the default deserializes into a Double
    this.setValue(((Double) data).floatValue());
  }

  public Float getMinValue() {
    return this.minValue;
  }

  public Float getMaxValue() {
    return this.maxValue;
  }

  public boolean useSlider() {
    return this.slider;
  }

  public float getStep() {
    if (this.step != null) {
      return this.step;
    }

    if (this.getMinValue() != null && this.getMaxValue() != null) {
      return (this.getMaxValue() - this.getMinValue()) / 10f;
    }

    return 20f;
  }

  @Override
  public boolean areValuesEqual(Float a, Float b) {
    return Math.abs(a - b) < 0x1.0p-10f;
  }

  @SuppressWarnings("UnusedReturnValue")
  public boolean step(int multi) {
    float value = this.getPendingValue();
    float minValue = Optional.ofNullable(this.getMinValue()).orElse(Float.MIN_VALUE);
    float maxValue = Optional.ofNullable(this.getMaxValue()).orElse(Float.MAX_VALUE);
    float newValue = MathHelper.clamp(value + this.getStep() * multi, minValue, maxValue);

    if (newValue == value) {
      return false;
    }

    this.setValue(newValue);
    return true;
  }

  public static Builder builder(Config config, String id) {
    return new Builder(config, id);
  }

  public static Builder sliderBuilder(Config config, String id) {
    return builder(config, id).setUseSlider(true);
  }

  // TODO: Set up a separate slider builder
  public static class Builder extends ConfigOption.AbstractBuilder<Float, FloatConfigOption, Builder> {
    private Float minValue = null;
    private Float maxValue = null;
    private boolean slider = false;
    private Float step = null;

    private Builder(Config config, String id) {
      super(config, id);
    }

    public Builder setDefaultValue(float defaultValue) {
      this.defaultValue = defaultValue;
      return this;
    }

    public Builder setMinValue(float minValue) {
      this.minValue = minValue;
      return this;
    }

    public Builder setMinValue(Float minValue) {
      this.minValue = minValue;
      return this;
    }

    public Builder setMaxValue(float maxValue) {
      this.maxValue = maxValue;
      return this;
    }

    public Builder setMaxValue(Float maxValue) {
      this.maxValue = maxValue;
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

    public Builder setStep(Float step) {
      this.step = step;
      return this;
    }

    @Override
    protected void validate() {
      super.validate();

      if (this.maxValue != null) {
        this.validators.addFirst((value, option) -> value <= this.maxValue);

        if (this.minValue != null && this.minValue > this.maxValue) {
          this.config.panic(
              new IllegalArgumentPanic("Min value cannot be larger than max value for FloatConfigOption"));
        }
      }

      if (this.minValue != null) {
        this.validators.addFirst((value, option) -> value >= this.minValue);
      }

      if (this.slider && (this.minValue == null || this.maxValue == null)) {
        this.config.panic(
            new IllegalArgumentPanic("Min and max values must be defined to use slider control for FloatConfigOption"));
      }
    }

    @Override
    protected FloatConfigOption buildInternal() {
      return new FloatConfigOption(this);
    }
  }
}
