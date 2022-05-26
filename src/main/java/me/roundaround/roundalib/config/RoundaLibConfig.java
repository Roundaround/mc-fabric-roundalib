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
  public static final StringConfigOption MY_NAME = new StringConfigOption("myName", "config.my_name", "Roundalib");
  public static final StringConfigOption RANDOM_STRING = new StringConfigOption("randomString", "config.randon_string",
      "fhjdsaghls");
  public static final BooleanConfigOption TEST_CONFIG_1 = new BooleanConfigOption("testConfig1", "config.test_1", true);
  public static final BooleanConfigOption TEST_CONFIG_2 = new BooleanConfigOption("testConfig2", "config.test_2", true);
  public static final BooleanConfigOption TEST_CONFIG_3 = new BooleanConfigOption("testConfig3", "config.test_3", true);
  public static final BooleanConfigOption TEST_CONFIG_4 = new BooleanConfigOption("testConfig4", "config.test_4", true);
  public static final BooleanConfigOption TEST_CONFIG_5 = new BooleanConfigOption("testConfig5", "config.test_5", true);
  public static final BooleanConfigOption TEST_CONFIG_6 = new BooleanConfigOption("testConfig6", "config.test_6", true);
  public static final BooleanConfigOption TEST_CONFIG_7 = new BooleanConfigOption("testConfig7", "config.test_7", true);
  public static final BooleanConfigOption TEST_CONFIG_8 = new BooleanConfigOption("testConfig8", "config.test_8", true);
  public static final BooleanConfigOption TEST_CONFIG_9 = new BooleanConfigOption("testConfig9", "config.test_9", true);

  private static final ImmutableList<ConfigOption<?>> ALL_CONFIG_OPTIONS = ImmutableList.of(
      GUI_ALIGNMENT,
      SOME_INTEGER,
      MY_NAME,
      RANDOM_STRING,
      TEST_CONFIG_1,
      TEST_CONFIG_2,
      TEST_CONFIG_3,
      TEST_CONFIG_4,
      TEST_CONFIG_5,
      TEST_CONFIG_6,
      TEST_CONFIG_7,
      TEST_CONFIG_8,
      TEST_CONFIG_9);

  public RoundaLibConfig() {
    super(RoundaLibMod.MOD_INFO);
  }

  @Override
  public ImmutableList<ConfigOption<?>> getConfigOptions() {
    return ALL_CONFIG_OPTIONS;
  }
}
