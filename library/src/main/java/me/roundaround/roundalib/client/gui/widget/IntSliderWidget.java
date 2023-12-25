package me.roundaround.roundalib.client.gui.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;
import java.util.function.Function;

public class IntSliderWidget extends SliderWidget {
  protected final int min;
  protected final int max;
  protected final int step;
  protected final Consumer<Integer> valueChanged;
  protected final Function<Integer, Text> formatter;

  private int intValue;

  public IntSliderWidget(
      int x,
      int y,
      int width,
      int height,
      int min,
      int max,
      int step,
      int value,
      Consumer<Integer> valueChanged) {
    this(x, y, width, height, min, max, step, value, valueChanged, (v) -> Text.of(v.toString()));
  }

  public IntSliderWidget(
      int x,
      int y,
      int width,
      int height,
      int min,
      int max,
      int step,
      int value,
      Consumer<Integer> valueChanged,
      Function<Integer, Text> formatter) {
    super(x, y, width, height, formatter.apply(value), valueToSlider(value, min, max));

    this.min = min;
    this.max = max;
    this.step = step;
    this.valueChanged = valueChanged;
    this.formatter = formatter;

    this.intValue = value;
  }

  public void setIntValue(int intValue) {
    this.intValue = intValue;
    this.value = valueToSlider(this.intValue, this.min, this.max);
    this.updateMessage();
  }

  public int getIntValue() {
    return this.intValue;
  }

  @Override
  protected void updateMessage() {
    this.setMessage(this.formatter.apply(this.intValue));
  }

  @Override
  protected void applyValue() {
    this.intValue = sliderToValue(this.value, this.min, this.max);
    this.valueChanged.accept(this.intValue);
  }

  @Override
  public boolean mouseScrolled(
      double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    if (this.isMouseOver(mouseX, mouseY)) {
      this.setIntValue(this.intValue + (int) Math.signum(verticalAmount) * this.step);
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

  protected static double valueToSlider(int value, int min, int max) {
    if (min == max) {
      return 0;
    }
    return MathHelper.clamp((value - min) / (double) (max - min), 0, 1);
  }

  protected static int sliderToValue(double slider, int min, int max) {
    return MathHelper.clamp((int) Math.round(slider * (max - min) + min), min, max);
  }
}
