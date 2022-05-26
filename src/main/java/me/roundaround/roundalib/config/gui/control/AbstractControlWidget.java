package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.AbstractWidget;
import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.option.ConfigOption;

public abstract class AbstractControlWidget<T, U extends ConfigOption<T, ?>> extends AbstractWidget<OptionRow>
    implements Control<U> {
  protected U configOption;

  protected AbstractControlWidget(OptionRow parent, U configOption, int top, int left, int height, int width) {
    super(parent, top, left, height, width);
    this.configOption = configOption;
  }

  @Override
  public U getConfigOption() {
    return configOption;
  }
}
