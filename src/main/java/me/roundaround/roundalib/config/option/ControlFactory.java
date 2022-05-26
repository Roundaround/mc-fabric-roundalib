package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.gui.control.ControlWidget;

@FunctionalInterface
public interface ControlFactory<T> {
  ControlWidget<T> apply(OptionRow parent, int top, int left, int height, int width);
}
