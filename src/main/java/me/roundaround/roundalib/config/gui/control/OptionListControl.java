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

    this.configOption.subscribeToValueChanges(this::onConfigValueChange);
    this.cachedText = Text.translatable(this.configOption.getValue().getI18nKey(this.config));
  }

  @Override
  protected Text getCurrentText() {
    return cachedText;
  }

  @Override
  protected void onPress(int button) {
    if (button == 0) {
      this.configOption.setNext();
    } else {
      this.configOption.setPrev();
    }
    super.onPress(button);
  }

  private void onConfigValueChange(S prev, S curr) {
    this.cachedText = Text.translatable(curr.getI18nKey(this.config));
  }

  private Text composeLabelText() {
    return ScreenTexts.composeGenericOptionText(this.configOption.getLabel(), this.cachedText);
  }

  private Text composeUsageText() {
    return ScreenTexts.composeGenericOptionText(this.configOption.getLabel(),
        Text.translatable(this.configOption.getValue().getNext().getI18nKey(this.config)));
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
