package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.gui.control.OptionListControl;
import me.roundaround.roundalib.config.value.ListOptionValue;

public class OptionListConfigOption<T extends ListOptionValue<T>> extends ConfigOption<T, OptionListControl<T>> {
  public OptionListConfigOption(String id, String labelI18nKey, T defaultValue) {
    super(id, labelI18nKey, defaultValue);
  }

  @Override
  public OptionListControl<T> createControl(OptionRow parent, int top, int left, int height, int width) {
    return new OptionListControl<T>(this, parent, top, left, height, width);
  }

  @Override
  public void deserialize(Object data) {
    setValue(getValue().getFromId((String) data));
  }

  @Override
  public Object serialize() {
    return getValue().getId();
  }
}
