package me.roundaround.roundalib.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.gui.control.ToggleControl;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class BooleanConfigOption extends ConfigOption<Boolean, ToggleControl> {
  private final Text enabledLabel;
  private final Text disabledLabel;

  // TODO: Make this a lot more user friendly. Use builder pattern, make
  // shortcuts for prebuilt variants (enabled/disabled, on/off, yes/no, etc)

  public BooleanConfigOption(String id, String labelI18nKey, Boolean defaultValue) {
    this(id, labelI18nKey, defaultValue, "config.toggle.enabled", "config.toggle.disabled");
  }

  public BooleanConfigOption(String id, String labelI18nKey, Boolean defaultValue, String enabledI18nKey,
      String disabledI18nKey) {
    this(id, labelI18nKey, defaultValue, new TranslatableText(enabledI18nKey), new TranslatableText(disabledI18nKey));
  }

  public BooleanConfigOption(String id, String labelI18nKey, Boolean defaultValue, Text enabledLabel,
      Text disabledLabel) {
    super(id, labelI18nKey, defaultValue);
    this.enabledLabel = enabledLabel;
    this.disabledLabel = disabledLabel;
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
    return new ToggleControl(this, parent, top, left, height, width, enabledLabel, disabledLabel);
  }
}
