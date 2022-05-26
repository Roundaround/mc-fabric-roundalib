package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.value.ListOptionValue;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class OptionListControl<T extends ListOptionValue<T>> extends ButtonControl<T> {
  private Text cachedText;

  public OptionListControl(
      OptionRow parent, int top, int left, int height, int width) {
    super(parent, top, left, height, width);
  }

  @Override
  public void init() {
    configOption.subscribeToValueChanges(this::onConfigValueChange);
    cachedText = new TranslatableText(configOption.getValue().getI18nKey());
  }

  @Override
  protected Text getCurrentText() {
    return cachedText;
  }

  @Override
  protected void onPress(int button) {
    T currentValue = configOption.getValue();
    configOption.setValue(button == 0 ? currentValue.getNext() : currentValue.getPrev());
    super.onPress(button);
  }

  private void onConfigValueChange(T prev, T curr) {
    cachedText = new TranslatableText(curr.getI18nKey());
  }
}
