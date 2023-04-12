package me.roundaround.testmod.config;

import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.testmod.TestMod;

public class TestModConfig extends ModConfig {
  public TestModConfig() {
    super(TestMod.MOD_ID);

    for (int i = 1; i <= 20; i++) {
      registerConfigOption(BooleanConfigOption.builder(this, "testOption" + i, "testmod.testOption" + i + ".label")
          .setDefaultValue(true)
          .build());
    }
  }
}
