package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.option.ConfigOption;

import java.util.function.BiFunction;

@FunctionalInterface
public interface ControlFactory<T> extends BiFunction<OptionRow, ConfigOption<T>, Control<T>> {
}
