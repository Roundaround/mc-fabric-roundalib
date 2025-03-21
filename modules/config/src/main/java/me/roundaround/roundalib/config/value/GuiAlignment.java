package me.roundaround.roundalib.config.value;

import net.minecraft.client.MinecraftClient;

import java.util.Arrays;

public enum GuiAlignment implements EnumValue<GuiAlignment> {
  TOP_LEFT(AlignmentY.TOP, AlignmentX.LEFT),
  TOP_RIGHT(AlignmentY.TOP, AlignmentX.RIGHT),
  BOTTOM_LEFT(AlignmentY.BOTTOM, AlignmentX.LEFT),
  BOTTOM_RIGHT(AlignmentY.BOTTOM, AlignmentX.RIGHT);

  private final AlignmentX alignmentX;
  private final AlignmentY alignmentY;
  private final String id;

  GuiAlignment(AlignmentY alignmentY, AlignmentX alignmentX) {
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
  public GuiAlignment getFromId(String id) {
    return fromId(id);
  }

  @Override
  public GuiAlignment getNext() {
    return values()[(this.ordinal() + 1) % values().length];
  }

  @Override
  public GuiAlignment getPrev() {
    return values()[(this.ordinal() + values().length - 1) % values().length];
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

  public static GuiAlignment getDefault() {
    return TOP_LEFT;
  }

  public static GuiAlignment fromId(String id) {
    return Arrays.stream(GuiAlignment.values())
        .filter(guiAlignment -> guiAlignment.id.equals(id))
        .findFirst()
        .orElse(getDefault());
  }

  public static int getPosX(GuiAlignment guiAlignment) {
    return guiAlignment.alignmentX.getPos();
  }

  public static int getPosY(GuiAlignment guiAlignment) {
    return guiAlignment.alignmentY.getPos();
  }

  public static int getOffsetMultiplierX(GuiAlignment guiAlignment) {
    return guiAlignment.alignmentX.getOffsetMultiplier();
  }

  public static int getOffsetMultiplierY(GuiAlignment guiAlignment) {
    return guiAlignment.alignmentY.getOffsetMultiplier();
  }

  private static int getScaledWindowWidth() {
    return MinecraftClient.getInstance().getWindow().getScaledWidth();
  }

  private static int getScaledWindowHeight() {
    return MinecraftClient.getInstance().getWindow().getScaledHeight();
  }

  public enum AlignmentX {
    LEFT("left"), RIGHT("right");

    private final String value;

    AlignmentX(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return this.value;
    }

    public int getPos() {
      return this.value.equals("right") ? getScaledWindowWidth() : 0;
    }

    public int getOffsetMultiplier() {
      return this.value.equals("right") ? -1 : 1;
    }
  }

  public enum AlignmentY {
    TOP("top"), BOTTOM("bottom");

    private final String value;

    AlignmentY(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return this.value;
    }

    public int getPos() {
      return this.value.equals("bottom") ? getScaledWindowHeight() : 0;
    }

    public int getOffsetMultiplier() {
      return this.value.equals("bottom") ? -1 : 1;
    }
  }
}
