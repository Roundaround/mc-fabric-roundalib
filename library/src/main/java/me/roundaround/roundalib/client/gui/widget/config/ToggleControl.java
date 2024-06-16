package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.config.option.BooleanConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.List;

public class ToggleControl extends Control<Boolean, BooleanConfigOption> {
  private final ButtonWidget button;

  public ToggleControl(MinecraftClient client, BooleanConfigOption option, int left, int top, int width, int height) {
    super(client, option, left, top, width, height);

    this.button = ButtonWidget.builder(this.option.getValueLabel(), (button) -> this.option.toggle())
        .position(this.getLeft(), this.getTop())
        .size(this.getWidth(), this.getHeight())
        .build();

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
    this.button.setMessage(this.getOption().getValueLabel());
  }
}
