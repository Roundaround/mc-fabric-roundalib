package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.OptionRow;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ToggleControl extends ButtonControl<Boolean> {
  private Text cachedText;

  public ToggleControl(
      OptionRow parent,
      int top,
      int left,
      int height,
      int width) {
    super(parent, top, left, height, width);
  }

  @Override
  public void init() {
    configOption.subscribeToValueChanges(this::onConfigValueChange);
    cachedText = new TranslatableText(configOption.getValue() ? "config.toggle.enabled" : "config.toggle.disabled");
  }

  @Override
  protected Text getCurrentText() {
    return cachedText;
  }

  @Override
  protected boolean handleValidClick(double mouseX, double mouseY, int button) {
    configOption.setValue(!configOption.getValue());
    return true;
  }

  private void onConfigValueChange(boolean prev, boolean curr) {
    cachedText = new TranslatableText(curr ? "config.toggle.enabled" : "config.toggle.disabled");
  }
}
