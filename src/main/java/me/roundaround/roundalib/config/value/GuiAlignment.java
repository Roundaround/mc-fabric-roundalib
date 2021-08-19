package me.roundaround.roundalib.config.value;

import net.minecraft.client.resource.language.I18n;

import java.util.Arrays;

public enum GuiAlignment implements ListOptionValue {
    TOP_LEFT ("top", "left"),
    TOP_CENTER ("top", "center"),
    TOP_RIGHT ("top", "right"),
    MIDDLE_LEFT ("middle", "left"),
    MIDDLE_CENTER ("middle", "center"),
    MIDDLE_RIGHT ("middle", "right"),
    BOTTOM_LEFT ("bottom", "left"),
    BOTTOM_CENTER ("bottom", "center"),
    BOTTOM_RIGHT ("bottom", "right");

    private final String alignmentX;
    private final String alignmentY;
    private final String id;

    GuiAlignment(String alignmentX, String alignmentY) {
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

    public String getAlignmentX() {
        return alignmentX;
    }

    public String getAlignmentY() {
        return alignmentY;
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
}
