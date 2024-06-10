package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.config.option.FloatConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

public class FloatTextControl extends Control<Float, FloatConfigOption> {
  private static final List<Character> ALLOWED_SPECIAL_CHARS = List.of('.', ',');

  private final TextFieldWidget textField;

  public FloatTextControl(MinecraftClient client, FloatConfigOption option, int left, int top, int width, int height) {
    super(client, option, left, top, width, height);

    this.textField = new TextFieldWidget(client.textRenderer, this.getWidgetLeft() + 1, this.getWidgetTop() + 1,
        this.getWidgetWidth() - 2, this.getWidgetHeight() - 2, this.option.getLabel()
    ) {
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
    };

    this.textField.setText(this.option.getValueAsString());
    this.textField.setMaxLength(12);
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

    float value = this.getOption().getPendingValue();
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

  private float parseFloat(String value) throws ParseException {
    DecimalFormat format = new DecimalFormat("#");
    return format.parse(value).floatValue();
  }

  private static boolean isCharAllowed(char chr) {
    return Character.isDigit(chr) || ALLOWED_SPECIAL_CHARS.contains(chr);
  }
}
