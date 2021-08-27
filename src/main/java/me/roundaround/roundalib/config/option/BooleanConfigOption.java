package me.roundaround.roundalib.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.roundaround.roundalib.config.gui.control.ControlFactory;
import me.roundaround.roundalib.config.gui.control.ToggleControl;

public class BooleanConfigOption extends ConfigOption<Boolean> {
  public BooleanConfigOption(String id, String labelI18nKey, Boolean defaultValue) {
    super(id, labelI18nKey, defaultValue);
  }

  @Override
  public Boolean deserializeFromJson(JsonElement data) {
    return data.getAsBoolean();
  }

  @Override
  public JsonElement serializeToJson() {
    return new JsonPrimitive(this.getValue());
  }

  @Override
  public ControlFactory<Boolean> getDefaultControlFactory() {
    return ToggleControl::new;
  }
}
