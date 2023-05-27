package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

public class SubScreenControl<D, O extends ConfigOption<D, ?>> extends Control<D, O> {
  private final SubScreenFactory<D, O> subScreenFactory;
  private final ButtonWidget button;

  public SubScreenControl(
      ConfigListWidget.OptionEntry<D, O> parent, SubScreenFactory<D, O> subScreenFactory) {
    super(parent);
    this.subScreenFactory = subScreenFactory;

    this.button = ButtonWidget.builder(Text.translatable(
                parent.getOption().getConfig().getModId() + ".roundalib.subscreen.label"),
            (button) -> GuiUtil.setScreen(this.subScreenFactory.create(this.parent.getClient().currentScreen,
                this.option)))
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
  public void setScrollAmount(double scrollAmount) {
    super.setScrollAmount(scrollAmount);

    this.button.setY(this.scrolledTop);
  }

  @Override
  public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    this.button.render(drawContext, mouseX, mouseY, delta);
  }

  @Override
  protected void onDisabledChange(boolean prev, boolean curr) {
    this.button.active = !this.disabled;
  }

  @FunctionalInterface
  public interface SubScreenFactory<D, C extends ConfigOption<D, ?>> {
    Screen create(Screen parent, C configOption);
  }
}
