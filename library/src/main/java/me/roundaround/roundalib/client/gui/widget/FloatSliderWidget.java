package me.roundaround.roundalib.client.gui.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;
import java.util.function.Function;

public class FloatSliderWidget extends SliderWidget {
  protected final float min;
  protected final float max;
  protected final float step;
  protected final Consumer<Float> valueChanged;
  protected final Function<Float, Text> formatter;

  private float floatValue;

  public FloatSliderWidget(
      int x,
      int y,
      int width,
      int height,
      float min,
      float max,
      float step,
      float value,
      Consumer<Float> valueChanged) {
    this(x, y, width, height, min, max, step, value, valueChanged, (v) -> Text.of(v.toString()));
  }

  public FloatSliderWidget(
      int x,
      int y,
      int width,
      int height,
      float min,
      float max,
      float step,
      float value,
      Consumer<Float> valueChanged,
      Function<Float, Text> formatter) {
    super(x, y, width, height, formatter.apply(value), valueToSlider(value, min, max));

    this.min = min;
    this.max = max;
    this.step = step;
    this.valueChanged = valueChanged;
    this.formatter = formatter;

    this.floatValue = value;
  }

  public void setFloatValue(float floatValue) {
    this.floatValue = floatValue;
    this.value = valueToSlider(this.floatValue, this.min, this.max);
    this.updateMessage();
  }

  public float getFloatValue() {
    return this.floatValue;
  }

  @Override
  protected void updateMessage() {
    this.setMessage(this.formatter.apply(this.floatValue));
  }

  @Override
  protected void applyValue() {
    this.floatValue = sliderToValue(this.value, this.min, this.max);
    this.valueChanged.accept(this.floatValue);
  }

  @Override
  public boolean mouseScrolled(
      double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    if (this.isMouseOver(mouseX, mouseY)) {
      this.setFloatValue(this.floatValue + (int) Math.signum(verticalAmount) * this.step);
      this.applyValue();
      return true;
    }

    return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
  }

  @Override
  public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    this.hovered = this.hovered && this.active;
    super.renderWidget(drawContext, mouseX, mouseY, delta);
  }

  protected static double valueToSlider(float value, float min, float max) {
    if (min == max) {
      return 0;
    }
    return MathHelper.clamp((value - min) / (double) (max - min), 0, 1);
  }

  protected static float sliderToValue(double slider, float min, float max) {
    return (float) MathHelper.clamp(slider * (max - min) + min, min, max);
  }
}
