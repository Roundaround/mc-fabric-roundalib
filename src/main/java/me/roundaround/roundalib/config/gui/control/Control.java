package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.option.ConfigOption;

public interface Control<O extends ConfigOption<?, ?>> {
  O getConfigOption();

  void markValid();

  void markInvalid();

  boolean isValid();
}
