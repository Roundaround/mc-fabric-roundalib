package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ToggleControl extends ButtonControl<BooleanConfigOption> {
  private final Text enabledLabel;
  private final Text disabledLabel;

  private Text cachedText;

  public ToggleControl(BooleanConfigOption configOption, OptionRow parent, int top, int left, int height, int width) {
    this(configOption, parent, top, left, height, width, "config.toggle.enabled", "config.toggle.disabled");
  }

  public ToggleControl(BooleanConfigOption configOption, OptionRow parent, int top, int left, int height, int width,
      String enabledI18nKey, String disabledI18nKey) {
    this(configOption, parent, top, left, height, width, new TranslatableText(enabledI18nKey),
        new TranslatableText(disabledI18nKey));
  }

  public ToggleControl(BooleanConfigOption configOption, OptionRow parent, int top, int left, int height, int width,
      Text enabledLabel, Text disabledLabel) {
    super(configOption, parent, top, left, height, width);
    this.enabledLabel = enabledLabel;
    this.disabledLabel = disabledLabel;
  }

  @Override
  public void init() {
    configOption.subscribeToValueChanges(this::onConfigValueChange);
    cachedText = configOption.getValue() ? enabledLabel : disabledLabel;
  }

  @Override
  protected Text getCurrentText() {
    return cachedText;
  }

  @Override
  protected void onPress(int button) {
    configOption.setValue(!configOption.getValue());
    super.onPress(button);
  }

  private void onConfigValueChange(boolean prev, boolean curr) {
    cachedText = curr ? enabledLabel : disabledLabel;
  }
}
