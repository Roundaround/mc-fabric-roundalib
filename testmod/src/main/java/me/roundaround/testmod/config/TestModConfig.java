package me.roundaround.testmod.config;

import com.electronwill.nightconfig.core.Config;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.*;
import me.roundaround.roundalib.config.value.Difficulty;
import me.roundaround.roundalib.config.value.Position;
import me.roundaround.testmod.TestMod;

import java.util.Arrays;

public class TestModConfig extends ModConfig {
  public final BooleanConfigOption first;
  public final BooleanConfigOption second;
  public final OptionListConfigOption<Difficulty> third;
  public final StringConfigOption fourth;
  public final IntConfigOption fifth;
  public final IntConfigOption sixth;
  public final IntConfigOption seventh;
  public final FloatConfigOption eighth;
  public final FloatConfigOption ninth;
  public final PositionConfigOption tenth;
  public final PositionConfigOption eleventh;
  public final IntConfigOption twelfth;
  public final BooleanConfigOption thirteenth;

  public TestModConfig() {
    super(TestMod.MOD_ID, options(TestMod.MOD_ID).setConfigVersion(2));

    this.first = this.registerConfigOption(
        BooleanConfigOption.builder(this, "testOption0").setGroup("group0").setDefaultValue(true).build());

    this.second = this.registerConfigOption(BooleanConfigOption.builder(this, "testOption1")
        .setGroup("group0")
        .setDefaultValue(true)
        .onUpdate((option) -> option.setDisabled(!this.first.getPendingValue()))
        .build());

    this.third = this.registerConfigOption(
        OptionListConfigOption.builder(this, "testOption2", Arrays.stream(Difficulty.values()).toList())
            .setGroup("group0")
            .setDefaultValue(Difficulty.getDefault())
            .build());

    this.fourth = this.registerConfigOption(StringConfigOption.builder(this, "testOption3")
        .setGroup("group0")
        .setDefaultValue("foo")
        .setMinLength(3)
        .setMaxLength(12)
        .onUpdate((option) -> option.setDisabled(!this.first.getPendingValue()))
        .build());

    this.fifth = this.registerConfigOption(IntConfigOption.builder(this, "testOption4")
        .setGroup("group1")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setStep(5)
        .addValidator((value, option) -> value % 25 != 0)
        .onUpdate((option) -> option.setDisabled(!this.first.getPendingValue()))
        .build());

    this.sixth = this.registerConfigOption(IntConfigOption.builder(this, "testOption5")
        .setGroup("group1")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setStep(null)
        .addValidator((value, option) -> value % 25 != 0)
        .onUpdate((option) -> option.setDisabled(!this.first.getPendingValue()))
        .build());

    this.seventh = this.registerConfigOption(IntConfigOption.builder(this, "testOption6")
        .setGroup("group1")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setStep(5)
        .setUseSlider(true)
        .onUpdate((option) -> option.setDisabled(!this.first.getPendingValue()))
        .build());

    this.eighth = this.registerConfigOption(FloatConfigOption.builder(this, "testOption7")
        .setGroup("group1")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .build());

    this.ninth = this.registerConfigOption(FloatConfigOption.builder(this, "testOption8")
        .setGroup("group1")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setUseSlider(true)
        .build());

    this.tenth = this.registerConfigOption(
        PositionConfigOption.builder(this, "testOption9").setGroup("group2").build());

    this.eleventh = this.registerConfigOption(PositionConfigOption.builder(this, "testOption10")
        .setGroup("group2")
        .setDefaultValue(new Position(50, 50))
        .build());

    this.twelfth = this.registerConfigOption(IntConfigOption.builder(this, "testOption11")
        .setGroup("group2")
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setStep(5)
        .setUseSlider(true)
        .build());

    this.thirteenth = this.registerConfigOption(
        BooleanConfigOption.builder(this, "testOption12").setGroup("group3").setDefaultValue(true).build());
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
