package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.config.option.EnumConfigOption;
import me.roundaround.roundalib.config.value.EnumValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;

public class EnumCycleControl<S extends EnumValue<S>> extends Control<S, EnumConfigOption<S>> {
  private final CyclingButtonWidget<S> button;

  public EnumCycleControl(MinecraftClient client, EnumConfigOption<S> option, int width, int height) {
    super(client, option, width, height);

    this.button = this.add(
        new CyclingButtonWidget.Builder<S>(
            (value) -> value.getDisplayText(option.getModId()),
            option::getPendingValue
        ).values(option.getValues()).omitKeyText().build(Text.empty(), this::buttonClicked), (parent, self) -> {
          self.setDimensions(parent.getWidth(), parent.getHeight());
        }
    );
  }

  @Override
  protected void update(S value, boolean isDisabled) {
    this.button.active = !isDisabled;
    this.button.setValue(value);
  }

  private void buttonClicked(CyclingButtonWidget<S> button, S value) {
    this.option.setValue(value);
  }
}
