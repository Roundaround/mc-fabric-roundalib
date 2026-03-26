package me.roundaround.roundalib.client.gui.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;
import java.util.function.Function;

public class FloatSliderWidget extends AbstractSliderButton {
  protected final float min;
  protected final float max;
  protected final Function<Integer, Float> stepHandler;
  protected final Consumer<Float> valueChanged;
  protected final Function<Float, Component> formatter;

  private float floatValue;

  public FloatSliderWidget(
      int width,
      int height,
      float min,
      float max,
      float value,
      Function<Integer, Float> stepHandler,
      Consumer<Float> valueChanged
  ) {
    this(0, 0, width, height, min, max, value, stepHandler, valueChanged);
  }

  public FloatSliderWidget(
      int width,
      int height,
      float min,
      float max,
      float value,
      Function<Integer, Float> stepHandler,
      Consumer<Float> valueChanged,
      Function<Float, Component> formatter
  ) {
    this(0, 0, width, height, min, max, value, stepHandler, valueChanged, formatter);
  }

  public FloatSliderWidget(
      int x,
      int y,
      int width,
      int height,
      float min,
      float max,
      float value,
      Function<Integer, Float> stepHandler,
      Consumer<Float> valueChanged
  ) {
    this(x, y, width, height, min, max, value, stepHandler, valueChanged, (v) -> Component.nullToEmpty(v.toString()));
  }

  public FloatSliderWidget(
      int x,
      int y,
      int width,
      int height,
      float min,
      float max,
      float value,
      Function<Integer, Float> stepHandler,
      Consumer<Float> valueChanged,
      Function<Float, Component> formatter
  ) {
    super(x, y, width, height, formatter.apply(value), valueToSlider(value, min, max));

    this.min = min;
    this.max = max;
    this.stepHandler = stepHandler;
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
  public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    if (this.isMouseOver(mouseX, mouseY)) {
      this.setFloatValue(this.stepHandler.apply((int) Math.signum(verticalAmount)));
      this.applyValue();
      return true;
    }

    return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
  }

  @Override
  public void extractWidgetRenderState(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float delta) {
    this.isHovered = this.isHovered && this.active;
    super.extractWidgetRenderState(drawContext, mouseX, mouseY, delta);
  }

  protected static double valueToSlider(float value, float min, float max) {
    if (min == max) {
      return 0;
    }
    return Mth.clamp((value - min) / (double) (max - min), 0, 1);
  }

  protected static float sliderToValue(double slider, float min, float max) {
    return (float) Mth.clamp(slider * (max - min) + min, min, max);
  }
}
