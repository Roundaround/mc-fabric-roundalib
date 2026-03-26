package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.widget.FloatSliderWidget;
import me.roundaround.roundalib.config.option.FloatConfigOption;
import me.roundaround.roundalib.config.panic.IllegalArgumentPanic;
import me.roundaround.roundalib.config.panic.Panic;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class FloatSliderControl extends Control<Float, FloatConfigOption> {
  private final FloatSliderWidget slider;

  public FloatSliderControl(
      Minecraft client, FloatConfigOption option, int width, int height
  ) {
    super(client, option, width, height);

    if (!this.option.useSlider() || this.option.getMinValue() == null || this.option.getMaxValue() == null) {
      Panic.panic(new IllegalArgumentPanic(
              "FloatConfigOption must use slider and have min and max values to use FloatSliderControl"),
          this.getOption().getModId()
      );
    }

    this.slider = this.add(new FloatSliderWidget(width, height, this.option.getMinValue(), this.option.getMaxValue(),
        this.option.getPendingValue(), this::step, this::onSliderChanged, this::getValueAsText
    ), (parent, self) -> {
      self.setSize(parent.getWidth(), parent.getHeight());
    });
  }

  @Override
  protected void update(Float value, boolean isDisabled) {
    this.slider.active = !isDisabled;
    this.slider.setFloatValue(value);
  }

  private float step(int multi) {
    this.getOption().step(multi);
    return this.getOption().getPendingValue();
  }

  private void onSliderChanged(float value) {
    this.option.setValue(value);
  }

  private Component getValueAsText(float value) {
    return Component.literal(this.option.getValueAsString(value));
  }
}
