package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.RoundaLibMod;
import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ToggleControl extends ButtonControl<Boolean> {
    public ToggleControl(OptionRow parent, ConfigOption<Boolean> configOption, int top, int left, int height, int width) {
        super(parent, configOption, top, left, height, width);
    }

    @Override
    protected Text getCurrentText() {
        return new LiteralText(configOption.getValue() ? "Enabled" : "Disabled");
    }

    @Override
    protected boolean handleValidClick(double mouseX, double mouseY, int button) {
        RoundaLibMod.LOGGER.info("Clicked button with mouse button #{}", button);

        this.configOption.setValue(!this.configOption.getValue());

        return true;
    }
}
