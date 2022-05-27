package me.roundaround.roundalib.config;

import com.google.common.collect.ImmutableList;

import me.roundaround.roundalib.RoundaLibMod;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.option.IntConfigOption;
import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.option.StringConfigOption;
import me.roundaround.roundalib.config.value.GuiAlignment;

public class RoundaLibConfig extends ModConfig {
  public static final OptionListConfigOption<GuiAlignment> GUI_ALIGNMENT = new OptionListConfigOption<>("guiAlignment",
      "config.gui_alignment", GuiAlignment.TOP_LEFT);
  public static final IntConfigOption SOME_INTEGER = new IntConfigOption("someInteger", "config.some_integer", 5);
  public static final IntConfigOption STEP_BY_TWO = new IntConfigOption("stepByTwo", "config.step_by_two", 10,
      IntConfigOption.Options.builder().setStep(2).build());
  public static final IntConfigOption WITHIN_ONE_HUNDRED = new IntConfigOption("withinOneHundred",
      "config.within_one_hundred", 10,
      IntConfigOption.Options.builder().setMinValue(-100).setMaxValue(100).build());
  public static final IntConfigOption WITHIN_ONE_HUNDRED_BIG_STEP = new IntConfigOption("withinOneHundredBigStep",
      "config.within_one_hundred_big_step", 10,
      IntConfigOption.Options.builder().setMinValue(-100).setMaxValue(100).setStep(33).build());
  public static final StringConfigOption MY_NAME = new StringConfigOption("myName", "config.my_name", "Roundalib");
  public static final BooleanConfigOption TEST_CONFIG_1 = new BooleanConfigOption("testConfig1", "config.test_1", true);
  public static final BooleanConfigOption TEST_CONFIG_2 = new BooleanConfigOption("testConfig2", "config.test_2", true);
  public static final BooleanConfigOption TEST_CONFIG_3 = new BooleanConfigOption("testConfig3", "config.test_3", true);
  public static final BooleanConfigOption TEST_CONFIG_4 = new BooleanConfigOption("testConfig4", "config.test_4", true);
  public static final BooleanConfigOption TEST_CONFIG_5 = new BooleanConfigOption("testConfig5", "config.test_5", true);
  public static final BooleanConfigOption TEST_CONFIG_6 = new BooleanConfigOption("testConfig6", "config.test_6", true);
  public static final BooleanConfigOption TEST_CONFIG_7 = new BooleanConfigOption("testConfig7", "config.test_7", true);
  public static final BooleanConfigOption TEST_CONFIG_8 = new BooleanConfigOption("testConfig8", "config.test_8", true);
  public static final BooleanConfigOption TEST_CONFIG_9 = new BooleanConfigOption("testConfig9", "config.test_9", true);
  public static final BooleanConfigOption TEST_CONFIG_A = new BooleanConfigOption("testConfigA", "config.test_a", true);
  public static final BooleanConfigOption TEST_CONFIG_B = new BooleanConfigOption("testConfigB", "config.test_b", true);
  public static final BooleanConfigOption TEST_CONFIG_C = new BooleanConfigOption("testConfigC", "config.test_c", true);
  public static final BooleanConfigOption TEST_CONFIG_D = new BooleanConfigOption("testConfigD", "config.test_d", true);
  public static final BooleanConfigOption TEST_CONFIG_E = new BooleanConfigOption("testConfigE", "config.test_e", true);
  public static final BooleanConfigOption TEST_CONFIG_F = new BooleanConfigOption("testConfigF", "config.test_f", true);
  public static final BooleanConfigOption TEST_CONFIG_G = new BooleanConfigOption("testConfigG", "config.test_g", true);
  public static final BooleanConfigOption TEST_CONFIG_H = new BooleanConfigOption("testConfigH", "config.test_h", true);
  public static final BooleanConfigOption TEST_CONFIG_I = new BooleanConfigOption("testConfigI", "config.test_i", true);
  public static final StringConfigOption RANDOM_STRING = new StringConfigOption("randomString", "config.randon_string",
      "fhjdsaghls");

  private static final ImmutableList<ConfigOption<?, ?>> ALL_CONFIG_OPTIONS = ImmutableList.of(
      GUI_ALIGNMENT,
      SOME_INTEGER,
      STEP_BY_TWO,
      WITHIN_ONE_HUNDRED,
      WITHIN_ONE_HUNDRED_BIG_STEP,
      MY_NAME,
      TEST_CONFIG_1,
      TEST_CONFIG_2,
      TEST_CONFIG_3,
      TEST_CONFIG_4,
      TEST_CONFIG_5,
      TEST_CONFIG_6,
      TEST_CONFIG_7,
      TEST_CONFIG_8,
      TEST_CONFIG_9,
      TEST_CONFIG_A,
      TEST_CONFIG_B,
      TEST_CONFIG_C,
      TEST_CONFIG_D,
      TEST_CONFIG_E,
      TEST_CONFIG_F,
      TEST_CONFIG_G,
      TEST_CONFIG_H,
      TEST_CONFIG_I,
      RANDOM_STRING);

  public RoundaLibConfig() {
    super(RoundaLibMod.MOD_INFO);
  }

  @Override
  public ImmutableList<ConfigOption<?, ?>> getConfigOptions() {
    return ALL_CONFIG_OPTIONS;
  }
}
