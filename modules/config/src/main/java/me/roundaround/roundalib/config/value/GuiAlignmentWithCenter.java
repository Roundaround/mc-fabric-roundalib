package me.roundaround.roundalib.config.value;

import net.minecraft.client.MinecraftClient;

import java.util.Arrays;

public enum GuiAlignmentWithCenter implements EnumValue<GuiAlignmentWithCenter> {
  TOP_LEFT(AlignmentY.TOP, AlignmentX.LEFT),
  TOP_CENTER(AlignmentY.TOP, AlignmentX.CENTER),
  TOP_RIGHT(AlignmentY.TOP, AlignmentX.RIGHT),
  MIDDLE_LEFT(AlignmentY.MIDDLE, AlignmentX.LEFT),
  MIDDLE_CENTER(AlignmentY.MIDDLE, AlignmentX.CENTER),
  MIDDLE_RIGHT(AlignmentY.MIDDLE, AlignmentX.RIGHT),
  BOTTOM_LEFT(AlignmentY.BOTTOM, AlignmentX.LEFT),
  BOTTOM_CENTER(AlignmentY.BOTTOM, AlignmentX.CENTER),
  BOTTOM_RIGHT(AlignmentY.BOTTOM, AlignmentX.RIGHT);

  private final AlignmentX alignmentX;
  private final AlignmentY alignmentY;
  private final String id;

  GuiAlignmentWithCenter(AlignmentY alignmentY, AlignmentX alignmentX) {
    this.alignmentX = alignmentX;
    this.alignmentY = alignmentY;
    this.id = alignmentY + "_" + alignmentX;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String getI18nKey(String modId) {
    return modId + ".roundalib.gui_alignment." + this.id;
  }

  @Override
  public GuiAlignmentWithCenter getFromId(String id) {
    return fromId(id);
  }

  @Override
  public GuiAlignmentWithCenter getNext() {
    return values()[this.ordinal() + 1 % values().length];
  }

  @Override
  public GuiAlignmentWithCenter getPrev() {
    return values()[this.ordinal() + values().length - 1 % values().length];
  }

  public AlignmentX getAlignmentX() {
    return this.alignmentX;
  }

  public AlignmentY getAlignmentY() {
    return this.alignmentY;
  }

  public int getPosX() {
    return getPosX(this);
  }

  public int getPosY() {
    return getPosY(this);
  }

  public int getOffsetMultiplierX() {
    return getOffsetMultiplierX(this);
  }

  public int getOffsetMultiplierY() {
    return getOffsetMultiplierY(this);
  }

  public static GuiAlignmentWithCenter getDefault() {
    return TOP_LEFT;
  }

  public static GuiAlignmentWithCenter fromId(String id) {
    return Arrays.stream(GuiAlignmentWithCenter.values())
        .filter(guiAlignment -> guiAlignment.id.equals(id))
        .findFirst()
        .orElse(getDefault());
  }

  public static int getPosX(GuiAlignmentWithCenter guiAlignment) {
    return guiAlignment.alignmentX.getPos();
  }

  public static int getPosY(GuiAlignmentWithCenter guiAlignment) {
    return guiAlignment.alignmentY.getPos();
  }

  public static int getOffsetMultiplierX(GuiAlignmentWithCenter guiAlignment) {
    return guiAlignment.alignmentX.getOffsetMultiplier();
  }

  public static int getOffsetMultiplierY(GuiAlignmentWithCenter guiAlignment) {
    return guiAlignment.alignmentY.getOffsetMultiplier();
  }

  private static int getScaledWindowWidth() {
    return MinecraftClient.getInstance().getWindow().getScaledWidth();
  }

  private static int getScaledWindowHeight() {
    return MinecraftClient.getInstance().getWindow().getScaledHeight();
  }

  public enum AlignmentX {
    LEFT("left"), CENTER("center"), RIGHT("right");

    private final String value;

    AlignmentX(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return this.value;
    }

    public int getPos() {
      return switch (this.value) {
        case "center" -> getScaledWindowWidth() / 2;
        case "right" -> getScaledWindowWidth();
        default -> 0;
      };
    }

    public int getOffsetMultiplier() {
      return switch (this.value) {
        case "center" -> 0;
        case "right" -> -1;
        default -> 1;
      };
    }
  }

  public enum AlignmentY {
    TOP("top"), MIDDLE("middle"), BOTTOM("bottom");

    private final String value;

    AlignmentY(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return this.value;
    }

    public int getPos() {
      return switch (this.value) {
        case "middle" -> getScaledWindowHeight() / 2;
        case "bottom" -> getScaledWindowHeight();
        default -> 0;
      };
    }

    public int getOffsetMultiplier() {
      return switch (this.value) {
        case "middle" -> 0;
        case "bottom" -> -1;
        default -> 1;
      };
    }
  }
}
