package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.widget.OptionRowWidget;
import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.value.ListOptionValue;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class OptionListControl extends ButtonControl<OptionListConfigOption> {
  private Text cachedText;

  public OptionListControl(
      OptionListConfigOption configOption,
      OptionRowWidget parent,
      int top,
      int left,
      int height,
      int width) {
    super(configOption, parent, top, left, height, width);

    configOption.subscribeToValueChanges(this::onConfigValueChange);
    cachedText = new TranslatableText(configOption.getValue().getI18nKey());
  }

  @Override
  protected Text getCurrentText() {
    return cachedText;
  }

  @Override
  protected void onPress(int button) {
    ListOptionValue<?> currentValue = configOption.getValue();
    configOption.setValue(button == 0 ? currentValue.getNext() : currentValue.getPrev());
    super.onPress(button);
  }

  private void onConfigValueChange(ListOptionValue<?> prev, ListOptionValue<?> curr) {
    cachedText = new TranslatableText(curr.getI18nKey());
  }

  private Text composeLabelText() {
    return ScreenTexts.composeGenericOptionText(configOption.getLabel(), cachedText);
  }

  private Text composeUsageText() {
    return ScreenTexts.composeGenericOptionText(configOption.getLabel(),
        new TranslatableText(configOption.getValue().getNext().getI18nKey()));
  }

  @Override
  public void appendNarrations(NarrationMessageBuilder builder) {
    builder.put(NarrationPart.TITLE, composeLabelText());

    // TODO: Write own usage texts: current one only mentions going forward, and
    // only mentions using ENTER (no SPACE) to use.
    Text usageText = composeUsageText();
    if (focused) {
      builder.put(NarrationPart.USAGE, new TranslatableText("narration.cycle_button.usage.focused", usageText));
    } else if (hovered) {
      builder.put(NarrationPart.USAGE, new TranslatableText("narration.cycle_button.usage.hovered", usageText));
    }
  }
}
