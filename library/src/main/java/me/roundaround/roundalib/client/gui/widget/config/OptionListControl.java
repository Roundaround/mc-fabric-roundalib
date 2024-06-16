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

    this.button = new CyclingButtonWidget.Builder<S>((value) -> value.getDisplayText(option.getModId())).values(
            option.getValues())
        .initially(option.getPendingValue())
        .omitKeyText()
        .build(this.getLeft(), this.getTop(), this.getWidth(), this.getHeight(), Text.empty(),
            this::buttonClicked
        );

    this.update();
  }

  @Override
  public List<? extends Element> children() {
    return List.of(this.button);
  }

  @Override
  public void refreshPositions() {
    this.button.setPosition(this.getLeft(), this.getTop());
    this.button.setDimensions(this.getWidth(), this.getHeight());
  }

  @Override
  public void renderPositional(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    this.button.render(drawContext, mouseX, mouseY, delta);
  }

  @Override
  protected void update() {
    this.button.active = !this.getOption().isDisabled();
    this.button.setValue(this.getOption().getPendingValue());
  }

  private void buttonClicked(CyclingButtonWidget<S> button, S value) {
    this.option.setValue(value);
  }
}
