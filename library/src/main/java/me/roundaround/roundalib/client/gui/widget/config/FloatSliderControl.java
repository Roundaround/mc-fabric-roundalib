package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.widget.FloatSliderWidget;
import me.roundaround.roundalib.config.option.FloatConfigOption;
import me.roundaround.roundalib.config.panic.IllegalArgumentPanic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.text.Text;

import java.util.List;

public class FloatSliderControl extends Control<Float, FloatConfigOption> {
  private final FloatSliderWidget slider;

  public FloatSliderControl(
      MinecraftClient client, FloatConfigOption option, int left, int top, int width, int height
  ) {
    super(client, option, left, top, width, height);

    if (!this.option.useSlider() || this.option.getMinValue() == null || this.option.getMaxValue() == null) {
      this.option.getModConfig()
          .panic(new IllegalArgumentPanic(
              "FloatConfigOption must use slider and have min and max values to use FloatSliderControl"));
    }

    this.slider = new FloatSliderWidget(this.getLeft(), this.getTop(), this.getWidth(), this.getHeight(),
        this.option.getMinValue(), this.option.getMaxValue(), this.option.getPendingValue(), this::step,
        this::onSliderChanged, this::getValueAsText
    );

    this.update();
  }

  @Override
  public List<? extends Element> children() {
    return List.of(this.slider);
  }

  @Override
  public void refreshPositions() {
    this.slider.setPosition(this.getLeft(), this.getTop());
    this.slider.setDimensions(this.getWidth(), this.getHeight());
    super.refreshPositions();
  }

  @Override
  public void renderPositional(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    this.slider.render(drawContext, mouseX, mouseY, delta);
  }

  @Override
  protected void update() {
    this.slider.active = !this.getOption().isDisabled();

    float value = this.getOption().getPendingValue();
    if (!this.getOption().areValuesEqual(value, this.slider.getFloatValue())) {
      this.slider.setFloatValue(value);
    }
  }

  private float step(int multi) {
    this.getOption().step(multi);
    return this.getOption().getPendingValue();
  }

  private void onSliderChanged(float value) {
    this.option.setValue(value);
  }

  private Text getValueAsText(float value) {
    return Text.literal(this.option.getValueAsString(value));
  }
}
