package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.asset.icon.BuiltinIcon;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.IntSliderWidget;
import me.roundaround.roundalib.client.gui.widget.layout.linear.LinearLayoutWidget;
import me.roundaround.roundalib.config.option.IntConfigOption;
import me.roundaround.roundalib.config.panic.IllegalArgumentPanic;
import me.roundaround.roundalib.config.panic.Panic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.Objects;

public class IntSliderControl extends Control<Integer, IntConfigOption> {
  private final IntSliderWidget slider;
  private final IconButtonWidget plusButton;
  private final IconButtonWidget minusButton;

  public IntSliderControl(MinecraftClient client, IntConfigOption option, int width, int height) {
    super(client, option, width, height);

    if (!this.option.useSlider() || this.option.getMinValue() == null || this.option.getMaxValue() == null) {
      Panic.panic(new IllegalArgumentPanic(
              "IntConfigOption must use slider and have min and max values to use IntSliderControl"),
          this.getOption().getModId()
      );
    }

    this.slider = this.add(new IntSliderWidget(width, height, this.option.getMinValue(), this.option.getMaxValue(),
        this.option.getPendingValue(), this::step, this::onSliderChanged, this::getValueAsText
    ), (parent, self) -> {
      int sliderWidth = parent.getWidth();
      if (this.option.showStepButtons()) {
        sliderWidth -= IconButtonWidget.SIZE_S + parent.getSpacing();
      }

      self.setDimensions(sliderWidth, parent.getHeight());
    });

    if (this.option.showStepButtons()) {
      String modId = this.getOption().getModId();
      int step = this.getOption().getStep();
      LinearLayoutWidget stepColumn = LinearLayoutWidget.vertical();

      this.plusButton = stepColumn.add(IconButtonWidget.builder(BuiltinIcon.PLUS_9, modId)
          .small()
          .messageAndTooltip(Text.translatable(modId + ".roundalib.step_up.tooltip", step))
          .onPress((button) -> this.getOption().increment())
          .build());
      this.minusButton = stepColumn.add(IconButtonWidget.builder(BuiltinIcon.MINUS_9, modId)
          .small()
          .messageAndTooltip(Text.translatable(modId + ".roundalib.step_down.tooltip", step))
          .onPress((button) -> this.getOption().decrement())
          .build());

      this.add(stepColumn, (parent, self) -> {
        self.spacing(parent.getHeight() - 2 * IconButtonWidget.SIZE_S);
      });
    } else {
      this.plusButton = null;
      this.minusButton = null;
    }

    this.update();
  }

  @Override
  protected void update() {
    IntConfigOption option = this.getOption();
    boolean disabled = option.isDisabled();

    this.slider.active = !disabled;

    int value = option.getPendingValue();
    if (!Objects.equals(value, this.slider.getIntValue())) {
      this.slider.setIntValue(value);
    }

    if (option.showStepButtons()) {
      this.plusButton.active = !disabled && option.canIncrement();
      this.minusButton.active = !disabled && option.canDecrement();
    }
  }

  private int step(int multi) {
    this.getOption().step(multi);
    return this.getOption().getPendingValue();
  }

  private void onSliderChanged(int value) {
    this.option.setValue(value);
  }

  private Text getValueAsText(int value) {
    return Text.literal(this.option.getValueAsString(value));
  }
}
