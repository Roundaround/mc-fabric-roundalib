package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.config.option.BooleanConfigOption;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public class ToggleControl extends Control<Boolean, BooleanConfigOption> {
  private final ButtonWidget button;

  public ToggleControl(ConfigListWidget.OptionEntry<Boolean, BooleanConfigOption> parent) {
    super(parent);

    this.button =
        ButtonWidget.builder(this.option.getValueLabel(), (button) -> this.option.toggle())
            .position(this.widgetLeft, this.widgetTop)
            .size(this.widgetWidth, this.widgetHeight)
            .build();
  }

  @Override
  public List<? extends Element> children() {
    return List.of(this.button);
  }

  @Override
  public void setScrollAmount(double scrollAmount) {
    super.setScrollAmount(scrollAmount);

    this.button.setY(this.scrolledTop);
  }

  @Override
  public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    this.button.render(matrixStack, mouseX, mouseY, delta);
  }

  @Override
  protected void onConfigValueChange(Boolean prev, Boolean curr) {
    this.button.setMessage(this.option.getValueLabel());
  }
}
