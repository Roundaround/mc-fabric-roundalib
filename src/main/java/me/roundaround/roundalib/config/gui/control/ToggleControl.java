package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.widget.OptionRowWidget;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ToggleControl extends ButtonControl<BooleanConfigOption> {
  private Text cachedText;

  public ToggleControl(BooleanConfigOption configOption, OptionRowWidget parent, int top, int left, int height,
      int width) {
    super(configOption, parent, top, left, height, width);

    configOption.subscribeToValueChanges(this::onConfigValueChange);
    cachedText = configOption.getValue() ? configOption.getEnabledLabel() : configOption.getDisabledLabel();
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
    cachedText = curr ? configOption.getEnabledLabel() : configOption.getDisabledLabel();
  }

  private MutableText composeLabelText() {
    return ScreenTexts.composeGenericOptionText(configOption.getLabel(), cachedText);
  }

  private Text composeUsageText() {
    Text nextValueText = configOption.getValue() ? configOption.getDisabledLabel() : configOption.getEnabledLabel();
    return ScreenTexts.composeGenericOptionText(configOption.getLabel(), nextValueText);
  }

  @Override
  public void appendNarrations(NarrationMessageBuilder builder) {
    builder.put(NarrationPart.TITLE, composeLabelText());

    // TODO: Write own usage texts: current one only mentions using ENTER (no
    // SPACE) to use.
    Text usageText = composeUsageText();
    if (focused) {
      builder.put(NarrationPart.USAGE, new TranslatableText("narration.cycle_button.usage.focused", usageText));
    } else if (hovered) {
      builder.put(NarrationPart.USAGE, new TranslatableText("narration.cycle_button.usage.hovered", usageText));
    }
  }
}
