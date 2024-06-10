package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.config.option.StringConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;

import java.util.List;

public class TextControl extends Control<String, StringConfigOption> {
  private final TextFieldWidget textField;

  public TextControl(MinecraftClient client, StringConfigOption option, int left, int top, int width, int height) {
    super(client, option, left, top, width, height);

    this.textField = new TextFieldWidget(client.textRenderer, this.getWidgetLeft() + 1, this.getWidgetTop() + 1,
        this.getWidgetWidth() - 2, this.getWidgetHeight() - 2, this.option.getLabel()
    );

    this.textField.setText(this.option.getPendingValue());
    this.textField.setChangedListener(this::onTextChanged);

    this.update();
  }

  @Override
  public List<? extends Element> children() {
    return List.of(this.textField);
  }

  @Override
  public void refreshPositions() {
    this.textField.setPosition(this.getWidgetLeft() + 1, this.getWidgetTop() + 1);
    this.textField.setDimensions(this.getWidgetWidth() - 2, this.getWidgetHeight() - 2);
  }

  @Override
  public void renderPositional(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    this.textField.render(drawContext, mouseX, mouseY, delta);
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
  protected void update() {
    boolean disabled = this.getOption().isDisabled();
    this.textField.active = !disabled;
    this.textField.setEditable(!disabled);

    String value = this.getOption().getPendingValue();
    if (!value.equals(this.textField.getText())) {
      this.textField.setText(value);
    }
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
