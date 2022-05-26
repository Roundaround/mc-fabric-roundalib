package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.AbstractClickableWidget;
import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.option.ConfigOption;

public abstract class AbstractClickableControlWidget<T, U extends ConfigOption<T>> extends AbstractClickableWidget<OptionRow>
    implements ClickableControlWidget<T, U>, ControlWidget<T, U> {
  protected U configOption;

  protected AbstractClickableControlWidget(OptionRow parent, int top, int left, int height, int width) {
    super(parent, top, left, height, width);
  }

  @Override
  public U getConfigOption() {
    return configOption;
  }

  @Override
  public void setConfigOption(U configOption) {
    this.configOption = configOption;
  }
}
