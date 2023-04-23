package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.config.option.IntConfigOption;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;
import java.util.Objects;

public class IntTextControl extends Control<Integer, IntConfigOption> {
  private final TextFieldWidget textField;
  private final IconButtonWidget plusButton;
  private final IconButtonWidget minusButton;

  public IntTextControl(ConfigListWidget.OptionEntry<Integer, IntConfigOption> parent) {
    super(parent);

    this.textField = new TextFieldWidget(parent.getTextRenderer(),
        this.widgetLeft + 1,
        this.widgetTop + 1,
        this.widgetWidth - 2,
        this.widgetHeight - 2,
        this.option.getLabel());

    this.textField.setText(this.option.getValue().toString());
    this.textField.setMaxLength(12);
    this.textField.setChangedListener(this::onTextChanged);

    if (this.option.showStepButtons()) {
      this.textField.setWidth(this.widgetWidth - RoundaLibIconButtons.SIZE_S - 4);

      this.plusButton = RoundaLibIconButtons.intStepButton(
          this.widgetLeft + this.widgetWidth - RoundaLibIconButtons.SIZE_S,
          this.widgetTop,
          this.option,
          true);

      this.minusButton = RoundaLibIconButtons.intStepButton(
          this.widgetLeft + this.widgetWidth - RoundaLibIconButtons.SIZE_S,
          this.widgetTop + this.widgetHeight - RoundaLibIconButtons.SIZE_S,
          this.option,
          false);
    } else {
      this.plusButton = null;
      this.minusButton = null;
    }
  }

  @Override
  public List<? extends Element> children() {
    if (this.option.showStepButtons()) {
      return List.of(this.textField, this.plusButton, this.minusButton);
    } else {
      return List.of(this.textField);
    }
  }

  @Override
  public void setScrollAmount(double scrollAmount) {
    super.setScrollAmount(scrollAmount);

    this.textField.setY(this.scrolledTop + 1);

    if (this.option.showStepButtons()) {
      this.plusButton.setY(this.scrolledTop);
      this.minusButton.setY(this.scrolledTop + this.widgetHeight - RoundaLibIconButtons.SIZE_S);
    }
  }

  @Override
  public void markInvalid() {
    this.textField.setEditableColor(GuiUtil.ERROR_COLOR);
    super.markInvalid();
  }

  @Override
  public void markValid() {
    this.textField.setEditableColor(GuiUtil.LABEL_COLOR);
    super.markValid();
  }

  @Override
  public void tick() {
    this.textField.tick();
  }

  @Override
  public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    this.textField.render(matrixStack, mouseX, mouseY, delta);
    if (this.option.showStepButtons()) {
      this.plusButton.render(matrixStack, mouseX, mouseY, delta);
      this.minusButton.render(matrixStack, mouseX, mouseY, delta);
    }
  }

  @Override
  protected void onConfigValueChange(Integer prev, Integer curr) {
    String currStr = String.valueOf(curr);

    if (Objects.equals(currStr, this.textField.getText())) {
      return;
    }

    this.textField.setText(currStr);
  }

  private void onTextChanged(String value) {
    try {
      int parsed = Integer.parseInt(value);
      if (this.option.validateInput(parsed)) {
        this.option.setValue(parsed);
        markValid();
      } else {
        markInvalid();
      }
    } catch (Exception e) {
      markInvalid();
    }
  }
}
