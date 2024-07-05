package me.roundaround.testmod.config;

import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.WorldScopedConfig;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.testmod.TestMod;

public class PerWorldTestModConfig extends WorldScopedConfig {
  private static PerWorldTestModConfig instance = null;

  public static PerWorldTestModConfig getInstance() {
    if (instance == null) {
      instance = new PerWorldTestModConfig();
    }
    return instance;
  }

  public BooleanConfigOption first;
  public BooleanConfigOption second;
  public BooleanConfigOption third;
  public BooleanConfigOption fourth;
  public BooleanConfigOption fifth;
  public BooleanConfigOption sixth;

  private PerWorldTestModConfig() {
    super(TestMod.MOD_ID, "pw");
  }

  @Override
  protected void registerOptions() {
    this.first = this.register(
        BooleanConfigOption.builder(ConfigPath.of("pwTestOption0")).setDefaultValue(true).build());

    this.second = this.register(BooleanConfigOption.builder(ConfigPath.of("pwTestOption1"))
        .setDefaultValue(true)
        .onUpdate((option) -> option.setDisabled(!this.first.getPendingValue()))
        .build());

    this.third = this.register(BooleanConfigOption.builder(ConfigPath.of("pwTestOption2"))
        .setDefaultValue(true)
        .onUpdate((option) -> option.setDisabled(!this.first.getPendingValue()))
        .build(), GuiContext.NOT_IN_GAME);

    this.fourth = this.register(BooleanConfigOption.builder(ConfigPath.of("pwTestOption3"))
        .setDefaultValue(true)
        .onUpdate((option) -> option.setDisabled(!this.first.getPendingValue()))
        .build(), GuiContext.INTEGRATED_SERVER);

    this.fifth = this.register(BooleanConfigOption.builder(ConfigPath.of("pwTestOption4"))
        .setDefaultValue(true)
        .onUpdate((option) -> option.setDisabled(!this.first.getPendingValue()))
        .build(), GuiContext.DEDICATED_SERVER);

    this.sixth = this.register(BooleanConfigOption.builder(ConfigPath.of("pwTestOption5"))
        .setDefaultValue(true)
        .onUpdate((option) -> option.setDisabled(!this.first.getPendingValue()))
        .build(), GuiContext.INTEGRATED_SERVER, GuiContext.DEDICATED_SERVER);
  }
}
