package me.roundaround.roundalib.config.gui;

import java.util.List;

public interface ClickableWidget extends Widget, SelectableElement {
  @Override
  public default List<SelectableElement> getSelectableElements() {
    return List.of(this);
  }
}
