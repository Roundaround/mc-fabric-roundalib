package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.config.option.EnumConfigOption;
import me.roundaround.roundalib.config.value.EnumValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;

public class EnumCycleControl<S extends EnumValue<S>> extends Control<S, EnumConfigOption<S>> {
  private final CycleButton<S> button;

  public EnumCycleControl(Minecraft client, EnumConfigOption<S> option, int width, int height) {
    super(client, option, width, height);

    this.button = this.add(
        new CycleButton.Builder<S>(
            (value) -> value.getDisplayText(option.getModId()),
            option::getPendingValue
        ).withValues(option.getValues()).displayOnlyValue().create(Component.empty(), this::buttonClicked), (parent, self) -> {
          self.setSize(parent.getWidth(), parent.getHeight());
        }
    );
  }

  @Override
  protected void update(S value, boolean isDisabled) {
    this.button.active = !isDisabled;
    this.button.setValue(value);
  }

  private void buttonClicked(CycleButton<S> button, S value) {
    this.option.setValue(value);
  }
}
