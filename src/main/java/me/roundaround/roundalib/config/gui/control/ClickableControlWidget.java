package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.ClickableWidget;
import me.roundaround.roundalib.config.option.ConfigOption;

public interface ClickableControlWidget<T, U extends ConfigOption<T>> extends ClickableWidget, Control<T, U> {

}
