package me.roundaround.testmod.config;

import com.electronwill.nightconfig.core.Config;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.*;
import me.roundaround.roundalib.config.value.Difficulty;
import me.roundaround.roundalib.config.value.Position;
import me.roundaround.testmod.TestMod;

import java.util.Arrays;

public class TestModConfig extends ModConfig {
  public final BooleanConfigOption FIRST;
  public final BooleanConfigOption SECOND;
  public final OptionListConfigOption<Difficulty> THIRD;
  public final StringConfigOption FOURTH;
  public final IntConfigOption FIFTH;
  public final IntConfigOption SIXTH;
  public final IntConfigOption SEVENTH;
  public final FloatConfigOption EIGHTH;
  public final FloatConfigOption NINTH;
  public final PositionConfigOption TENTH;
  public final PositionConfigOption ELEVENTH;
  public final IntConfigOption TWELFTH;

  public TestModConfig() {
    super(TestMod.MOD_ID, options(TestMod.MOD_ID).setConfigVersion(2));

    this.FIRST = this.registerConfigOption(
        BooleanConfigOption.builder(this, "testOption0").setGroup("group0").setDefaultValue(true).build());

    this.SECOND = this.registerConfigOption(BooleanConfigOption.builder(this, "testOption1")
        .setGroup("group0")
        .setDefaultValue(true)
        .onUpdate((option) -> option.setDisabled(!this.FIRST.getPendingValue()))
        .build());

    this.THIRD = this.registerConfigOption(
        OptionListConfigOption.builder(this, "testOption2", Arrays.stream(Difficulty.values()).toList())
            .setGroup("group0")
            .setDefaultValue(Difficulty.getDefault())
            .build());

    this.FOURTH = this.registerConfigOption(StringConfigOption.builder(this, "testOption3")
        .setGroup("group0")
        .setDefaultValue("foo")
        .setMinLength(3)
        .setMaxLength(12)
        .onUpdate((option) -> option.setDisabled(!this.FIRST.getPendingValue()))
        .build());

    this.FIFTH = this.registerConfigOption(IntConfigOption.builder(this, "testOption4")
        .setGroup("group1")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setStep(5)
        .addValidator((value, option) -> value % 25 != 0)
        .onUpdate((option) -> option.setDisabled(!this.FIRST.getPendingValue()))
        .build());

    this.SIXTH = this.registerConfigOption(IntConfigOption.builder(this, "testOption5")
        .setGroup("group1")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setStep(null)
        .addValidator((value, option) -> value % 25 != 0)
        .onUpdate((option) -> option.setDisabled(!this.FIRST.getPendingValue()))
        .build());

    this.SEVENTH = this.registerConfigOption(IntConfigOption.builder(this, "testOption6")
        .setGroup("group1")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setStep(5)
        .setUseSlider(true)
        .onUpdate((option) -> option.setDisabled(!this.FIRST.getPendingValue()))
        .build());

    this.EIGHTH = this.registerConfigOption(FloatConfigOption.builder(this, "testOption7")
        .setGroup("group1")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .build());

    this.NINTH = this.registerConfigOption(FloatConfigOption.builder(this, "testOption8")
        .setGroup("group1")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setUseSlider(true)
        .build());

    this.TENTH = this.registerConfigOption(
        PositionConfigOption.builder(this, "testOption9").setGroup("group2").build());

    this.ELEVENTH = this.registerConfigOption(PositionConfigOption.builder(this, "testOption10")
        .setGroup("group2")
        .setDefaultValue(new Position(50, 50))
        .build());

    this.TWELFTH = this.registerConfigOption(IntConfigOption.builder(this, "testOption11")
        .setGroup("group2")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setStep(5)
        .setUseSlider(true)
        .build());
  }

  @Override
  protected boolean updateConfigVersion(int version, Config config) {
    if (version == 1) {
      // Added a new group1.testOption5 so everything after needs shifting.
      // testOption8 is now also part of group1 rather than group2

      config.set(this.getPath(2, 11), config.get(this.getPath(2, 10)));
      config.set(this.getPath(2, 10), config.get(this.getPath(2, 9)));
      config.set(this.getPath(2, 9), config.get(this.getPath(2, 8)));
      config.remove(this.getPath(2, 8));
      config.set(this.getPath(1, 8), config.get(this.getPath(1, 7)));
      config.set(this.getPath(1, 7), config.get(this.getPath(1, 6)));
      config.set(this.getPath(1, 6), config.get(this.getPath(1, 5)));
      config.remove(this.getPath(1, 5));

      return true;
    }

    return false;
  }

  private String getPath(int groupNum, int idNum) {
    return this.getPath("group" + groupNum, "testOption" + idNum);
  }
}
