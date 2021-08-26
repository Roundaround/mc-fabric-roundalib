package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;

public abstract class Control<T> extends DrawableHelper implements Drawable, Element {
    protected final OptionRow parent;
    protected final ConfigOption<T> configOption;

    protected Control(OptionRow parent, ConfigOption<T> configOption) {
        this.parent = parent;
        this.configOption = configOption;
    }
}
