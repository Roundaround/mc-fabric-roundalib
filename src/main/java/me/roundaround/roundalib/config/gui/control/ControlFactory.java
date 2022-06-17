package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.widget.OptionRowWidget;
import me.roundaround.roundalib.config.option.ConfigOption;

@FunctionalInterface
public interface ControlFactory<O extends ConfigOption<?, ?>> {
  public ControlWidget<O> apply(
      O configOption,
      OptionRowWidget optionRow,
      int top,
      int left,
      int height,
      int width);
}
