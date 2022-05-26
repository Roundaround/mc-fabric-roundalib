package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.gui.AbstractWidget;
import me.roundaround.roundalib.config.option.ConfigOption;

public abstract class AbstractControlWidget<T> extends AbstractWidget<OptionRow> implements ControlWidget<T> {
  protected ConfigOption<T> configOption;

  protected AbstractControlWidget(OptionRow parent, int top, int left, int height, int width) {
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
