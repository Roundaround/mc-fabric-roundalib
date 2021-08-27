package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.gui.Widget;
import me.roundaround.roundalib.config.option.ConfigOption;

public abstract class Control<T> extends Widget<OptionRow> {
    protected final ConfigOption<T> configOption;

    protected Control(OptionRow parent, ConfigOption<T> configOption, int top, int left, int height, int width) {
        super(parent, top, left, height, width);
        this.configOption = configOption;
    }
}
