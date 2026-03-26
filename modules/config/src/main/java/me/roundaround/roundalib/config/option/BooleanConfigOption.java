package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ConfigPath;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class BooleanConfigOption extends ConfigOption<Boolean> {
  private final Component enabledLabel;
  private final Component disabledLabel;

  protected BooleanConfigOption(Builder builder) {
    super(builder);
    this.enabledLabel = builder.enabledLabel;
    this.disabledLabel = builder.disabledLabel;
  }

  public Component getEnabledLabel() {
    if (this.enabledLabel == null) {
      return Component.translatable(this.getModId() + ".roundalib.toggle.enabled");
    }
    return this.enabledLabel;
  }

  public Component getDisabledLabel() {
    if (this.disabledLabel == null) {
      return Component.translatable(this.getModId() + ".roundalib.toggle.disabled");
    }
    return this.disabledLabel;
  }

  public Component getValueLabel(boolean value) {
    return value ? this.getEnabledLabel() : this.getDisabledLabel();
  }

  public Component getValueLabel() {
    return this.getPendingValue() ? this.getEnabledLabel() : this.getDisabledLabel();
  }

  public void toggle() {
    this.setValue(!this.getPendingValue());
  }

  public static Builder builder(ConfigPath path) {
    return new Builder(path);
  }

  public static Builder onOffBuilder(ConfigPath path) {
    return new Builder(path).setEnabledLabel(CommonComponents.OPTION_ON).setDisabledLabel(CommonComponents.OPTION_OFF);
  }

  public static Builder yesNoBuilder(ConfigPath path) {
    return new Builder(path).setEnabledLabel(CommonComponents.GUI_YES).setDisabledLabel(CommonComponents.GUI_NO);
  }

  public static class Builder extends AbstractBuilder<Boolean, BooleanConfigOption, Builder> {
    private Component enabledLabel = null;
    private Component disabledLabel = null;

    private Builder(ConfigPath path) {
      super(path);
    }

    public Builder setEnabledLabel(String i18nKey) {
      this.enabledLabel = Component.translatable(i18nKey);
      return this;
    }

    public Builder setEnabledLabel(Component label) {
      this.enabledLabel = label;
      return this;
    }

    public Builder setDisabledLabel(String i18nKey) {
      this.disabledLabel = Component.translatable(i18nKey);
      return this;
    }

    public Builder setDisabledLabel(Component label) {
      this.disabledLabel = label;
      return this;
    }

    @Override
    protected BooleanConfigOption buildInternal() {
      return new BooleanConfigOption(this);
    }
  }
}
