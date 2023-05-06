package me.roundaround.testmod.config;

import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.*;
import me.roundaround.roundalib.config.value.Difficulty;
import me.roundaround.roundalib.config.value.Position;
import me.roundaround.testmod.TestMod;

import java.util.Arrays;

public class TestModConfig extends ModConfig {
  public TestModConfig() {
    super(TestMod.MOD_ID);

    BooleanConfigOption first = registerConfigOption(BooleanConfigOption.builder(this,
        "testOption1",
        "testmod.testOption1.label").setDefaultValue(true).build());

    registerConfigOption(OptionListConfigOption.defaultInstance(this,
        "testOption2",
        "testmod.testOption2.label",
        Arrays.stream(Difficulty.values()).toList(),
        Difficulty.NORMAL));

    registerConfigOption(StringConfigOption.builder(this,
            "testOption3",
            "testmod.testOption3.label")
        .setDefaultValue("foo")
        .setMinLength(3)
        .setMaxLength(12)
        .setDisabledSupplier(() -> !first.getValue())
        .build());

    registerConfigOption(IntConfigOption.builder(this, "testOption4", "testmod.testOption4.label")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setStep(5)
        .addCustomValidator((option, value) -> value % 25 != 0)
        .setDisabledSupplier(() -> !first.getValue())
        .build());

    registerConfigOption(IntConfigOption.builder(this, "testOption5", "testmod.testOption5.label")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setStep(5)
        .setUseSlider(true)
        .build());

    registerConfigOption(FloatConfigOption.builder(this, "testOption6", "testmod.testOption6.label")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .build());

    registerConfigOption(FloatConfigOption.builder(this, "testOption7", "testmod.testOption7.label")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setUseSlider(true)
        .build());

    registerConfigOption(PositionConfigOption.builder(this,
        "testOption8",
        "testmod.testOption8.label",
        new Position(0, 0)).build());

    registerConfigOption(PositionConfigOption.builder(this,
        "testOption9",
        "testmod.testOption9.label",
        new Position(0, 0)).build());

    registerConfigOption(IntConfigOption.builder(this, "testOption10", "testmod.testOption10.label")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setStep(5)
        .setUseSlider(true)
        .build());
  }
}
