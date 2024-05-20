package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.config.option.BooleanConfigOption;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;

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

    this.onDisabledChange(this.disabled, this.disabled);
  }

  @Override
  public List<? extends Element> children() {
    return List.of(this.button);
  }

  @Override
  public void updateBounds(double scrollAmount) {
    super.updateBounds(scrollAmount);

    this.button.setY(this.scrolledTop);
  }

  @Override
  public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    this.button.render(drawContext, mouseX, mouseY, delta);
  }

  @Override
  protected void onConfigValueChange(Boolean prev, Boolean curr) {
    this.button.setMessage(this.option.getValueLabel());
  }

  @Override
  protected void onDisabledChange(boolean prev, boolean curr) {
    this.button.active = !this.disabled;
  }
}
