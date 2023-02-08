package me.roundaround.roundalib.config.gui.widget;

import java.util.function.Consumer;

import me.roundaround.roundalib.config.gui.SelectableElement;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;

public class TextFieldWidget extends net.minecraft.client.gui.widget.TextFieldWidget implements SelectableElement {
  private Consumer<Boolean> focusChangedListener;
  private Widget parent;
  private boolean dragging = false;

  public TextFieldWidget(
      Widget parent,
      TextRenderer textRenderer,
      int x,
      int y,
      int width,
      int height,
      TextFieldWidget copyFrom,
      Text text) {
    super(textRenderer, x, y, width, height, copyFrom, text);
    this.parent = parent;
  }

  public TextFieldWidget(
      Widget parent,
      TextRenderer textRenderer,
      int x,
      int y,
      int width,
      int height,
      Text text) {
    super(textRenderer, x, y, width, height, text);
    this.parent = parent;
  }

  @Override
  public boolean charTyped(char chr, int modifiers) {
    if (!isCharAllowed(chr)) {
      return false;
    }
    return super.charTyped(chr, modifiers);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (!parent.isMouseOver(mouseX, mouseY)) {
      return false;
    }
    dragging = isMouseOver(mouseX, mouseY);
    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    if (!dragging) {
      return false;
    }
    return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    dragging = false;
    return super.mouseReleased(mouseX, mouseY, button);
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

  @Override
  public void appendClickableNarrations(NarrationMessageBuilder builder) {
    builder.put(NarrationPart.TITLE, getNarrationMessage());
  }

  protected boolean isCharAllowed(char chr) {
    return true;
  }
}
