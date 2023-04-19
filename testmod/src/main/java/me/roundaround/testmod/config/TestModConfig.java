package me.roundaround.testmod.config;

import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.value.Difficulty;
import me.roundaround.testmod.TestMod;

import java.util.Arrays;

public class TestModConfig extends ModConfig {
  public TestModConfig() {
    super(TestMod.MOD_ID);

    for (int i = 1; i <= 20; i++) {
      registerConfigOption(BooleanConfigOption.builder(this,
          "testOption" + i,
          "testmod.testOption" + i + ".label").setDefaultValue(true).build());
    }

    registerConfigOption(OptionListConfigOption.defaultInstance(this,
        "testOption21",
        "testmod.testOption21.label",
        Arrays.stream(Difficulty.values()).toList(),
        Difficulty.NORMAL));
  }
}
