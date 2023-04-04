package me.roundaround.roundalib.config.gui.widget;

import java.util.function.Consumer;

import me.roundaround.roundalib.config.ModConfig;
import net.minecraft.client.gui.Selectable;

public abstract class AbstractClickableWidget<T> extends AbstractWidget<T> implements ClickableWidget {
  protected boolean focused;
  protected Consumer<Boolean> focusChangeListener;

  protected AbstractClickableWidget(T parent, ModConfig config, int top, int left, int height, int width) {
    super(parent, config, top, left, height, width);
  }

  @Override
  public SelectionType getType() {
    if (this.isFocused()) {
      return Selectable.SelectionType.FOCUSED;
    } else if (this.hovered) {
      return Selectable.SelectionType.HOVERED;
    }
    return Selectable.SelectionType.NONE;
  }

  protected boolean isHoveredOrFocused() {
    return hovered || isFocused();
  }

  @Override
  public boolean isFocused() {
    return focused;
  }

  @Override
  public void setFocusChangedListener(Consumer<Boolean> listener) {
    focusChangeListener = listener;
  }

  @Override
  public boolean setIsFocused(boolean focused) {
    this.focused = focused;
    if (focusChangeListener != null) {
      focusChangeListener.accept(focused);
    }
    return true;
  }
}
