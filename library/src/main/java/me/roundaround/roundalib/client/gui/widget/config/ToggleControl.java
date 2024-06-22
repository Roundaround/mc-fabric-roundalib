package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.config.option.BooleanConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;

public class ToggleControl extends Control<Boolean, BooleanConfigOption> {
  private final ButtonWidget button;

  public ToggleControl(MinecraftClient client, BooleanConfigOption option, int width, int height) {
    this(client, option, 0, 0, width, height);
  }

  public ToggleControl(MinecraftClient client, BooleanConfigOption option, int x, int y, int width, int height) {
    super(client, option, x, y, width, height);

    this.button = this.add(
        ButtonWidget.builder(this.option.getValueLabel(), (button) -> this.option.toggle()).build(), (parent, self) -> {
          self.setDimensionsAndPosition(parent.getWidth(), parent.getHeight(), parent.getX(), parent.getY());
        });

    this.update();
  }

  @Override
  protected void update() {
    this.button.active = !this.getOption().isDisabled();
    this.button.setMessage(this.getOption().getValueLabel());
  }
}
