package me.roundaround.roundalib.config.option;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.gui.control.IntInputControl;

public class IntConfigOption extends ConfigOption<Integer, IntInputControl> {
  public IntConfigOption(String id, String labelI18nKey, Integer defaultValue) {
    super(id, labelI18nKey, defaultValue);
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

  public static class Options {
    private List<Integer> steps = List.of(1);
    private List<Validator> validators = List.of();

    private Options() {
    }

    private Options(Builder builder) {
      steps = builder.steps;

      List<Validator> allValidators = new ArrayList<>();
      if (builder.minValue.isPresent()) {
        allValidators.add((int prev, int curr) -> curr >= builder.minValue.get());
      }
      if (builder.maxValue.isPresent()) {
        allValidators.add((int prev, int curr) -> curr >= builder.maxValue.get());
      }
      if (builder.forceSteps && !builder.steps.isEmpty()) {
        allValidators.add((int prev, int curr) -> builder.steps.stream().anyMatch((step) -> curr % step == 0));
      }
      if (!builder.customValidators.isEmpty()) {
        allValidators.addAll(builder.customValidators);
      }
      validators = List.copyOf(allValidators);
    }

    public static Builder builder() {
      return new Builder();
    }

    public List<Integer> getSteps() {
      return steps;
    }

    public List<Validator> getValidators() {
      return validators;
    }

    public final static class Builder {
      private Optional<Integer> minValue = Optional.empty();
      private Optional<Integer> maxValue = Optional.empty();
      private List<Integer> steps = List.of(1);
      private boolean forceSteps = false;
      private List<Validator> customValidators = new ArrayList<>();

      public Builder setMinValue(int value) {
        minValue = Optional.of(value);
        return this;
      }

      public Builder setMaxValue(int value) {
        maxValue = Optional.of(value);
        return this;
      }

      public Builder setSteps(Collection<Integer> steps) {
        steps = List.copyOf(steps);
        return this;
      }

      public Builder forceStepIncrement() {
        forceSteps = true;
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
