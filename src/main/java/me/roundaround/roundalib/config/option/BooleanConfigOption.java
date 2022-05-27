package me.roundaround.roundalib.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.gui.control.ToggleControl;

public class BooleanConfigOption extends ConfigOption<Boolean, ToggleControl> {
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
  public ToggleControl createControl(OptionRow parent, int top, int left, int height, int width) {
    return new ToggleControl(this, parent, top, left, height, width);
  }
}
