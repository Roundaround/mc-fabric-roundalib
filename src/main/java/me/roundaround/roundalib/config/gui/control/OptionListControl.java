package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.widget.OptionRowWidget;
import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.value.ListOptionValue;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class OptionListControl<S extends ListOptionValue<S>, T extends OptionListConfigOption<S>>
    extends ButtonControl<T> {
  private Text cachedText;

  public OptionListControl(
      T configOption,
      OptionRowWidget parent,
      int top,
      int left,
      int height,
      int width) {
    super(configOption, parent, top, left, height, width);

    configOption.subscribeToValueChanges(this::onConfigValueChange);
    cachedText = Text.translatable(configOption.getValue().getI18nKey());
  }

  @Override
  protected Text getCurrentText() {
    return cachedText;
  }

  @Override
  protected void onPress(int button) {
    if (button == 0) {
      configOption.setNext();
    } else {
      configOption.setPrev();
    }
    super.onPress(button);
  }

  private void onConfigValueChange(S prev, S curr) {
    cachedText = Text.translatable(curr.getI18nKey());
  }

  private Text composeLabelText() {
    return ScreenTexts.composeGenericOptionText(configOption.getLabel(), cachedText);
  }

  private Text composeUsageText() {
    return ScreenTexts.composeGenericOptionText(configOption.getLabel(),
        Text.translatable(configOption.getValue().getNext().getI18nKey()));
  }

  @Override
  public void appendNarrations(NarrationMessageBuilder builder) {
    builder.put(NarrationPart.TITLE, composeLabelText());

    Text usageText = composeUsageText();
    if (focused) {
      builder.put(NarrationPart.USAGE, Text.translatable("narration.cycle_button.usage.focused", usageText));
    } else if (hovered) {
      builder.put(NarrationPart.USAGE, Text.translatable("narration.cycle_button.usage.hovered", usageText));
    }
  }
}
