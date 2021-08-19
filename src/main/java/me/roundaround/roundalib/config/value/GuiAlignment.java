package me.roundaround.roundalib.config.value;

import me.roundaround.roundalib.util.GuiUtil;
import net.minecraft.client.resource.language.I18n;

import java.util.Arrays;

public enum GuiAlignment implements ListOptionValue {
    TOP_LEFT (AlignmentY.TOP, AlignmentX.LEFT),
    TOP_CENTER (AlignmentY.TOP, AlignmentX.CENTER),
    TOP_RIGHT (AlignmentY.TOP, AlignmentX.RIGHT),
    MIDDLE_LEFT (AlignmentY.MIDDLE, AlignmentX.LEFT),
    MIDDLE_CENTER (AlignmentY.MIDDLE, AlignmentX.CENTER),
    MIDDLE_RIGHT (AlignmentY.MIDDLE, AlignmentX.RIGHT),
    BOTTOM_LEFT (AlignmentY.BOTTOM, AlignmentX.LEFT),
    BOTTOM_CENTER (AlignmentY.BOTTOM, AlignmentX.CENTER),
    BOTTOM_RIGHT (AlignmentY.BOTTOM, AlignmentX.RIGHT);

    private final AlignmentX alignmentX;
    private final AlignmentY alignmentY;
    private final String id;

    GuiAlignment(AlignmentY alignmentY, AlignmentX alignmentX) {
        this.alignmentX = alignmentX;
        this.alignmentY = alignmentY;
        this.id = alignmentX + "_" + alignmentY;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public GuiAlignment getFromId(String id) {
        return fromId(id);
    }

    @Override
    public String getDisplayString() {
        return I18n.translate("me.roundaround.roundalib.config.gui_alignment." + this.id);
    }
    
    @Override
    public GuiAlignment getNext() {
        return values()[this.ordinal() + 1 % values().length];
    }

    @Override
    public GuiAlignment getPrev() {
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

    public enum AlignmentX {
        LEFT("left"),
        CENTER("center"),
        RIGHT("right");

        private final String value;

        AlignmentX(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public int getPos() {
            return switch (value) {
                case "center" -> GuiUtil.getScaledWindowWidth() / 2;
                case "right" -> GuiUtil.getScaledWindowWidth();
                default -> 0;
            };
        }
    }

    public enum AlignmentY {
        TOP("top"),
        MIDDLE("middle"),
        BOTTOM("bottom");

        private final String value;

        AlignmentY(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public int getPos() {
            return switch (value) {
                case "middle" -> GuiUtil.getScaledWindowHeight() / 2;
                case "bottom" -> GuiUtil.getScaledWindowHeight();
                default -> 0;
            };
        }
    }
}
