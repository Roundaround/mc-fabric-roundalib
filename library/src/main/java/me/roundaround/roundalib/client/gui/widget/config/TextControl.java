package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.config.option.StringConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class TextControl extends Control<String, StringConfigOption> {
  private final TextFieldWidget textField;

  public TextControl(MinecraftClient client, StringConfigOption option, int width, int height) {
    super(client, option, width, height);

    this.textField =
        this.add(new TextFieldWidget(client.textRenderer, width, height, this.option.getLabel()),
            (parent, self) -> {
              self.setDimensions(parent.getWidth(), parent.getHeight());
            });

    this.textField.setText(this.option.getPendingValue());
    this.textField.setChangedListener(this::onTextChanged);

    this.update();
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
    if (!this.option.validate(text)) {
      this.markInvalid();
      return;
    }

    this.option.setValue(text);
    this.markValid();
  }
}
