package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.OptionRow;
import net.minecraft.client.util.math.MatrixStack;

public class IntInputControl extends AbstractControlWidget<Integer> {
  public IntInputControl(
      OptionRow parent,
      int top,
      int left,
      int height,
      int width) {
    super(parent, top, left, height, width);
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
  }
}
