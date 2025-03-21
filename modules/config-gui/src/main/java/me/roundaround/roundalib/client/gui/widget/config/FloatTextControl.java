package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.util.GuiUtil;
import me.roundaround.roundalib.config.option.FloatConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

public class FloatTextControl extends Control<Float, FloatConfigOption> {
  private static final List<Character> ALLOWED_SPECIAL_CHARS = List.of('.', ',');

  private final TextFieldWidget textField;

  public FloatTextControl(MinecraftClient client, FloatConfigOption option, int width, int height) {
    super(client, option, width, height);

    this.textField = this.add(new TextFieldWidget(client.textRenderer, width - 2, height - 2, this.option.getLabel()) {
      @Override
      public boolean charTyped(char chr, int keyCode) {
        if (chr == '-' && this.getCursor() > 0) {
          return false;
        }

        if (!isCharAllowed(chr)) {
          return false;
        }

        return super.charTyped(chr, keyCode);
      }
    }, (parent, self) -> {
      self.setDimensions(parent.getWidth(), parent.getHeight());
    });

    this.textField.setText(this.option.getPendingValueAsString());
    this.textField.setMaxLength(12);
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
  protected void update(Float value, boolean isDisabled) {
    this.textField.active = !isDisabled;
    this.textField.setEditable(!isDisabled);

    try {
      float parsed = this.parseFloat(this.textField.getText());
      if (!this.getOption().areValuesEqual(value, parsed)) {
        this.textField.setText(this.getOption().toString());
      }
    } catch (Exception e) {
      this.textField.setText(this.getOption().toString());
    }
  }

  private void onTextChanged(String value) {
    try {
      float parsed = this.parseFloat(value);
      if (this.option.validate(parsed)) {
        this.option.setValue(parsed);
        this.markValid();
      } else {
        this.markInvalid();
      }
    } catch (Exception e) {
      this.markInvalid();
    }
  }

  private float parseFloat(String value) throws ParseException {
    DecimalFormat format = new DecimalFormat("#");
    return format.parse(value).floatValue();
  }

  private static boolean isCharAllowed(char chr) {
    return Character.isDigit(chr) || ALLOWED_SPECIAL_CHARS.contains(chr);
  }
}
