package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.widget.FloatSliderWidget;
import me.roundaround.roundalib.config.option.FloatConfigOption;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Objects;

public class FloatSliderControl extends Control<Float, FloatConfigOption> {
  private final FloatSliderWidget slider;

  public FloatSliderControl(ConfigListWidget.OptionEntry<Float, FloatConfigOption> parent) {
    super(parent);

    if (!this.option.useSlider() || this.option.getMinValue().isEmpty() ||
        this.option.getMaxValue().isEmpty()) {
      throw new IllegalArgumentException(
          "FloatConfigOption must use slider and have min and max values to use FloatSliderControl");
    }

    this.slider = new FloatSliderWidget(this.widgetLeft,
        this.widgetTop,
        this.widgetWidth,
        this.widgetHeight,
        this.option.getMinValue().get(),
        this.option.getMaxValue().get(),
        this.option.getStep(),
        this.option.getValue(),
        this::onSliderChanged,
        this::getValueAsText);
  }

  @Override
  public List<? extends Element> children() {
    return List.of(this.slider);
  }

  @Override
  public void setScrollAmount(double scrollAmount) {
    super.setScrollAmount(scrollAmount);

    this.slider.setY(this.scrolledTop);
  }

  @Override
  public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    this.slider.render(matrixStack, mouseX, mouseY, delta);
  }

  @Override
  protected void onConfigValueChange(Float prev, Float curr) {
    if (Objects.equals(curr, this.slider.getFloatValue())) {
      return;
    }
    this.slider.setFloatValue(curr);
  }

  @Override
  protected void onDisabledChange(boolean prev, boolean curr) {
    this.slider.active = !disabled;
  }

  private void onSliderChanged(float value) {
    this.option.setValue(value);
  }

  private Text getValueAsText(float value) {
    return Text.literal(this.option.getValueAsString(value));
  }
}
