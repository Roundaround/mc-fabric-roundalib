package me.roundaround.roundalib.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.roundaround.roundalib.config.gui.control.ControlFactory;
import me.roundaround.roundalib.config.gui.control.TextInputControl;

public class StringConfigOption extends ConfigOption<String> {
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
    public ControlFactory<String> getDefaultControlFactory() {
        return TextInputControl::new;
    }
}
