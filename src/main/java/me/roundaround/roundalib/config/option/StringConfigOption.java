package me.roundaround.roundalib.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.gui.control.TextInputControl;

public class StringConfigOption extends ConfigOption<String, TextInputControl> {
  public StringConfigOption(String id, String labelI18nKey, String defaultValue) {
    super(id, labelI18nKey, defaultValue);
  }

  @Override
  public String deserializeFromJson(JsonElement data) {
    return data.getAsString();
  }

  @Override
  public JsonElement serializeToJson() {
    return new JsonPrimitive(this.getValue());
  }

  @Override
  public TextInputControl createControl(OptionRow parent, int top, int left, int height, int width) {
    return new TextInputControl(this, parent, top, left, height, width);
  }
}
