package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.config.option.EnumConfigOption;
import me.roundaround.roundalib.config.value.EnumValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;

public class EnumCycleControl<S extends EnumValue<S>> extends Control<S, EnumConfigOption<S>> {
  private final CyclingButtonWidget<S> button;

  public EnumCycleControl(
      MinecraftClient client, EnumConfigOption<S> option, int width, int height
  ) {
    super(client, option, width, height);

    this.button = this.add(
        new CyclingButtonWidget.Builder<S>((value) -> value.getDisplayText(option.getModId())).values(
                option.getValues())
            .initially(option.getPendingValue())
            .omitKeyText()
            .build(Text.empty(), this::buttonClicked), (parent, self) -> {
          self.setDimensions(parent.getWidth(), parent.getHeight());
        });

    this.update();
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
