package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.config.option.BooleanConfigOption;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public class ToggleControl extends Control<BooleanConfigOption> {
  private final BooleanConfigOption configOption;
  private final ButtonWidget button;

  public ToggleControl(ConfigListWidget.OptionEntry<BooleanConfigOption> parent) {
    super(parent);

    this.configOption = parent.getOption();
    this.configOption.subscribeToValueChanges(this::onConfigValueChange);

    this.button = ButtonWidget.builder(this.configOption.getValueLabel(),
            (button) -> this.configOption.toggle())
        .position(this.widgetLeft, this.widgetTop)
        .size(this.widgetWidth, this.widgetHeight)
        .build();
  }

  @Override
  public List<? extends Element> children() {
    return List.of(this.button);
  }

  @Override
  public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    this.button.render(matrixStack, mouseX, mouseY, delta);
  }

  private void onConfigValueChange(Boolean prev, Boolean curr) {
    this.button.setMessage(this.configOption.getValueLabel());
  }
}
