package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.value.ListOptionValue;
import net.minecraft.client.util.math.MatrixStack;

public class OptionListControl<T extends ListOptionValue<T>> extends Control<T> {
  public OptionListControl(
      OptionRow parent, ConfigOption<T> configOption, int top, int left, int height, int width) {
    super(parent, configOption, top, left, height, width);
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {}
}
