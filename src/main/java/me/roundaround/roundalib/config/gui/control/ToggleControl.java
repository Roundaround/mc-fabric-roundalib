package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ToggleControl extends ButtonControl<BooleanConfigOption> {
  private final String enabledI18nKey;
  private final String disabledI18nKey;

  private Text cachedText;

  public ToggleControl(BooleanConfigOption configOption, OptionRow parent, int top, int left, int height, int width) {
    this(configOption, parent, top, left, height, width, "config.toggle.enabled", "config.toggle.disabled");
  }

  public ToggleControl(BooleanConfigOption configOption, OptionRow parent, int top, int left, int height, int width,
      String enabledI18nKey, String disabledI18nKey) {
    super(configOption, parent, top, left, height, width);
    this.enabledI18nKey = enabledI18nKey;
    this.disabledI18nKey = disabledI18nKey;
  }

  @Override
  public void init() {
    configOption.subscribeToValueChanges(this::onConfigValueChange);
    cachedText = new TranslatableText(configOption.getValue() ? enabledI18nKey : disabledI18nKey);
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
    cachedText = new TranslatableText(curr ? enabledI18nKey : disabledI18nKey);
  }
}
