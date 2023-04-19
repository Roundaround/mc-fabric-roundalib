package me.roundaround.roundalib.client.gui.widget.config;

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
        this.widgetLeft,
        this.widgetTop,
        this.widgetWidth,
        this.widgetHeight,
        option.getLabel());

    this.textField.setText(option.getValue());
    this.textField.setChangedListener(this::onTextChanged);
  }

  @Override
  public List<? extends Element> children() {
    return List.of(this.textField);
  }

  @Override
  public void setScrollAmount(double scrollAmount) {
    super.setScrollAmount(scrollAmount);

    this.textField.setY(this.scrolledTop);
  }

  @Override
  public void removeFocus() {
    this.textField.setFocused(false);
  }

  @Override
  public void tick() {
    this.textField.tick();
  }

  @Override
  public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    this.textField.render(matrixStack, mouseX, mouseY, delta);
  }

  @Override
  protected void onConfigValueChange(String prev, String curr) {
    if (Objects.equals(prev, curr)) {
      return;
    }
    this.textField.setText(curr);
  }

  private void onTextChanged(String text) {
    this.option.setValue(text);
  }
}
