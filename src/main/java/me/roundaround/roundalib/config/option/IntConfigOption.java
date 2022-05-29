package me.roundaround.roundalib.config.option;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import me.roundaround.roundalib.config.gui.control.IntInputControl;
import me.roundaround.roundalib.config.gui.widget.OptionRowWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class IntConfigOption extends ConfigOption<Integer, IntInputControl> {
  private Optional<Integer> minValue = Optional.empty();
  private Optional<Integer> maxValue = Optional.empty();
  private Optional<Integer> step = Optional.of(1);
  private List<Validator> validators = List.of();

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
  }

  @Override
  public IntInputControl createControl(OptionRowWidget parent, int top, int left, int height, int width) {
    return new IntInputControl(this, parent, top, left, height, width);
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

  public static class Builder extends ConfigOption.Builder<Integer, IntInputControl> {
    private Optional<Integer> minValue = Optional.empty();
    private Optional<Integer> maxValue = Optional.empty();
    private Optional<Integer> step = Optional.of(1);
    private List<Validator> customValidators = new ArrayList<>();

    private Builder(String id, String labelI18nKey) {
      super(id, labelI18nKey, 0);
    }

    private Builder(String id, Text label) {
      super(id, label, 0);
    }

    public Builder setDefaultValue(Integer defaultValue) {
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

    @Override
    public Builder setComment(String i18nKey) {
      comment = Optional.of(new TranslatableText(i18nKey));
      return this;
    }

    @Override
    public Builder setComment(Text comment) {
      this.comment = Optional.of(comment);
      return this;
    }

    @Override
    public Builder setUseLabelAsCommentFallback(boolean useLabelAsCommentFallback) {
      this.useLabelAsCommentFallback = useLabelAsCommentFallback;
      return this;
    }

    @Override
    public IntConfigOption build() {
      return new IntConfigOption(this);
    }
  }

  @FunctionalInterface
  public static interface Validator {
    boolean apply(int prev, int curr);
  }
}
