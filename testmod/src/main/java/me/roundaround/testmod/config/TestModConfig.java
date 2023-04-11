package me.roundaround.testmod.config;

import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.testmod.TestMod;

public class TestModConfig extends ModConfig {
  public final BooleanConfigOption TEST_OPTION;

  public TestModConfig() {
    super(TestMod.MOD_ID);

    this.TEST_OPTION =
        registerConfigOption(BooleanConfigOption.builder(this, "test_option", "test_option")
            .setDefaultValue(true)
            .build());
  }
}
