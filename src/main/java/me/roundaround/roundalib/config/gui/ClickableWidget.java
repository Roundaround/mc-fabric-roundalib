package me.roundaround.roundalib.config.gui;

import net.minecraft.client.gui.Selectable;

public abstract class ClickableWidget<T> extends Widget<T> implements Selectable {
  protected boolean focused;

  protected ClickableWidget(T parent, int top, int left, int height, int width) {
    super(parent, top, left, height, width);
  }

  @Override
  public SelectionType getType() {
    if (this.focused) {
      return Selectable.SelectionType.FOCUSED;
    } else if (this.hovered) {
      return Selectable.SelectionType.HOVERED;
    }
    return Selectable.SelectionType.NONE;
  }

  public boolean changeFocus(boolean lookForwards) {
    focused = !focused;
    onFocusedChanged(focused);
    return focused;
  }

  protected boolean isHoveredOrFocused() {
    return hovered || focused;
  }

  protected void onFocusedChanged(boolean newFocused) {
  }
}
