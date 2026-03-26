package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.util.GuiUtil;
import me.roundaround.roundalib.config.option.FloatConfigOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

public class FloatTextControl extends Control<Float, FloatConfigOption> {
  private static final List<Character> ALLOWED_SPECIAL_CHARS = List.of('.', ',');

  private final EditBox textField;

  public FloatTextControl(Minecraft client, FloatConfigOption option, int width, int height) {
    super(client, option, width, height);

    this.textField = this.add(new EditBox(client.font, width - 2, height - 2, this.option.getLabel()) {
      @Override
      public boolean charTyped(CharacterEvent input) {
        if (input.codepoint() == '-' && this.getCursorPosition() > 0) {
          return false;
        }

        if (!isCharAllowed((char)input.codepoint())) {
          return false;
        }

        return super.charTyped(input);
      }
    }, (parent, self) -> {
      self.setSize(parent.getWidth(), parent.getHeight());
    });

    this.textField.setValue(this.option.getPendingValueAsString());
    this.textField.setMaxLength(12);
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
  protected void update(Float value, boolean isDisabled) {
    this.textField.active = !isDisabled;
    this.textField.setEditable(!isDisabled);

    try {
      float parsed = this.parseFloat(this.textField.getValue());
      if (!this.getOption().areValuesEqual(value, parsed)) {
        this.textField.setValue(this.getOption().toString());
      }
    } catch (Exception e) {
      this.textField.setValue(this.getOption().toString());
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
