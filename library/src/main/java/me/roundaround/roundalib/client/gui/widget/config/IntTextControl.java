package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.layout.FillerWidget;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.layout.LinearLayoutWidget;
import me.roundaround.roundalib.config.option.IntConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class IntTextControl extends Control<Integer, IntConfigOption> {
  private final TextFieldWidget textField;
  private final IconButtonWidget plusButton;
  private final IconButtonWidget minusButton;

  public IntTextControl(MinecraftClient client, IntConfigOption option, int width, int height) {
    super(client, option, width, height);

    this.textField = this.add(new TextFieldWidget(client.textRenderer,
        width - 2,
        height - 2,
        this.option.getLabel()) {
      @Override
      public boolean charTyped(char chr, int keyCode) {
        if (chr == '-' && this.getCursor() > 0) {
          return false;
        }
        return super.charTyped(chr, keyCode);
      }
    }, (parent, self) -> {
      int inputWidth = parent.getWidth();
      if (this.option.showStepButtons()) {
        inputWidth -= IconButtonWidget.SIZE_S + parent.getSpacing();
      }

      self.setDimensions(inputWidth, parent.getHeight());
    });

    this.textField.setText(this.option.getPendingValueAsString());
    this.textField.setMaxLength(12);
    this.textField.setChangedListener(this::onTextChanged);

    if (this.option.showStepButtons()) {
      LinearLayoutWidget stepColumn = this.add(LinearLayoutWidget.vertical(), (parent, self) -> {
        self.setDimensions(IconButtonWidget.SIZE_S, parent.getHeight());
      });

      String modId = this.getOption().getModId();
      int step = this.getOption().getStep();

      this.plusButton =
          stepColumn.add(IconButtonWidget.builder(IconButtonWidget.BuiltinIcon.PLUS_9, modId)
              .small()
              .messageAndTooltip(Text.translatable(modId + ".roundalib.step_up.tooltip", step))
              .onPress((button) -> this.getOption().increment())
              .build());

      stepColumn.add(FillerWidget.empty(), (parent, self) -> {
        self.setHeight(parent.getHeight() - 2 * IconButtonWidget.SIZE_S);
      });

      this.minusButton =
          stepColumn.add(IconButtonWidget.builder(IconButtonWidget.BuiltinIcon.MINUS_9, modId)
              .small()
              .messageAndTooltip(Text.translatable(modId + ".roundalib.step_down.tooltip", step))
              .onPress((button) -> this.getOption().decrement())
              .build());
    } else {
      this.plusButton = null;
      this.minusButton = null;
    }

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
    IntConfigOption option = this.getOption();
    boolean disabled = option.isDisabled();

    this.textField.active = !disabled;
    this.textField.setEditable(!disabled);

    String value = option.getPendingValueAsString();
    if (!value.equals(this.textField.getText())) {
      this.textField.setText(value);
    }

    if (option.showStepButtons()) {
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
