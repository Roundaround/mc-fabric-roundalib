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

    BooleanConfigOption first = registerConfigOption("group0",
        BooleanConfigOption.builder(this, "testOption0", "testmod.group0.testOption0.label")
            .setDefaultValue(true)
            .build()
    );

    registerConfigOption(
        "group0", BooleanConfigOption.builder(this, "testOption1", "testmod.group0.testOption1.label")
            .setDefaultValue(true)
            .dependsOn(first)
            .setDisabledSupplier(() -> !first.getValue())
            .build());

    registerConfigOption("group0",
        OptionListConfigOption.defaultInstance(this, "testOption2", "testmod.group0.testOption2.label",
            Arrays.stream(Difficulty.values()).toList(), Difficulty.NORMAL
        )
    );

    registerConfigOption(
        "group0", StringConfigOption.builder(this, "testOption3", "testmod.group0.testOption3.label")
            .setDefaultValue("foo")
            .setMinLength(3)
            .setMaxLength(12)
            .dependsOn(first)
            .setDisabledSupplier(() -> !first.getValue())
            .build());

    registerConfigOption(
        "group1", IntConfigOption.builder(this, "testOption4", "testmod.group1.testOption4.label")
            .setDefaultValue(5)
            .setMinValue(0)
            .setMaxValue(100)
            .setStep(5)
            .addCustomValidator((option, value) -> value % 25 != 0)
            .dependsOn(first)
            .setDisabledSupplier(() -> !first.getValue())
            .build());

    registerConfigOption(
        "group1", IntConfigOption.builder(this, "testOption5", "testmod.group1.testOption5.label")
            .setDefaultValue(5)
            .setMinValue(0)
            .setMaxValue(100)
            .setStep(5)
            .setUseSlider(true)
            .dependsOn(first)
            .setDisabledSupplier(() -> !first.getValue())
            .build());

    registerConfigOption(
        "group1", FloatConfigOption.builder(this, "testOption6", "testmod.group1.testOption6.label")
            .setDefaultValue(5)
            .setMinValue(0)
            .setMaxValue(100)
            .build());

    registerConfigOption(
        "group1", FloatConfigOption.builder(this, "testOption7", "testmod.group1.testOption7.label")
            .setDefaultValue(5)
            .setMinValue(0)
            .setMaxValue(100)
            .setUseSlider(true)
            .build());

    registerConfigOption("group2",
        PositionConfigOption.builder(this, "testOption8", "testmod.group2.testOption8.label", new Position(0, 0))
            .build()
    );

    registerConfigOption("group2",
        PositionConfigOption.builder(this, "testOption9", "testmod.group2.testOption9.label", new Position(0, 0))
            .build()
    );

    registerConfigOption(
        "group2", IntConfigOption.builder(this, "testOption10", "testmod.group2.testOption10.label")
            .setDefaultValue(5)
            .setMinValue(0)
            .setMaxValue(100)
            .setStep(5)
            .setUseSlider(true)
            .build());
  }
}
