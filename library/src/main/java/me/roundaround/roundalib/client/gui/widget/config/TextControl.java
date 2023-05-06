package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.config.option.StringConfigOption;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;
import java.util.Objects;

public class TextControl extends Control<String, StringConfigOption> {
  private final TextFieldWidget textField;

  public TextControl(ConfigListWidget.OptionEntry<String, StringConfigOption> parent) {
    super(parent);

    this.textField = new TextFieldWidget(parent.getTextRenderer(),
        this.widgetLeft + 1,
        this.widgetTop + 1,
        this.widgetWidth - 2,
        this.widgetHeight - 2,
        this.option.getLabel());

    this.textField.setText(this.option.getValue());
    this.textField.setChangedListener(this::onTextChanged);

    this.onDisabledChange(this.disabled, this.disabled);
  }

  @Override
  public List<? extends Element> children() {
    return List.of(this.textField);
  }

  @Override
  public void setScrollAmount(double scrollAmount) {
    super.setScrollAmount(scrollAmount);

    this.textField.setY(this.scrolledTop + 1);
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
    super.tick();
    this.textField.tick();
  }

  @Override
  public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    this.textField.render(matrixStack, mouseX, mouseY, delta);
  }

  @Override
  protected void onConfigValueChange(String prev, String curr) {
    if (Objects.equals(curr, this.textField.getText())) {
      return;
    }
    this.textField.setText(curr);
  }

  @Override
  protected void onDisabledChange(boolean prev, boolean curr) {
    this.textField.active = !this.disabled;
    this.textField.setEditable(!this.disabled);
  }

  private void onTextChanged(String text) {
    if (!this.option.validateInput(text)) {
      this.markInvalid();
      return;
    }

    this.option.setValue(text);
    this.markValid();
  }
}
