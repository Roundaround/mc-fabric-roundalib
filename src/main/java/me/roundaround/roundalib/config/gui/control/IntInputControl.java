package me.roundaround.roundalib.config.gui.control;

import java.util.List;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.gui.SelectableElement;
import me.roundaround.roundalib.config.gui.compat.SelectableElementTextFieldWidget;
import me.roundaround.roundalib.config.option.IntConfigOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class IntInputControl extends AbstractControlWidget<Integer, IntConfigOption> {
  private SelectableElementTextFieldWidget textBox;

  public IntInputControl(
      OptionRow parent,
      IntConfigOption configOption,
      int top,
      int left,
      int height,
      int width) {
    super(parent, configOption, top, left, height, width);
  }

  @Override
  public void init() {
    textBox = new SelectableElementTextFieldWidget(TEXT_RENDERER, left + 1, top + 1,
        width - 2, height - 2,
        new LiteralText("Foo")) {
      @Override
      protected boolean isCharAllowed(char chr) {
        return Character.isDigit(chr) || Character.valueOf('-').equals(chr);
      }
    };
    textBox.setMaxLength(50);
    textBox.setChangedListener(this::onTextFieldValueChange);
    textBox.setText(configOption.getValue().toString());

    configOption.subscribeToValueChanges(this::onConfigValueChange);
  }

  @Override
  public List<SelectableElement> getSelectableElements() {
    return List.of(textBox);
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    textBox.render(matrices, mouseX, mouseY, delta);
  }

  @Override
  public void tick() {
    textBox.tick();
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    return textBox.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean charTyped(char chr, int modifiers) {
    return textBox.charTyped(chr, modifiers);
  }

  @Override
  public boolean onMouseClicked(double mouseX, double mouseY, int button) {
    return textBox.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean onMouseReleased(double mouseX, double mouseY, int button) {
    return textBox.mouseReleased(mouseX, mouseY, button);
  }

  @Override
  public boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    return textBox.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
  }

  private void onTextFieldValueChange(String value) {
    configOption.setValue(Integer.parseInt(value));
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
