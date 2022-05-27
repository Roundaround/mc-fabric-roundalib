package me.roundaround.roundalib.config.gui.control;

import java.util.List;

import me.roundaround.roundalib.config.gui.IntStepButton;
import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.gui.SelectableElement;
import me.roundaround.roundalib.config.gui.compat.SelectableElementTextFieldWidget;
import me.roundaround.roundalib.config.option.IntConfigOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class IntInputControl extends AbstractControlWidget<IntConfigOption> {
  private SelectableElementTextFieldWidget textBox;
  private IntStepButton incrementButton;
  private IntStepButton decrementButton;

  public IntInputControl(
      IntConfigOption configOption,
      OptionRow parent,
      int top,
      int left,
      int height,
      int width) {
    super(configOption, parent, top, left, height, width);
  }

  @Override
  public void init() {
    textBox = new SelectableElementTextFieldWidget(TEXT_RENDERER, left + 1, top + 1,
        width - 2 - (configOption.showStepButtons() ? 10 : 0), height - 2,
        new LiteralText("Foo")) {
      @Override
      protected boolean isCharAllowed(char chr) {
        return Character.isDigit(chr) || Character.valueOf('-').equals(chr);
      }
    };
    textBox.setMaxLength(12);
    textBox.setChangedListener(this::onTextFieldValueChange);
    textBox.setText(configOption.getValue().toString());

    if (configOption.showStepButtons()) {
      incrementButton = new IntStepButton(this, true, top, right - IntStepButton.WIDTH + 1);
      decrementButton = new IntStepButton(this, false, bottom - IntStepButton.HEIGHT + 1,
          right - IntStepButton.WIDTH + 1);
    }

    configOption.subscribeToValueChanges(this::onConfigValueChange);
  }

  @Override
  public List<SelectableElement> getSelectableElements() {
    if (configOption.showStepButtons()) {
      return List.of(textBox, incrementButton, decrementButton);
    }
    return List.of(textBox);
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    textBox.render(matrices, mouseX, mouseY, delta);
    if (configOption.showStepButtons()) {
      incrementButton.render(matrices, mouseX, mouseY, delta);
      decrementButton.render(matrices, mouseX, mouseY, delta);
    }
  }

  @Override
  public void tick() {
    textBox.tick();
    if (configOption.showStepButtons()) {
      incrementButton.tick();
      decrementButton.tick();
    }
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    return getSelectableElements().stream().anyMatch((element) -> {
      return element.keyPressed(keyCode, scanCode, modifiers);
    });
  }

  @Override
  public boolean charTyped(char chr, int modifiers) {
    return getSelectableElements().stream().anyMatch((element) -> {
      return element.charTyped(chr, modifiers);
    });
  }

  @Override
  public boolean onMouseClicked(double mouseX, double mouseY, int button) {
    return getSelectableElements().stream().anyMatch((element) -> {
      return element.mouseClicked(mouseX, mouseY, button);
    });
  }

  @Override
  public boolean onMouseReleased(double mouseX, double mouseY, int button) {
    return getSelectableElements().stream().anyMatch((element) -> {
      return element.mouseReleased(mouseX, mouseY, button);
    });
  }

  @Override
  public boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    return getSelectableElements().stream().anyMatch((element) -> {
      return element.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    });
  }

  private void onTextFieldValueChange(String value) {
    int parsed = 0;
    try {
      parsed = Integer.parseInt(value);
    } catch (Exception e) {
    }
    configOption.setValue(parsed);
  }

  private void onConfigValueChange(Integer prev, Integer curr) {
    if (!curr.equals(prev)) {
      textBox.setText(curr.toString());
    }
  }

  @Override
  public void moveTop(int top) {
    super.moveTop(top);
    textBox.y = top + 1;
  }
}
