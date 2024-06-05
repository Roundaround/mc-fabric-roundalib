package me.roundaround.roundalib.config.option;

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

  protected BooleanConfigOption(BooleanConfigOption other) {
    super(other);
    this.enabledLabel = other.enabledLabel;
    this.disabledLabel = other.disabledLabel;
  }

  public Text getEnabledLabel() {
    return this.enabledLabel;
  }

  public Text getDisabledLabel() {
    return this.disabledLabel;
  }

  public Text getValueLabel() {
    return this.getValue() ? this.enabledLabel : this.disabledLabel;
  }

  public boolean toggle() {
    this.setValue(!this.getValue());
    return this.getValue();
  }

  @Override
  public BooleanConfigOption copy() {
    return new BooleanConfigOption(this);
  }

  public static Builder builder(String modId, String id, String labelI18nKey) {
    return new Builder(modId, id, labelI18nKey);
  }

  public static Builder builder(String modId, String id, Text label) {
    return new Builder(modId, id, label);
  }

  public static Builder onOffBuilder(String modId, String id, String labelI18nKey) {
    return new Builder(modId, id, labelI18nKey).setEnabledLabel(ScreenTexts.ON).setDisabledLabel(ScreenTexts.OFF);
  }

  public static Builder onOffBuilder(String modId, String id, Text label) {
    return new Builder(modId, id, label).setEnabledLabel(ScreenTexts.ON).setDisabledLabel(ScreenTexts.OFF);
  }

  public static Builder yesNoBuilder(String modId, String id, String labelI18nKey) {
    return new Builder(modId, id, labelI18nKey).setEnabledLabel(ScreenTexts.YES).setDisabledLabel(ScreenTexts.NO);
  }

  public static Builder yesNoBuilder(String modId, String id, Text label) {
    return new Builder(modId, id, label).setEnabledLabel(ScreenTexts.YES).setDisabledLabel(ScreenTexts.NO);
  }

  public static class Builder extends ConfigOption.AbstractBuilder<Boolean> {
    private Text enabledLabel = Text.translatable(this.modId + ".roundalib.toggle.enabled");
    private Text disabledLabel = Text.translatable(this.modId + ".roundalib.toggle.disabled");

    private Builder(String modId, String id, String labelI18nKey) {
      super(modId, id, labelI18nKey, true);
    }

    private Builder(String modId, String id, Text label) {
      super(modId, id, label, true);
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
