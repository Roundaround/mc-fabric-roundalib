package me.roundaround.roundalib.config.option;

import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class BooleanConfigOption extends ConfigOption<Boolean, BooleanConfigOption.Builder> {
  private final Text enabledLabel;
  private final Text disabledLabel;

  protected BooleanConfigOption(Builder builder) {
    super(builder);
    enabledLabel = builder.enabledLabel;
    disabledLabel = builder.disabledLabel;
  }

  protected BooleanConfigOption(BooleanConfigOption other) {
    super(other);
    enabledLabel = other.enabledLabel;
    disabledLabel = other.disabledLabel;
  }

  public Text getEnabledLabel() {
    return enabledLabel;
  }

  public Text getDisabledLabel() {
    return disabledLabel;
  }

  @Override
  public BooleanConfigOption copy() {
    return new BooleanConfigOption(this);
  }

  public static Builder builder(String id, String labelI18nKey) {
    return new Builder(id, labelI18nKey);
  }

  public static Builder builder(String id, Text label) {
    return new Builder(id, label);
  }

  public static Builder onOffBuilder(String id, String labelI18nKey) {
    return new Builder(id, labelI18nKey)
        .setEnabledLabel(ScreenTexts.ON)
        .setDisabledLabel(ScreenTexts.OFF);
  }

  public static Builder onOffBuilder(String id, Text label) {
    return new Builder(id, label)
        .setEnabledLabel(ScreenTexts.ON)
        .setDisabledLabel(ScreenTexts.OFF);
  }

  public static Builder yesNoBuilder(String id, String labelI18nKey) {
    return new Builder(id, labelI18nKey)
        .setEnabledLabel(ScreenTexts.YES)
        .setDisabledLabel(ScreenTexts.NO);
  }

  public static Builder yesNoBuilder(String id, Text label) {
    return new Builder(id, label)
        .setEnabledLabel(ScreenTexts.YES)
        .setDisabledLabel(ScreenTexts.NO);
  }

  public static class Builder extends ConfigOption.Builder<Boolean, Builder> {
    private Text enabledLabel = Text.translatable("roundalib.toggle.enabled");
    private Text disabledLabel = Text.translatable("roundalib.toggle.disabled");

    private Builder(String id, String labelI18nKey) {
      super(id, labelI18nKey, true);
    }

    private Builder(String id, Text label) {
      super(id, label, true);
    }

    public Builder setDefaultValue(Boolean defaultValue) {
      this.defaultValue = defaultValue;
      return this;
    }

    public Builder setEnabledLabel(String i18nKey) {
      enabledLabel = Text.translatable(i18nKey);
      return this;
    }

    public Builder setEnabledLabel(Text label) {
      enabledLabel = label;
      return this;
    }

    public Builder setDisabledLabel(String i18nKey) {
      disabledLabel = Text.translatable(i18nKey);
      return this;
    }

    public Builder setDisabledLabel(Text label) {
      disabledLabel = label;
      return this;
    }

    @Override
    public BooleanConfigOption build() {
      return new BooleanConfigOption(this);
    }
  }
}
