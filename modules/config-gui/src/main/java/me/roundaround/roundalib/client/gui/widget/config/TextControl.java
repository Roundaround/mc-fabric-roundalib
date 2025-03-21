package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.util.GuiUtil;
import me.roundaround.roundalib.config.option.StringConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class TextControl extends Control<String, StringConfigOption> {
  private final TextFieldWidget textField;

  public TextControl(MinecraftClient client, StringConfigOption option, int width, int height) {
    super(client, option, width, height);

    this.textField = this.add(
        new TextFieldWidget(client.textRenderer, width, height, this.option.getLabel()), (parent, self) -> {
          self.setDimensions(parent.getWidth(), parent.getHeight());
        });

    this.textField.setText(this.option.getPendingValue());
    this.textField.setChangedListener(this::onTextChanged);
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
  protected void update(String value, boolean isDisabled) {
    this.textField.active = !isDisabled;
    this.textField.setEditable(!isDisabled);

    if (!value.equals(this.textField.getText())) {
      this.textField.setText(value);
    }
  }

  private void onTextChanged(String text) {
    if (!this.option.validate(text)) {
      this.markInvalid();
      return;
    }

    this.option.setValue(text);
    this.markValid();
  }
}
