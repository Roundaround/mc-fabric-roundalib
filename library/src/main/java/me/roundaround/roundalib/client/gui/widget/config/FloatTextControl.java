package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.config.option.FloatConfigOption;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;

public class FloatTextControl extends Control<Float, FloatConfigOption> {
  private static final List<Character> ALLOWED_SPECIAL_CHARS = List.of('.', ',');

  private final TextFieldWidget textField;

  public FloatTextControl(ConfigListWidget.OptionEntry<Float, FloatConfigOption> parent) {
    super(parent);

    this.textField = new TextFieldWidget(parent.getTextRenderer(),
        this.widgetLeft + 1,
        this.widgetTop + 1,
        this.widgetWidth - 2,
        this.widgetHeight - 2,
        this.option.getLabel()) {
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

    this.textField.setText(this.option.getValue().toString());
    this.textField.setMaxLength(12);
    this.textField.setChangedListener(this::onTextChanged);
    this.textField.active = this.disabled;
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
  protected void onConfigValueChange(Float prev, Float curr) {
    try {
      float parsed = this.parseFloat(this.textField.getText());
      if (Math.abs(curr - parsed) < MathHelper.EPSILON) {
        return;
      }
      this.textField.setText(curr.toString());
    } catch (Exception e) {
      this.textField.setText(curr.toString());
    }
  }

  @Override
  protected void onDisabledChange(boolean prev, boolean curr) {
    this.textField.active = !disabled;
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
