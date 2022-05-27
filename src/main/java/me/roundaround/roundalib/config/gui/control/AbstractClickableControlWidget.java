package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.AbstractClickableWidget;
import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.option.ConfigOption;

public abstract class AbstractClickableControlWidget<O extends ConfigOption<?, ?>>
    extends AbstractClickableWidget<OptionRow>
    implements Control<O> {
  protected O configOption;

  protected AbstractClickableControlWidget(O configOption, OptionRow parent, int top, int left, int height, int width) {
    super(parent, top, left, height, width);
    this.configOption = configOption;
  }

  @Override
  public O getConfigOption() {
    return configOption;
  }
}
