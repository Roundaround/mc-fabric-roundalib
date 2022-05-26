package me.roundaround.roundalib.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import me.roundaround.roundalib.config.gui.control.OptionListControl;
import me.roundaround.roundalib.config.value.ListOptionValue;

public class OptionListConfigOption<T extends ListOptionValue<T>> extends ConfigOption<T> {
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
  public ControlFactory<T> getControlFactory() {
    return OptionListControl::new;
  }
}
