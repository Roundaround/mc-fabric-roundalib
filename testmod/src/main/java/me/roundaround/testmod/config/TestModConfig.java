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

    BooleanConfigOption first = registerConfigOption(
        BooleanConfigOption.builder(this, "testOption0").setGroup("group0").setDefaultValue(true).build());

    registerConfigOption(BooleanConfigOption.builder(this, "testOption1")
        .setGroup("group0")
        .setDefaultValue(true)
        .onUpdate((option) -> option.setDisabled(!first.getPendingValue()))
        .build());

    registerConfigOption(
        OptionListConfigOption.builder(this, "testOption2", Arrays.stream(Difficulty.values()).toList())
            .setGroup("group0")
            .setDefaultValue(Difficulty.getDefault())
            .build());

    registerConfigOption(StringConfigOption.builder(this, "testOption3")
        .setGroup("group0")
        .setDefaultValue("foo")
        .setMinLength(3)
        .setMaxLength(12)
        .onUpdate((option) -> option.setDisabled(!first.getPendingValue()))
        .build());

    registerConfigOption(IntConfigOption.builder(this, "testOption4")
        .setGroup("group1")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setStep(5)
        .addCustomValidator((option, value) -> value % 25 != 0)
        .onUpdate((option) -> option.setDisabled(!first.getPendingValue()))
        .build());

    registerConfigOption(IntConfigOption.builder(this, "testOption5")
        .setGroup("group1")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setStep(5)
        .setUseSlider(true)
        .onUpdate((option) -> option.setDisabled(!first.getPendingValue()))
        .build());

    registerConfigOption(FloatConfigOption.builder(this, "testOption6")
        .setGroup("group1")
        .setDefaultValue(5f)
        .setMinValue(0)
        .setMaxValue(100)
        .build());

    registerConfigOption(FloatConfigOption.builder(this, "testOption7")
        .setGroup("group1")
        .setDefaultValue(5f)
        .setMinValue(0)
        .setMaxValue(100)
        .setUseSlider(true)
        .build());

    registerConfigOption(PositionConfigOption.builder(this, "testOption8").setGroup("group2").build());

    registerConfigOption(PositionConfigOption.builder(this, "testOption9")
        .setGroup("group2")
        .setDefaultValue(new Position(50, 50))
        .build());

    registerConfigOption(IntConfigOption.builder(this, "testOption10")
        .setGroup("group2")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setStep(5)
        .setUseSlider(true)
        .build());
  }
}
