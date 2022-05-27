package me.roundaround.roundalib.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.gui.control.OptionListControl;
import me.roundaround.roundalib.config.value.ListOptionValue;

public class OptionListConfigOption<T extends ListOptionValue<T>> extends ConfigOption<T, OptionListControl<T>> {
  public OptionListConfigOption(String id, String labelI18nKey, T defaultValue) {
    super(id, labelI18nKey, defaultValue);
  }

  @Override
  public T deserializeFromJson(JsonElement data) {
    return this.getValue().getFromId(data.getAsString());
  }

  @Override
  public JsonElement serializeToJson() {
    return new JsonPrimitive(this.getValue().getId());
  }

  @Override
  public OptionListControl<T> createControl(OptionRow parent, int top, int left, int height, int width) {
    return new OptionListControl<T>(this, parent, top, left, height, width);
  }
}
