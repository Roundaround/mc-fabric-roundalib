package me.roundaround.roundalib.config;

import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

import me.roundaround.roundalib.RoundaLibMod;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.option.IntConfigOption;
import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.option.StringConfigOption;
import me.roundaround.roundalib.config.value.GuiAlignment;

public class RoundaLibConfig extends ModConfig {
  public static final OptionListConfigOption<GuiAlignment> GUI_ALIGNMENT = OptionListConfigOption.defaultInstance(
      "guiAlignment",
      "config.gui_alignment", GuiAlignment.TOP_LEFT);

  public static final IntConfigOption SOME_INTEGER = IntConfigOption.builder("someInteger", "config.some_integer")
      .setDefaultValue(5).build();
  public static final IntConfigOption STEP_BY_TWO = IntConfigOption.builder("stepByTwo", "config.step_by_two")
      .setDefaultValue(10).setStep(2).build();
  public static final IntConfigOption WITHIN_ONE_HUNDRED = IntConfigOption.builder("withinOneHundred",
      "config.within_one_hundred").setDefaultValue(10).setMinValue(-100).setMaxValue(100).build();
  public static final IntConfigOption WITHIN_ONE_HUNDRED_BIG_STEP = IntConfigOption.builder("withinOneHundredBigStep",
      "config.within_one_hundred_big_step").setDefaultValue(10).setMinValue(-100).setMaxValue(100).setStep(33).build();

  public static final StringConfigOption PLAIN_STRING = StringConfigOption.defaultInstance("plainString",
      "config.plain_string", "Roundalib");
  public static final StringConfigOption RESTRICTED_LENGTH = StringConfigOption.builder("restrictedLength",
      "config.restricted_length").setDefaultValue("3 to 6").setMinLength(3).setMaxLength(6).build();
  public static final StringConfigOption REGEX = StringConfigOption.builder("regex",
      "config.regex").setDefaultValue("alpha_numeric").setRegex(Pattern.compile("^[a-zA-Z0-9_]")).build();

  public static final BooleanConfigOption BASIC_TOGGLE = BooleanConfigOption
      .builder("basicToggle", "config.basic_toggle").build();
  public static final BooleanConfigOption DEFAULT_FALSE = BooleanConfigOption
      .builder("defaultFalse", "config.default_false").setDefaultValue(false).build();
  public static final BooleanConfigOption ON_OFF = BooleanConfigOption.onOffBuilder("onOff", "config.on_off").build();

  private static final ImmutableList<ConfigOption<?, ?>> ALL_CONFIG_OPTIONS = ImmutableList.of(
      GUI_ALIGNMENT,
      SOME_INTEGER,
      STEP_BY_TWO,
      WITHIN_ONE_HUNDRED,
      WITHIN_ONE_HUNDRED_BIG_STEP,
      PLAIN_STRING,
      RESTRICTED_LENGTH,
      REGEX,
      BASIC_TOGGLE,
      DEFAULT_FALSE,
      ON_OFF);

  public RoundaLibConfig() {
    super(RoundaLibMod.MOD_INFO);
  }

  @Override
  public ImmutableList<ConfigOption<?, ?>> getConfigOptions() {
    return ALL_CONFIG_OPTIONS;
  }
}
