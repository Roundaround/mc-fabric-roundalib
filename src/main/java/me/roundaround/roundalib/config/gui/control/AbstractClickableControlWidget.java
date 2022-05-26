package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.AbstractClickableWidget;
import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.option.ConfigOption;

public abstract class AbstractClickableControlWidget<T> extends AbstractClickableWidget<OptionRow>
    implements ClickableControlWidget<T>, ControlWidget<T> {
  protected ConfigOption<T> configOption;

  protected AbstractClickableControlWidget(OptionRow parent, int top, int left, int height, int width) {
    super(parent, top, left, height, width);
  }

  @Override
  public ConfigOption<T> getConfigOption() {
    return configOption;
  }

  @Override
  public void setConfigOption(ConfigOption<T> configOption) {
    this.configOption = configOption;
  }
}
