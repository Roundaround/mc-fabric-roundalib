package me.roundaround.roundalib.config.value;

import java.util.Arrays;

public enum GuiTheme implements ListOptionValue<GuiTheme> {
  LIGHT("light"), DARK("dark"), AUTO("auto");

  private final String id;

  GuiTheme(String id) {
    this.id = id;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String getI18nKey(String modId) {
    return modId + ".roundalib.gui_theme." + this.id;
  }

  @Override
  public GuiTheme getFromId(String id) {
    return fromId(id);
  }

  @Override
  public GuiTheme getNext() {
    return values()[(this.ordinal() + 1) % values().length];
  }

  @Override
  public GuiTheme getPrev() {
    return values()[(this.ordinal() + values().length - 1) % values().length];
  }

  public static GuiTheme getDefault() {
    return AUTO;
  }

  public static GuiTheme fromId(String id) {
    return Arrays.stream(GuiTheme.values())
        .filter(guiAlignment -> guiAlignment.id.equals(id))
        .findFirst()
        .orElse(getDefault());
  }
}
