package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.config.option.BooleanConfigOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;

public class ToggleControl extends Control<Boolean, BooleanConfigOption> {
  private final Button button;

  public ToggleControl(Minecraft client, BooleanConfigOption option, int width, int height) {
    super(client, option, width, height);

    this.button = this.add(Button.builder(this.option.getValueLabel(), (button) -> this.option.toggle()).build(),
        (parent, self) -> {
          self.setSize(parent.getWidth(), parent.getHeight());
        }
    );
  }

  @Override
  protected void update(Boolean value, boolean isDisabled) {
    this.button.active = !isDisabled;
    this.button.setMessage(this.getOption().getValueLabel(value));
  }
}
