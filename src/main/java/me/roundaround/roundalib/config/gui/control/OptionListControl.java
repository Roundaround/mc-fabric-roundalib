package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.value.ListOptionValue;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class OptionListControl<T extends ListOptionValue<T>> extends ButtonControl<T> {
  private Text cachedText;

  public OptionListControl(
      OptionRow parent, ConfigOption<T> configOption, int top, int left, int height, int width) {
    super(parent, configOption, top, left, height, width);
    this.configOption.subscribeToValueChanges(this::onConfigValueChange);
    this.cachedText = new TranslatableText(this.configOption.getValue().getI18nKey());
  }

  @Override
  protected Text getCurrentText() {
    return this.cachedText;
  }

  @Override
  protected boolean handleValidClick(double mouseX, double mouseY, int button) {
    T currentValue = this.configOption.getValue();
    this.configOption.setValue(button == 0 ? currentValue.getNext() : currentValue.getPrev());
    return true;
  }

  private void onConfigValueChange(T prev, T curr) {
    this.cachedText = new TranslatableText(curr.getI18nKey());
  }
}
