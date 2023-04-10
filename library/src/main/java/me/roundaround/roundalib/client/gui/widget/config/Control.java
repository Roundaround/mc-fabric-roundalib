package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.gui.AbstractParentElement;

public abstract class Control<O extends ConfigOption<?, ?>> extends AbstractParentElement {
  protected final ConfigListWidget.OptionEntry<O> parent;
  protected final O option;

  protected boolean valid;

  protected Control(ConfigListWidget.OptionEntry<O> parent) {
    this.parent = parent;
    this.option = parent.getOption();
  }

  public O getOption() {
    return this.option;
  }

  public boolean isValid() {
    return this.valid;
  }

  public void markValid() {
    this.valid = true;
  }

  public void markInvalid() {
    this.valid = false;
  }

  public boolean isDisabled() {
    return this.option.isDisabled();
  }
}
