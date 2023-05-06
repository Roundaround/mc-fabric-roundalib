package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.widget.IntSliderWidget;
import me.roundaround.roundalib.config.option.IntConfigOption;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Objects;

public class IntSliderControl extends Control<Integer, IntConfigOption> {
  private final IntSliderWidget slider;

  public IntSliderControl(ConfigListWidget.OptionEntry<Integer, IntConfigOption> parent) {
    super(parent);

    if (!this.option.useSlider() || this.option.getMinValue().isEmpty() || this.option.getMaxValue().isEmpty()) {
      throw new IllegalArgumentException(
          "IntConfigOption must use slider and have min and max values to use IntSliderControl");
    }

    this.slider = new IntSliderWidget(
        this.widgetLeft,
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
  protected void onConfigValueChange(Integer prev, Integer curr) {
    if (Objects.equals(curr, this.slider.getIntValue())) {
      return;
    }
    this.slider.setIntValue(curr);
  }

  @Override
  protected void onDisabledChange(boolean prev, boolean curr) {
    this.slider.active = !this.disabled;
  }

  private void onSliderChanged(int value) {
    this.option.setValue(value);
  }

  private Text getValueAsText(int value) {
    return Text.literal(this.option.getValueAsString(value));
  }
}
