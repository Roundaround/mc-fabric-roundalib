package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.icon.BuiltinIcon;
import me.roundaround.roundalib.client.gui.util.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.linear.LinearLayoutWidget;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.config.option.IntConfigOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.network.chat.Component;

public class IntTextControl extends Control<Integer, IntConfigOption> {
  private final EditBox textField;
  private final IconButtonWidget plusButton;
  private final IconButtonWidget minusButton;

  public IntTextControl(Minecraft client, IntConfigOption option, int width, int height) {
    super(client, option, width, height);

    this.textField = this.add(new EditBox(client.font, width - 2, height - 2, this.option.getLabel()) {
      @Override
      public boolean charTyped(CharacterEvent input) {
        if (input.codepoint() == '-' && this.getCursorPosition() > 0) {
          return false;
        }
        return super.charTyped(input);
      }
    }, (parent, self) -> {
      int inputWidth = parent.getWidth();
      if (this.option.showStepButtons()) {
        inputWidth -= IconButtonWidget.SIZE_S + parent.getSpacing();
      }

      self.setSize(inputWidth, parent.getHeight());
    });

    this.textField.setValue(this.option.getPendingValueAsString());
    this.textField.setMaxLength(12);
    this.textField.setResponder(this::onTextChanged);

    if (this.option.showStepButtons()) {
      String modId = this.getOption().getModId();
      int step = this.getOption().getStep();
      LinearLayoutWidget stepColumn = LinearLayoutWidget.vertical();

      this.plusButton = stepColumn.add(IconButtonWidget.builder(BuiltinIcon.PLUS_9, modId)
          .small()
          .messageAndTooltip(Component.translatable(modId + ".roundalib.step_up.tooltip", step))
          .onPress((button) -> this.getOption().increment())
          .build());
      this.minusButton = stepColumn.add(IconButtonWidget.builder(BuiltinIcon.MINUS_9, modId)
          .small()
          .messageAndTooltip(Component.translatable(modId + ".roundalib.step_down.tooltip", step))
          .onPress((button) -> this.getOption().decrement())
          .build());

      this.add(stepColumn, (parent, self) -> {
        self.spacing(parent.getHeight() - 2 * IconButtonWidget.SIZE_S);
      });
    } else {
      this.plusButton = null;
      this.minusButton = null;
    }
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
  protected void update(Integer value, boolean isDisabled) {
    IntConfigOption option = this.getOption();

    this.textField.active = !isDisabled;
    this.textField.setEditable(!isDisabled);

    String strValue = option.getValueAsString(value);
    if (!strValue.equals(this.textField.getValue())) {
      this.textField.setValue(strValue);
    }

    if (option.showStepButtons()) {
      this.plusButton.active = !isDisabled && option.canIncrement();
      this.minusButton.active = !isDisabled && option.canDecrement();
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
