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

  public final BooleanConfigOption first;
  public final BooleanConfigOption second;

  private PerWorldTestModConfig() {
    super(TestMod.MOD_ID, "pw");

    this.first = this.register(
        BooleanConfigOption.builder(ConfigPath.of("pwTestOption0")).setDefaultValue(true).build());

    this.second = this.register(BooleanConfigOption.builder(ConfigPath.of("pwTestOption1"))
        .setDefaultValue(true)
        .onUpdate((option) -> option.setDisabled(!this.first.getPendingValue()))
        .build());
  }
}
