package me.roundaround.roundalib.config.option;

import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class FloatConfigOption extends ConfigOption<Float, FloatConfigOption.Builder> {
  private Optional<Float> minValue = Optional.empty();
  private Optional<Float> maxValue = Optional.empty();
  private List<Validator> validators = List.of();
  private boolean slider = false;
  private Optional<Integer> step = Optional.of(20);
  private Function<Float, String> valueDisplayFunction = (Float value) -> value.toString();

  protected FloatConfigOption(Builder builder) {
    super(builder);

    this.minValue = builder.minValue;
    this.maxValue = builder.maxValue;

    List<Validator> allValidators = new ArrayList<>();
    if (this.minValue.isPresent()) {
      allValidators.add((float prev, float curr) -> curr >= minValue.get());
    }
    if (this.maxValue.isPresent()) {
      allValidators.add((float prev, float curr) -> curr <= maxValue.get());
    }
    if (!builder.customValidators.isEmpty()) {
      allValidators.addAll(builder.customValidators);
    }
    this.validators = List.copyOf(allValidators);

    this.slider = builder.slider;
    this.step = builder.step;
    this.valueDisplayFunction = builder.valueDisplayFunction;
  }

  private FloatConfigOption(FloatConfigOption other) {
    super(other);

    this.minValue = other.minValue;
    this.maxValue = other.maxValue;
    this.validators = other.validators;
    this.slider = other.slider;
    this.step = other.step;
    this.valueDisplayFunction = other.valueDisplayFunction;
  }

  @Override
  public void deserialize(Object data) {
    // Getting around a weird issue where the default deserializes into a Double
    setValue(((Double) data).floatValue());
  }

  public Optional<Float> getMinValue() {
    return this.minValue;
  }

  public Optional<Float> getMaxValue() {
    return this.maxValue;
  }

  public boolean validateInput(float newValue) {
    return this.validators.stream().allMatch((validator) -> {
      return validator.apply(getValue(), newValue);
    });
  }

  public boolean useSlider() {
    return this.slider;
  }

  public int getStep() {
    return this.step.isEmpty() ? 20 : this.step.get();
  }

  public String getValueAsString() {
    return this.getValueAsString(this.getValue());
  }

  public String getValueAsString(float value) {
    return this.valueDisplayFunction.apply(getValue());
  }

  @Override
  public FloatConfigOption copy() {
    return new FloatConfigOption(this);
  }

  public static Builder builder(String modId, String id, String labelI18nKey) {
    return new Builder(modId, id, labelI18nKey);
  }

  public static Builder builder(String modId, String id, Text label) {
    return new Builder(modId, id, label);
  }

  public static Builder sliderBuilder(String modId, String id, String labelI18nKey) {
    return builder(modId, id, labelI18nKey).setUseSlider(true);
  }

  public static Builder sliderBuilder(String modId, String id, Text label) {
    return builder(modId, id, label).setUseSlider(true);
  }

  public static class Builder extends ConfigOption.AbstractBuilder<Float, Builder> {
    private Optional<Float> minValue = Optional.empty();
    private Optional<Float> maxValue = Optional.empty();
    private List<Validator> customValidators = new ArrayList<>();
    private boolean slider = false;
    private Optional<Integer> step = Optional.of(20);
    private Function<Float, String> valueDisplayFunction = (Float value) -> String.format("%.2f", value);

    private Builder(String modId, String id, String labelI18nKey) {
      super(modId, id, labelI18nKey, 0f);
    }

    private Builder(String modId, String id, Text label) {
      super(modId, id, label, 0f);
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
  public static interface Validator {
    boolean apply(float prev, float curr);
  }
}
