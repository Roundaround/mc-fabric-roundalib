package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.ConfigScreen;
import me.roundaround.roundalib.config.gui.widget.AbstractWidget;
import me.roundaround.roundalib.config.gui.widget.ConfigListWidget;
import me.roundaround.roundalib.config.gui.widget.OptionRowWidget;
import me.roundaround.roundalib.config.option.ConfigOption;

public abstract class AbstractControlWidget<O extends ConfigOption<?, ?>>
    extends AbstractWidget<OptionRowWidget>
    implements ControlWidget<O> {
  protected O configOption;
  protected boolean valid = true;

  protected AbstractControlWidget(O configOption, OptionRowWidget parent, int top, int left, int height, int width) {
    super(parent, top, left, height, width);
    this.configOption = configOption;
  }

  @Override
  public O getConfigOption() {
    return configOption;
  }

  @Override
  public void markValid() {
    valid = true;
    getConfigScreen().markValid(getOptionRow());
  }

  @Override
  public void markInvalid() {
    valid = false;
    getConfigScreen().markInvalid(getOptionRow());
  }

  @Override
  public boolean isValid() {
    return valid;
  }

  public OptionRowWidget getOptionRow() {
    return getParent();
  }

  public ConfigListWidget getConfigList() {
    return getOptionRow().getConfigList();
  }

  public ConfigScreen getConfigScreen() {
    return getConfigList().getConfigScreen();
  }
}
