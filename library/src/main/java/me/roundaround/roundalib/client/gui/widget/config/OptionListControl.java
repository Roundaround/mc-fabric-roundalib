package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.value.ListOptionValue;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;

public class OptionListControl<S extends ListOptionValue<S>>
    extends Control<S, OptionListConfigOption<S>> {
  private final CyclingButtonWidget<S> button;

  public OptionListControl(ConfigListWidget.OptionEntry<S, OptionListConfigOption<S>> parent) {
    super(parent);

    this.button =
        new CyclingButtonWidget.Builder<S>((value) -> value.getDisplayText(this.option.getConfig())).values(
                this.option.getValues())
            .initially(this.option.getValue())
            .omitKeyText()
            .build(this.widgetLeft,
                this.widgetTop,
                this.widgetWidth,
                this.widgetHeight,
                Text.empty(),
                this::buttonClicked);
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
  public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    this.button.render(matrixStack, mouseX, mouseY, delta);
  }

  @Override
  protected void onConfigValueChange(S prev, S curr) {
    if (prev == curr) {
      return;
    }
    this.button.setValue(curr);
  }

  @Override
  protected void onDisabledChange(boolean prev, boolean curr) {
    this.button.active = !disabled;
  }

  private void buttonClicked(CyclingButtonWidget<S> button, S value) {
    this.option.setValue(value);
  }
}
