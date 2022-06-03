package me.roundaround.roundalib.config.gui;

import java.util.function.Consumer;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;

public interface SelectableElement extends Selectable, Element {
  boolean setIsFocused(boolean focused);

  boolean isFocused();

  void setFocusChangedListener(Consumer<Boolean> listener);
}
