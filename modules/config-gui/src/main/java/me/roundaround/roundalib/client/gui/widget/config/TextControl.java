package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.util.GuiUtil;
import me.roundaround.roundalib.config.option.StringConfigOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;

public class TextControl extends Control<String, StringConfigOption> {
  private final EditBox textField;

  public TextControl(Minecraft client, StringConfigOption option, int width, int height) {
    super(client, option, width, height);

    this.textField = this.add(
        new EditBox(client.font, width, height, this.option.getLabel()), (parent, self) -> {
          self.setSize(parent.getWidth(), parent.getHeight());
        });

    this.textField.setValue(this.option.getPendingValue());
    this.textField.setResponder(this::onTextChanged);
  }

  @Override
  public void markInvalid() {
    this.textField.setTextColor(GuiUtil.ERROR_COLOR);
    super.markInvalid();
  }

  @Override
  public void markValid() {
    this.textField.setTextColor(GuiUtil.LABEL_COLOR);
    super.markValid();
  }

  @Override
  protected void update(String value, boolean isDisabled) {
    this.textField.active = !isDisabled;
    this.textField.setEditable(!isDisabled);

    if (!value.equals(this.textField.getValue())) {
      this.textField.setValue(value);
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
