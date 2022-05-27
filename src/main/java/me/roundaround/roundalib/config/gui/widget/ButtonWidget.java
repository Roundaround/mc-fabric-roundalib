package me.roundaround.roundalib.config.gui.widget;

import java.util.function.Consumer;

import me.roundaround.roundalib.config.gui.SelectableElement;
import net.minecraft.text.Text;

public class ButtonWidget extends net.minecraft.client.gui.widget.ButtonWidget implements SelectableElement {
  protected Consumer<Boolean> focusChangeListener;
  
  public ButtonWidget(int top, int left, int height, int width, Text message, PressAction onPress) {
    this(top, left, height, width, message, onPress, EMPTY);
  }

  public ButtonWidget(int top, int left, int height, int width, Text message, PressAction onPress,
      TooltipSupplier tooltipSupplier) {
    super(left, top, width, height, message, onPress, tooltipSupplier);
  }

  @Override
  public boolean setIsFocused(boolean newFocused) {
    super.setFocused(newFocused);
    onFocusedChanged(newFocused);
    return true;
  }

  @Override
  protected void onFocusedChanged(boolean newFocused) {
    super.onFocusedChanged(newFocused);
    if (focusChangeListener != null) {
      focusChangeListener.accept(newFocused);
    }
  }

  @Override
  public void setFocusChangedListener(Consumer<Boolean> listener) {
    focusChangeListener = listener;
  }
}
