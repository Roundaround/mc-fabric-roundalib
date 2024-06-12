package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.IconButtons;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.config.option.IntConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.TextFieldWidget;

import java.util.List;

public class IntTextControl extends Control<Integer, IntConfigOption> {
  private final TextFieldWidget textField;
  private final IconButtonWidget plusButton;
  private final IconButtonWidget minusButton;

  public IntTextControl(MinecraftClient client, IntConfigOption option, int left, int top, int width, int height) {
    super(client, option, left, top, width, height);

    int widgetLeft = this.getWidgetLeft();
    int widgetRight = this.getWidgetRight();
    int widgetTop = this.getWidgetTop();
    int widgetBottom = this.getWidgetBottom();
    int widgetWidth = this.getWidgetWidth();
    int widgetHeight = this.getWidgetHeight();

    this.textField = new TextFieldWidget(client.textRenderer, widgetLeft + 1, widgetTop + 1, widgetWidth - 2,
        widgetHeight - 2, this.option.getLabel()
    ) {
      @Override
      public boolean charTyped(char chr, int keyCode) {
        if (chr == '-' && this.getCursor() > 0) {
          return false;
        }
        return super.charTyped(chr, keyCode);
      }
    };

    this.textField.setText(this.option.getValueAsString());
    this.textField.setMaxLength(12);
    this.textField.setChangedListener(this::onTextChanged);

    if (this.option.showStepButtons()) {
      this.textField.setWidth(widgetWidth - IconButtons.SIZE_S - 4);

      this.plusButton = IconButtons.intStepButton(widgetRight - IconButtons.SIZE_S, widgetTop,
          this.option, true
      );

      this.minusButton = IconButtons.intStepButton(widgetRight - IconButtons.SIZE_S,
          widgetBottom - IconButtons.SIZE_S, this.option, false
      );
    } else {
      this.plusButton = null;
      this.minusButton = null;
    }

    this.update();
  }

  @Override
  public List<? extends Element> children() {
    if (this.option.showStepButtons()) {
      return List.of(this.textField, this.plusButton, this.minusButton);
    } else {
      return List.of(this.textField);
    }
  }

  @Override
  public void refreshPositions() {
    int widgetLeft = this.getWidgetLeft();
    int widgetRight = this.getWidgetRight();
    int widgetTop = this.getWidgetTop();
    int widgetBottom = this.getWidgetBottom();
    int widgetWidth = this.getWidgetWidth();
    int widgetHeight = this.getWidgetHeight();

    this.textField.setPosition(widgetLeft + 1, widgetTop + 1);
    this.textField.setDimensions(widgetWidth - 2, widgetHeight - 2);

    if (this.option.showStepButtons()) {
      this.textField.setWidth(widgetWidth - IconButtons.SIZE_S - 4);
      this.plusButton.setPosition(widgetRight - IconButtons.SIZE_S, widgetTop);
      this.minusButton.setPosition(
          widgetRight - IconButtons.SIZE_S, widgetBottom - IconButtons.SIZE_S);
    }
  }

  @Override
  public void renderPositional(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    this.textField.render(drawContext, mouseX, mouseY, delta);
    if (this.option.showStepButtons()) {
      this.plusButton.render(drawContext, mouseX, mouseY, delta);
      this.minusButton.render(drawContext, mouseX, mouseY, delta);
    }
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
    IntConfigOption option = this.getOption();

    boolean disabled = option.isDisabled();
    this.textField.active = !disabled;
    this.textField.setEditable(!disabled);

    String value = option.getValueAsString();
    if (!value.equals(this.textField.getText())) {
      this.textField.setText(value);
    }

    if (this.option.showStepButtons()) {
      this.plusButton.active = !disabled && option.canIncrement();
      this.minusButton.active = !disabled && option.canDecrement();
    }
  }

  private void onTextChanged(String value) {
    try {
      int parsed = Integer.parseInt(value);
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
}
