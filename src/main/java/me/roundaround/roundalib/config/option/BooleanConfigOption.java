package me.roundaround.roundalib.config.option;

import java.util.Optional;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.gui.control.ToggleControl;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class BooleanConfigOption extends ConfigOption<Boolean, ToggleControl> {
  private final Text enabledLabel;
  private final Text disabledLabel;

  protected BooleanConfigOption(Builder builder) {
    super(builder);
    enabledLabel = builder.enabledLabel;
    disabledLabel = builder.disabledLabel;
  }

  @Override
  public ToggleControl createControl(OptionRow parent, int top, int left, int height, int width) {
    return new ToggleControl(this, parent, top, left, height, width, enabledLabel, disabledLabel);
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

  public static class Builder extends ConfigOption.Builder<Boolean, ToggleControl> {
    private Text enabledLabel = new TranslatableText("config.toggle.enabled");
    private Text disabledLabel = new TranslatableText("config.toggle.disabled");

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
      enabledLabel = new TranslatableText(i18nKey);
      return this;
    }

    public Builder setEnabledLabel(Text label) {
      enabledLabel = label;
      return this;
    }

    public Builder setDisabledLabel(String i18nKey) {
      disabledLabel = new TranslatableText(i18nKey);
      return this;
    }

    public Builder setDisabledLabel(Text label) {
      disabledLabel = label;
      return this;
    }

    @Override
    public Builder setComment(String i18nKey) {
      comment = Optional.of(new TranslatableText(i18nKey));
      return this;
    }

    @Override
    public Builder setComment(Text comment) {
      this.comment = Optional.of(comment);
      return this;
    }

    @Override
    public Builder setUseLabelAsCommentFallback(boolean useLabelAsCommentFallback) {
      this.useLabelAsCommentFallback = useLabelAsCommentFallback;
      return this;
    }

    @Override
    public BooleanConfigOption build() {
      return new BooleanConfigOption(this);
    }
  }
}
