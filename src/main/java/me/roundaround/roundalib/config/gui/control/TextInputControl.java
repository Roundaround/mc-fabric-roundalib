package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.util.math.MatrixStack;

public class TextInputControl extends Control<String> {
    public TextInputControl(OptionRow parent, ConfigOption<String> configOption) {
        super(parent, configOption);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

    }
}
