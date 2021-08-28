package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ToggleControl extends ButtonControl<Boolean> {
  private Text cachedText;

  public ToggleControl(
      OptionRow parent,
      ConfigOption<Boolean> configOption,
      int top,
      int left,
      int height,
      int width) {
    super(parent, configOption, top, left, height, width);
    this.configOption.subscribeToValueChanges(this::onConfigValueChange);
    this.cachedText = new TranslatableText(configOption.getValue() ? "config.toggle.enabled" : "config.toggle.disabled");
  }

  @Override
  protected Text getCurrentText() {
    return this.cachedText;
  }

  @Override
  protected boolean handleValidClick(double mouseX, double mouseY, int button) {
    this.configOption.setValue(!this.configOption.getValue());
    return true;
  }

  private void onConfigValueChange(boolean prev, boolean curr) {
    this.cachedText = new TranslatableText(curr ? "config.toggle.enabled" : "config.toggle.disabled");
  }
}
