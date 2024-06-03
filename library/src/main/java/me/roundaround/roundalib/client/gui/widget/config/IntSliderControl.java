package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.widget.IntSliderWidget;
import me.roundaround.roundalib.config.option.IntConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Objects;

public class IntSliderControl extends Control<Integer, IntConfigOption> {
  private final IntSliderWidget slider;

  public IntSliderControl(MinecraftClient client, IntConfigOption option, int left, int top, int width, int height) {
    super(client, option, left, top, width, height);

    if (!this.option.useSlider() || this.option.getMinValue().isEmpty() || this.option.getMaxValue().isEmpty()) {
      throw new IllegalArgumentException(
          "IntConfigOption must use slider and have min and max values to use IntSliderControl");
    }

    this.slider = new IntSliderWidget(this.getWidgetLeft(), this.getWidgetTop(), this.getWidgetWidth(),
        this.getWidgetHeight(), this.option.getMinValue().get(), this.option.getMaxValue().get(), this.option.getStep(),
        this.option.getValue(), this::onSliderChanged, this::getValueAsText
    );

    this.onDisabledChange(this.disabled, this.disabled);
  }

  @Override
  public List<? extends Element> children() {
    return List.of(this.slider);
  }

  @Override
  public void refreshPositions() {
    this.slider.setPosition(this.getWidgetLeft(), this.getWidgetTop());
    this.slider.setDimensions(this.getWidgetWidth(), this.getWidgetHeight());
    super.refreshPositions();
  }

  @Override
  public void renderPositional(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    this.slider.render(drawContext, mouseX, mouseY, delta);
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
