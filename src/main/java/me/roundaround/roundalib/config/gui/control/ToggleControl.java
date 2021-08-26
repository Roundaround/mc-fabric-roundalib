package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.util.math.MatrixStack;

public class ToggleControl extends Control<Boolean> {
    public ToggleControl(OptionRow parent, ConfigOption<Boolean> configOption) {
        super(parent, configOption);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

    }
}
