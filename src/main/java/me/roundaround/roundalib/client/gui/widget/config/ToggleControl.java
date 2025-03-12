package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.config.option.BooleanConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;

public class ToggleControl extends Control<Boolean, BooleanConfigOption> {
  private final ButtonWidget button;

  public ToggleControl(MinecraftClient client, BooleanConfigOption option, int width, int height) {
    super(client, option, width, height);

    this.button = this.add(ButtonWidget.builder(this.option.getValueLabel(), (button) -> this.option.toggle()).build(),
        (parent, self) -> {
          self.setDimensions(parent.getWidth(), parent.getHeight());
        }
    );
  }

  @Override
  protected void update(Boolean value, boolean isDisabled) {
    this.button.active = !isDisabled;
    this.button.setMessage(this.getOption().getValueLabel(value));
  }
}
