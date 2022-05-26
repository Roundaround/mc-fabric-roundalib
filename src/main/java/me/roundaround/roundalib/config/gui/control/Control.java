package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.option.ConfigOption;

public interface Control<T extends ConfigOption<?, ?>> {
  T getConfigOption();
}
