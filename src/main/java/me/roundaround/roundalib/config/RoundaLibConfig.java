package me.roundaround.roundalib.config;

import java.util.regex.Pattern;

import me.roundaround.roundalib.RoundaLibMod;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.config.option.IntConfigOption;
import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.option.StringConfigOption;
import me.roundaround.roundalib.config.value.GuiAlignment;
import net.minecraft.client.gui.screen.ScreenTexts;

public class RoundaLibConfig extends ModConfig {
  public final OptionListConfigOption<GuiAlignment> GUI_ALIGNMENT;

  public final IntConfigOption SOME_INTEGER;
  public final IntConfigOption STEP_BY_TWO;
  public final IntConfigOption WITHIN_ONE_HUNDRED;
  public final IntConfigOption WITHIN_ONE_HUNDRED_BIG_STEP;

  public final StringConfigOption PLAIN_STRING;
  public final StringConfigOption RESTRICTED_LENGTH;
  public final StringConfigOption REGEX;
  public final StringConfigOption WITH_COMMENT;

  public final BooleanConfigOption BASIC_TOGGLE;
  public final BooleanConfigOption DEFAULT_FALSE;
  public final BooleanConfigOption ON_OFF;

  public final StringConfigOption GROUP_ITEM_1;
  public final StringConfigOption GROUP_ITEM_2;
  public final StringConfigOption GROUP_ITEM_3;
  public final StringConfigOption GROUP_ITEM_4;

  public RoundaLibConfig() {
    super(RoundaLibMod.MOD_INFO);

    GUI_ALIGNMENT = registerConfigOption(OptionListConfigOption
        .defaultInstance("guiAlignment", "config.gui_alignment", GuiAlignment.TOP_LEFT));

    SOME_INTEGER = registerConfigOption(IntConfigOption
        .builder("someInteger", "config.some_integer")
        .setDefaultValue(5)
        .build());
    STEP_BY_TWO = registerConfigOption(IntConfigOption
        .builder("stepByTwo", "config.step_by_two")
        .setDefaultValue(10)
        .setStep(2)
        .build());
    WITHIN_ONE_HUNDRED = registerConfigOption(IntConfigOption
        .builder("withinOneHundred", "config.within_one_hundred")
        .setDefaultValue(10)
        .setMinValue(-100)
        .setMaxValue(100)
        .build());
    WITHIN_ONE_HUNDRED_BIG_STEP = registerConfigOption(IntConfigOption
        .builder("withinOneHundredBigStep", "config.within_one_hundred_big_step")
        .setDefaultValue(10)
        .setMinValue(-100)
        .setMaxValue(100)
        .setStep(33)
        .build());

    PLAIN_STRING = registerConfigOption(StringConfigOption
        .defaultInstance("plainString", "config.plain_string", "Roundalib"));
    RESTRICTED_LENGTH = registerConfigOption(StringConfigOption
        .builder("restrictedLength", "config.restricted_length")
        .setDefaultValue("3 to 6")
        .setMinLength(3)
        .setMaxLength(6)
        .build());
    REGEX = registerConfigOption(StringConfigOption
        .builder("regex", "config.regex")
        .setDefaultValue("alpha_numeric")
        .setRegex(Pattern.compile("^[a-zA-Z0-9_]"))
        .build());
    WITH_COMMENT = registerConfigOption(StringConfigOption
        .builder("withComment", "config.with_comment")
        .setDefaultValue("Roundalib")
        .setComment(ScreenTexts.PROCEED)
        .build());

    BASIC_TOGGLE = registerConfigOption(BooleanConfigOption
        .builder("basicToggle", "config.basic_toggle")
        .build());
    DEFAULT_FALSE = registerConfigOption(BooleanConfigOption
        .builder("defaultFalse", "config.default_false")
        .setDefaultValue(false)
        .build());
    ON_OFF = registerConfigOption(BooleanConfigOption
        .onOffBuilder("onOff", "config.on_off")
        .build());

    GROUP_ITEM_1 = registerConfigOption("group", StringConfigOption
        .defaultInstance("item1", "config.group.item1", "Item 1"));
    GROUP_ITEM_2 = registerConfigOption("group", StringConfigOption
        .defaultInstance("item2", "config.group.item2", "Item 2"));
    GROUP_ITEM_3 = registerConfigOption("group", StringConfigOption
        .defaultInstance("item3", "config.group.item3", "Item 3"));
    GROUP_ITEM_4 = registerConfigOption("group", StringConfigOption
        .defaultInstance("item4", "config.group.item4", "Item 4"));
  }
}
