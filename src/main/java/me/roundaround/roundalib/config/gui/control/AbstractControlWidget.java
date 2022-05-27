package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.AbstractWidget;
import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.option.ConfigOption;

public abstract class AbstractControlWidget<O extends ConfigOption<?, ?>> extends AbstractWidget<OptionRow>
    implements Control<O> {
  protected O configOption;

  protected AbstractControlWidget(O configOption, OptionRow parent, int top, int left, int height, int width) {
    super(parent, top, left, height, width);
    this.configOption = configOption;
  }

  @Override
  public O getConfigOption() {
    return configOption;
  }
}
