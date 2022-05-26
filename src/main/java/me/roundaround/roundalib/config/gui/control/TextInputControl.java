package me.roundaround.roundalib.config.gui.control;

import java.util.List;

import me.roundaround.roundalib.config.gui.OptionRow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class TextInputControl extends AbstractControlWidget<String> {
  private TextFieldWidget textBox;

  public TextInputControl(
      OptionRow parent,
      int top,
      int left,
      int height,
      int width) {
    super(parent, top, left, height, width);
  }

  @Override
  public void init() {
    textBox = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, left + 1, top + 1, width - 2, height - 2,
        new LiteralText("Foo"));
    textBox.setMaxLength(50);
    textBox.setChangedListener(this::onTextFieldValueChange);
    textBox.setText(configOption.getValue());

    configOption.subscribeToValueChanges(this::onConfigValueChange);
  }

  @Override
  public <S extends Element & Selectable> List<S> getSelectableElements() {
    return List.of((S) textBox);
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
    configOption.setValue(value);
  }

  private void onConfigValueChange(String prev, String curr) {
    if (curr != prev) {
      textBox.setText(curr);
    }
  }

  @Override
  public void moveTop(int top) {
    super.moveTop(top);
    textBox.y = top + 1;
  }
}
