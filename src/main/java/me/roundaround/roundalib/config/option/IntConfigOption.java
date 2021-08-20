package me.roundaround.roundalib.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class IntConfigOption extends ConfigOption<Integer> {
    public IntConfigOption(String id, String labelI18nKey, Integer defaultValue) {
        super(id, labelI18nKey, defaultValue);
    }

    @Override
    public Integer deserializeFromJson(JsonElement data) {
        return data.getAsInt();
    }

    @Override
    public JsonElement serializeToJson() {
        return new JsonPrimitive(this.getValue());
    }
}
