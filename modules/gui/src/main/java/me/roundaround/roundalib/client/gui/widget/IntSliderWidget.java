package me.roundaround.roundalib.client.gui.widget;

import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class IntSliderWidget extends AbstractSliderButton {
  protected final int min;
  protected final int max;
  protected final Function<Integer, Integer> stepHandler;
  protected final Consumer<Integer> valueChanged;
  protected final Function<Integer, Component> formatter;

  private int intValue;

  public IntSliderWidget(
      int width,
      int height,
      int min,
      int max,
      int value,
      Function<Integer, Integer> stepHandler,
      Consumer<Integer> valueChanged
  ) {
    this(0, 0, width, height, min, max, value, stepHandler, valueChanged);
  }

  public IntSliderWidget(
      int width,
      int height,
      int min,
      int max,
      int value,
      Function<Integer, Integer> stepHandler,
      Consumer<Integer> valueChanged,
      Function<Integer, Component> formatter
  ) {
    this(0, 0, width, height, min, max, value, stepHandler, valueChanged, formatter);
  }

  public IntSliderWidget(
      int x,
      int y,
      int width,
      int height,
      int min,
      int max,
      int value,
      Function<Integer, Integer> stepHandler,
      Consumer<Integer> valueChanged
  ) {
    this(x, y, width, height, min, max, value, stepHandler, valueChanged, (v) -> Component.nullToEmpty(v.toString()));
  }

  public IntSliderWidget(
      int x,
      int y,
      int width,
      int height,
      int min,
      int max,
      int value,
      Function<Integer, Integer> stepHandler,
      Consumer<Integer> valueChanged,
      Function<Integer, Component> formatter
  ) {
    super(x, y, width, height, formatter.apply(value), valueToSlider(value, min, max));

    this.min = min;
    this.max = max;
    this.stepHandler = stepHandler;
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
      double mouseX, double mouseY, double horizontalAmount, double verticalAmount
  ) {
    if (this.isMouseOver(mouseX, mouseY)) {
      this.setIntValue(this.stepHandler.apply((int) Math.signum(verticalAmount)));
      this.applyValue();
      return true;
    }

    return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
  }

  @Override
  public void renderWidget(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
    this.isHovered = this.isHovered && this.active;
    super.renderWidget(drawContext, mouseX, mouseY, delta);
  }

  protected static double valueToSlider(int value, int min, int max) {
    if (min == max) {
      return 0;
    }
    return Mth.clamp((value - min) / (double) (max - min), 0, 1);
  }

  protected static int sliderToValue(double slider, int min, int max) {
    return Mth.clamp((int) Math.round(slider * (max - min) + min), min, max);
  }
}
