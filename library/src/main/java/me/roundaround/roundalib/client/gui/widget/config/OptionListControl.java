package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.value.ListOptionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public class OptionListControl<S extends ListOptionValue<S>> extends Control<S, OptionListConfigOption<S>> {
  private final CyclingButtonWidget<S> button;

  public OptionListControl(
      MinecraftClient client, OptionListConfigOption<S> option, int left, int top, int width, int height
  ) {
    super(client, option, left, top, width, height);

    this.button = new CyclingButtonWidget.Builder<S>((value) -> value.getDisplayText(option.getConfig())).values(
            option.getValues())
        .initially(option.getValue())
        .omitKeyText()
        .build(this.getWidgetLeft(), this.getWidgetTop(), this.getWidgetWidth(), this.getWidgetHeight(), Text.empty(),
            this::buttonClicked
        );

    this.onDisabledChange(this.disabled, this.disabled);
  }

  @Override
  public List<? extends Element> children() {
    return List.of(this.button);
  }

  @Override
  public void refreshPositions() {
    this.button.setPosition(this.getWidgetLeft(), this.getWidgetTop());
    this.button.setDimensions(this.getWidgetWidth(), this.getWidgetHeight());
  }

  @Override
  public void renderPositional(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    this.button.render(drawContext, mouseX, mouseY, delta);
  }

  @Override
  protected void onConfigValueChange(S prev, S curr) {
    if (prev == curr) {
      return;
    }
    this.button.setValue(curr);
  }

  @Override
  protected void onDisabledChange(boolean prev, boolean curr) {
    this.button.active = !this.disabled;
  }

  private void buttonClicked(CyclingButtonWidget<S> button, S value) {
    this.option.setValue(value);
  }
}
