package me.roundaround.roundalib.config.gui.widget;

import java.util.List;

import me.roundaround.roundalib.config.gui.SelectableElement;

public interface ClickableWidget extends Widget, SelectableElement {
  @Override
  public default List<SelectableElement> getSelectableElements() {
    return List.of(this);
  }
}
