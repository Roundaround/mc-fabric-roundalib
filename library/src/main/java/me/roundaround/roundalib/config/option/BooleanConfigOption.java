package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ModConfig;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class BooleanConfigOption extends ConfigOption<Boolean> {
  private final Text enabledLabel;
  private final Text disabledLabel;

  protected BooleanConfigOption(Builder builder) {
    super(builder);
    this.enabledLabel = builder.enabledLabel;
    this.disabledLabel = builder.disabledLabel;
  }

  public Text getEnabledLabel() {
    return this.enabledLabel;
  }

  public Text getDisabledLabel() {
    return this.disabledLabel;
  }

  public Text getValueLabel() {
    return this.getPendingValue() ? this.getEnabledLabel() : this.getDisabledLabel();
  }

  public void toggle() {
    this.setValue(!this.getPendingValue());
  }

  public static Builder builder(ModConfig modConfig, String id, String labelI18nKey) {
    return new Builder(modConfig, id, labelI18nKey);
  }

  public static Builder builder(ModConfig modConfig, String id, Text label) {
    return new Builder(modConfig, id, label);
  }

  public static Builder onOffBuilder(ModConfig modConfig, String id, String labelI18nKey) {
    return new Builder(modConfig, id, labelI18nKey).setEnabledLabel(ScreenTexts.ON).setDisabledLabel(ScreenTexts.OFF);
  }

  public static Builder onOffBuilder(ModConfig modConfig, String id, Text label) {
    return new Builder(modConfig, id, label).setEnabledLabel(ScreenTexts.ON).setDisabledLabel(ScreenTexts.OFF);
  }

  public static Builder yesNoBuilder(ModConfig modConfig, String id, String labelI18nKey) {
    return new Builder(modConfig, id, labelI18nKey).setEnabledLabel(ScreenTexts.YES).setDisabledLabel(ScreenTexts.NO);
  }

  public static Builder yesNoBuilder(ModConfig modConfig, String id, Text label) {
    return new Builder(modConfig, id, label).setEnabledLabel(ScreenTexts.YES).setDisabledLabel(ScreenTexts.NO);
  }

  public static class Builder extends ConfigOption.AbstractBuilder<Boolean> {
    private Text enabledLabel = Text.translatable(this.modConfig.getModId() + ".roundalib.toggle.enabled");
    private Text disabledLabel = Text.translatable(this.modConfig.getModId() + ".roundalib.toggle.disabled");

    private Builder(ModConfig modConfig, String id, String labelI18nKey) {
      super(modConfig, id, labelI18nKey, true);
    }

    private Builder(ModConfig modConfig, String id, Text label) {
      super(modConfig, id, label, true);
    }

    public Builder setDefaultValue(Boolean defaultValue) {
      this.defaultValue = defaultValue;
      return this;
    }

    public Builder setEnabledLabel(String i18nKey) {
      this.enabledLabel = Text.translatable(i18nKey);
      return this;
    }

    public Builder setEnabledLabel(Text label) {
      this.enabledLabel = label;
      return this;
    }

    public Builder setDisabledLabel(String i18nKey) {
      this.disabledLabel = Text.translatable(i18nKey);
      return this;
    }

    public Builder setDisabledLabel(Text label) {
      this.disabledLabel = label;
      return this;
    }

    @Override
    public BooleanConfigOption build() {
      return new BooleanConfigOption(this);
    }
  }
}
