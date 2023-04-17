package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class RoundaLibIconButtons {
  public static final int SIZE_L = 18;
  public static final int SIZE_M = 13;
  public static final int SIZE_S = 9;
  public static final int ORIGIN_L = 0;
  public static final int ORIGIN_M = ORIGIN_L + 2 * SIZE_L;
  public static final int ORIGIN_S = ORIGIN_M + 2 * SIZE_M;
  public static final int INDEX_RESET = 0;
  public static final int INDEX_CANCEL = 1;
  public static final int INDEX_DONE = 2;
  public static final int INDEX_HELP = 3;
  public static final int INDEX_CLOSE = 4;
  public static final int INDEX_UP = 5;
  public static final int INDEX_DOWN = 6;
  public static final int INDEX_LEFT = 7;
  public static final int INDEX_RIGHT = 8;
  public static final int INDEX_PLUS = 9;
  public static final int INDEX_MINUS = 10;

  public static IconButtonWidget resetButton(int x, int y, ConfigOption<?, ?> option) {
    return resetButton(x, y, option, SIZE_M);
  }

  public static IconButtonWidget resetButton(int x, int y, ConfigOption<?, ?> option, int size) {
    String modId = option.getConfig().getModId();
    Identifier texture = new Identifier(modId, "textures/roundalib.png");

    IconButtonWidget button =
        IconButtonWidget.builder(texture, (buttonWidget) -> option.resetToDefault())
            .size(size)
            .position(x, y)
            .autoCalculateUV(INDEX_RESET, 0, getOriginForSize(size))
            .tooltip(Text.translatable(modId + ".roundalib.reset.tooltip"))
            .build();

    button.active = option.isModified();
    option.subscribeToValueChanges((oldValue, newValue) -> {
      button.active = option.isModified();
    });

    return button;
  }

  private static int getOriginForSize(int size) {
    return switch (size) {
      case SIZE_L -> ORIGIN_L;
      case SIZE_M -> ORIGIN_M;
      case SIZE_S -> ORIGIN_S;
      default -> throw new IllegalArgumentException("Invalid size: " + size);
    };
  }
}
