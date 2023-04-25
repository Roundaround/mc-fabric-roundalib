package me.roundaround.testmod.config;

import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.*;
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

    registerConfigOption(StringConfigOption.builder(this,
            "testOption22",
            "testmod.testOption22.label")
        .setDefaultValue("foo")
        .setMinLength(3)
        .setMaxLength(12)
        .build());

    registerConfigOption(IntConfigOption.builder(this,
            "testOption23",
            "testmod.testOption23.label")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setStep(5)
        .addCustomValidator((option, value) -> value % 25 != 0)
        .build());

    registerConfigOption(IntConfigOption.builder(this,
            "testOption24",
            "testmod.testOption24.label")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setStep(5)
        .setUseSlider(true)
        .build());

    registerConfigOption(FloatConfigOption.builder(this,
            "testOption25",
            "testmod.testOption25.label")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .build());
  }
}
