package me.roundaround.roundalib.config.gui.widget;

import java.util.function.Consumer;

import me.roundaround.roundalib.config.gui.SelectableElement;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

public class TextFieldWidget extends net.minecraft.client.gui.widget.TextFieldWidget implements SelectableElement {
  private Consumer<Boolean> focusChangedListener;

  public TextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height,
      TextFieldWidget copyFrom, Text text) {
    super(textRenderer, x, y, width, height, copyFrom, text);
  }

  public TextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
    super(textRenderer, x, y, width, height, text);
  }

  @Override
  public boolean charTyped(char chr, int modifiers) {
    if (!isCharAllowed(chr)) {
      return false;
    }
    return super.charTyped(chr, modifiers);
  }

  @Override
  public void setTextFieldFocused(boolean focused) {
    setIsFocused(focused);
  }

  @Override
  protected void onFocusedChanged(boolean newFocused) {
    super.onFocusedChanged(newFocused);
    if (focusChangedListener != null) {
      focusChangedListener.accept(newFocused);
    }
  }

  @Override
  public boolean setIsFocused(boolean newFocused) {
    if (newFocused == isFocused()) {
      return false;
    }
    setFocused(newFocused);
    onFocusedChanged(newFocused);
    return true;
  }

  @Override
  public void setFocusChangedListener(Consumer<Boolean> focusChangedListener) {
    this.focusChangedListener = focusChangedListener;
  }

  protected boolean isCharAllowed(char chr) {
    return true;
  }
}