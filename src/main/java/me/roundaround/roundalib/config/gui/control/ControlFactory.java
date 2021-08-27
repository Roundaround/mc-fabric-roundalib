package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.option.ConfigOption;

@FunctionalInterface
public interface ControlFactory<T> {
  Control<T> apply(
      OptionRow parent, ConfigOption<T> configOption, int top, int left, int height, int width);
}
