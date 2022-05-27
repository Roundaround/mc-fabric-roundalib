package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.AbstractClickableWidget;
import me.roundaround.roundalib.config.gui.ConfigList;
import me.roundaround.roundalib.config.gui.ConfigScreen;
import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.option.ConfigOption;

public abstract class AbstractClickableControlWidget<O extends ConfigOption<?, ?>>
    extends AbstractClickableWidget<OptionRow>
    implements Control<O> {
  protected O configOption;
  protected boolean valid = true;

  protected AbstractClickableControlWidget(O configOption, OptionRow parent, int top, int left, int height, int width) {
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

  public OptionRow getOptionRow() {
    return getParent();
  }

  public ConfigList getConfigList() {
    return getOptionRow().getConfigList();
  }

  public ConfigScreen getConfigScreen() {
    return getConfigList().getConfigScreen();
  }
}
