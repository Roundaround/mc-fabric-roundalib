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
import net.minecraft.client.gui.screen.ScreenTexts;

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

  public static final StringConfigOption PLAIN_STRING = new StringConfigOption("plainString", "config.plain_string",
      "Roundalib");
  public static final StringConfigOption RESTRICTED_LENGTH = new StringConfigOption("restrictedLength",
      "config.restricted_length", "At least 3 chars",
      StringConfigOption.Options.builder().setMinLength(3).setMaxLength(6).build());
  public static final StringConfigOption REGEX = new StringConfigOption("regex",
      "config.regex", "alpha_numeric",
      StringConfigOption.Options.builder().setRegex(Pattern.compile("^[a-zA-Z0-9_]")).build());

  public static final BooleanConfigOption BASIC_TOGGLE = new BooleanConfigOption("basicToggle", "config.basic_toggle",
      true);
  public static final BooleanConfigOption DEFAULT_FALSE = new BooleanConfigOption("defaultFalse",
      "config.default_false", false);
  public static final BooleanConfigOption ON_OFF = new BooleanConfigOption("onOff", "config.on_off", true,
      ScreenTexts.ON, ScreenTexts.OFF);

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
