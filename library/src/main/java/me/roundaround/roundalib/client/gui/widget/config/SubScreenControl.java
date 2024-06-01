package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.MinecraftClient;
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
      MinecraftClient client, O option, SubScreenFactory<D, O> subScreenFactory
  ) {
    super(client, option);
    this.subScreenFactory = subScreenFactory;

    this.button = ButtonWidget.builder(
        Text.translatable(option.getConfig().getModId() + ".roundalib.subscreen.label"),
        (button) -> GuiUtil.setScreen(this.subScreenFactory.create(client.currentScreen, this.option))
    ).position(this.widgetX, this.widgetY).size(this.widgetWidth, this.widgetHeight).build();

    this.onDisabledChange(this.disabled, this.disabled);
  }

  @Override
  public List<? extends Element> children() {
    return List.of(this.button);
  }

  @Override
  public void onBoundsChanged() {
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
  public interface SubScreenFactory<D, O extends ConfigOption<D, ?>> {
    Screen create(Screen screen, O option);
  }
}
