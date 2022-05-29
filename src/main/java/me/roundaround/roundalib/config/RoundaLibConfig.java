package me.roundaround.roundalib.config;

import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

import me.roundaround.roundalib.RoundaLibMod;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.config.option.IntConfigOption;
import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.option.StringConfigOption;
import me.roundaround.roundalib.config.value.GuiAlignment;
import net.minecraft.client.gui.screen.ScreenTexts;

public class RoundaLibConfig extends ModConfig {
  // TODO: Consider a single static/global config registry with the following:
  // register(MODID, ConfigOption)
  // isDirty(MODID)
  // getConfigGroups(MODID)
  // getConfigOptions(MODID)

  public static final OptionListConfigOption<GuiAlignment> GUI_ALIGNMENT;

  public static final IntConfigOption SOME_INTEGER;
  public static final IntConfigOption STEP_BY_TWO;
  public static final IntConfigOption WITHIN_ONE_HUNDRED;
  public static final IntConfigOption WITHIN_ONE_HUNDRED_BIG_STEP;

  public static final StringConfigOption PLAIN_STRING;
  public static final StringConfigOption RESTRICTED_LENGTH;
  public static final StringConfigOption REGEX;
  public static final StringConfigOption WITH_COMMENT;

  public static final BooleanConfigOption BASIC_TOGGLE;
  public static final BooleanConfigOption DEFAULT_FALSE;
  public static final BooleanConfigOption ON_OFF;

  public static final StringConfigOption GROUP_ITEM_1;
  public static final StringConfigOption GROUP_ITEM_2;
  public static final StringConfigOption GROUP_ITEM_3;
  public static final StringConfigOption GROUP_ITEM_4;

  static {
    GUI_ALIGNMENT = OptionListConfigOption
        .defaultInstance("guiAlignment", "config.gui_alignment", GuiAlignment.TOP_LEFT);

    SOME_INTEGER = IntConfigOption
        .builder("someInteger", "config.some_integer")
        .setDefaultValue(5)
        .build();
    STEP_BY_TWO = IntConfigOption
        .builder("stepByTwo", "config.step_by_two")
        .setDefaultValue(10)
        .setStep(2)
        .build();
    WITHIN_ONE_HUNDRED = IntConfigOption
        .builder("withinOneHundred", "config.within_one_hundred")
        .setDefaultValue(10)
        .setMinValue(-100)
        .setMaxValue(100)
        .build();
    WITHIN_ONE_HUNDRED_BIG_STEP = IntConfigOption
        .builder("withinOneHundredBigStep", "config.within_one_hundred_big_step")
        .setDefaultValue(10)
        .setMinValue(-100)
        .setMaxValue(100)
        .setStep(33)
        .build();

    PLAIN_STRING = StringConfigOption
        .defaultInstance("plainString", "config.plain_string", "Roundalib");
    RESTRICTED_LENGTH = StringConfigOption
        .builder("restrictedLength", "config.restricted_length")
        .setDefaultValue("3 to 6")
        .setMinLength(3)
        .setMaxLength(6)
        .build();
    REGEX = StringConfigOption
        .builder("regex", "config.regex")
        .setDefaultValue("alpha_numeric")
        .setRegex(Pattern.compile("^[a-zA-Z0-9_]"))
        .build();
    WITH_COMMENT = StringConfigOption
        .builder("withComment", "config.with_comment")
        .setDefaultValue("Roundalib")
        .setComment(ScreenTexts.PROCEED)
        .build();

    BASIC_TOGGLE = BooleanConfigOption
        .builder("basicToggle", "config.basic_toggle")
        .build();
    DEFAULT_FALSE = BooleanConfigOption
        .builder("defaultFalse", "config.default_false")
        .setDefaultValue(false)
        .build();
    ON_OFF = BooleanConfigOption
        .onOffBuilder("onOff", "config.on_off")
        .build();

    GROUP_ITEM_1 = StringConfigOption
        .defaultInstance("group.item1", "config.group.item1", "Item 1");
    GROUP_ITEM_2 = StringConfigOption
        .defaultInstance("group.item2", "config.group.item2", "Item 2");
    GROUP_ITEM_3 = StringConfigOption
        .defaultInstance("group.item3", "config.group.item3", "Item 3");
    GROUP_ITEM_4 = StringConfigOption
        .defaultInstance("group.item4", "config.group.item4", "Item 4");
  }

  public RoundaLibConfig() {
    super(RoundaLibMod.MOD_INFO, ImmutableList.of(
        GUI_ALIGNMENT,
        SOME_INTEGER,
        STEP_BY_TWO,
        WITHIN_ONE_HUNDRED,
        WITHIN_ONE_HUNDRED_BIG_STEP,
        PLAIN_STRING,
        RESTRICTED_LENGTH,
        REGEX,
        WITH_COMMENT,
        BASIC_TOGGLE,
        DEFAULT_FALSE,
        ON_OFF,
        GROUP_ITEM_1,
        GROUP_ITEM_2,
        GROUP_ITEM_3,
        GROUP_ITEM_4));
  }
}
