package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.panic.IllegalArgumentPanic;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;

public class IntConfigOption extends ConfigOption<Integer> {
  private final Integer minValue;
  private final Integer maxValue;
  private final Integer step;
  private final boolean slider;

  protected IntConfigOption(Builder builder) {
    super(builder);

    this.minValue = builder.minValue;
    this.maxValue = builder.maxValue;
    this.slider = builder.slider;
    this.step = builder.step;
  }

  public Integer getMinValue() {
    return this.minValue;
  }

  public Integer getMaxValue() {
    return this.maxValue;
  }

  public boolean useSlider() {
    return this.slider;
  }

  public int getStep() {
    if (this.step != null) {
      return this.step;
    }

    if (this.getMinValue() != null && this.getMaxValue() != null) {
      return Math.max(1, Math.round((this.getMaxValue() - this.getMinValue()) / 10f));
    }

    return 1;
  }

  public boolean showStepButtons() {
    return this.step != null;
  }

  public boolean canIncrement() {
    if (this.step == null) {
      return false;
    }

    return this.getPendingValue() < Optional.ofNullable(this.getMaxValue()).orElse(Integer.MAX_VALUE);
  }

  public boolean canDecrement() {
    if (this.step == null) {
      return false;
    }

    return this.getPendingValue() > Optional.ofNullable(this.minValue).orElse(Integer.MIN_VALUE);
  }

  @SuppressWarnings("UnusedReturnValue")
  public boolean increment() {
    return this.step(1);
  }

  @SuppressWarnings("UnusedReturnValue")
  public boolean decrement() {
    return this.step(-1);
  }

  public boolean step(int multi) {
    if (this.step == null) {
      return false;
    }

    int value = this.getPendingValue();
    int minValue = Optional.ofNullable(this.getMinValue()).orElse(Integer.MIN_VALUE);
    int maxValue = Optional.ofNullable(this.getMaxValue()).orElse(Integer.MAX_VALUE);
    int newValue = MathHelper.clamp(value + this.step * multi, minValue, maxValue);

    if (newValue == value) {
      return false;
    }

    this.setValue(newValue);
    return true;
  }

  public static Builder builder(ModConfig modConfig, String id) {
    return new Builder(modConfig, id);
  }

  public static Builder sliderBuilder(ModConfig modConfig, String id) {
    return new Builder(modConfig, id).setUseSlider(true);
  }

  // TODO: Set up a separate slider builder
  public static class Builder extends ConfigOption.AbstractBuilder<Integer, IntConfigOption, Builder> {
    private Integer minValue = null;
    private Integer maxValue = null;
    private Integer step = 1;
    private boolean slider = false;

    private Builder(ModConfig modConfig, String id) {
      super(modConfig, id);
    }

    public Builder setDefaultValue(int defaultValue) {
      this.defaultValue = defaultValue;
      return this;
    }

    public Builder setMinValue(int minValue) {
      this.minValue = minValue;
      return this;
    }

    public Builder setMinValue(Integer minValue) {
      this.minValue = minValue;
      return this;
    }

    public Builder setMaxValue(int maxValue) {
      this.maxValue = maxValue;
      return this;
    }

    public Builder setMaxValue(Integer maxValue) {
      this.maxValue = maxValue;
      return this;
    }

    public Builder setStep(int step) {
      this.step = step;
      return this;
    }

    public Builder setStep(Integer step) {
      this.step = step;
      return this;
    }

    public Builder setUseSlider(boolean slider) {
      this.slider = slider;
      return this;
    }

    @Override
    public void validate() {
      super.validate();

      if (this.maxValue != null) {
        this.validators.addFirst((value, option) -> value <= this.maxValue);

        if (this.minValue != null && this.minValue > this.maxValue) {
          this.modConfig.panic(
              new IllegalArgumentPanic("Min value cannot be larger than max value for IntConfigOption"));
        }
      }

      if (this.minValue != null) {
        this.validators.addFirst((value, option) -> value >= this.minValue);
      }

      if (this.slider && (this.minValue == null || this.maxValue == null)) {
        this.modConfig.panic(
            new IllegalArgumentPanic("Min and max values must be defined to use slider control for IntConfigOption"));
      }
    }

    @Override
    protected IntConfigOption buildInternal() {
      return new IntConfigOption(this);
    }
  }
}
