package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.Config;
import me.roundaround.roundalib.config.ConfigPath;
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

  public static Builder builder(Config config, ConfigPath path) {
    return new Builder(config, path);
  }

  public static Builder onOffBuilder(Config config, ConfigPath path) {
    return new Builder(config, path).setEnabledLabel(ScreenTexts.ON).setDisabledLabel(ScreenTexts.OFF);
  }

  public static Builder yesNoBuilder(Config config, ConfigPath path) {
    return new Builder(config, path).setEnabledLabel(ScreenTexts.YES).setDisabledLabel(ScreenTexts.NO);
  }

  public static class Builder extends ConfigOption.AbstractBuilder<Boolean, BooleanConfigOption, Builder> {
    private Text enabledLabel = Text.translatable(this.config.getModId() + ".roundalib.toggle.enabled");
    private Text disabledLabel = Text.translatable(this.config.getModId() + ".roundalib.toggle.disabled");

    private Builder(Config config, ConfigPath path) {
      super(config, path);
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
    protected BooleanConfigOption buildInternal() {
      return new BooleanConfigOption(this);
    }
  }
}
