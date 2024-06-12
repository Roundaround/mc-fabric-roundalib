package me.roundaround.roundalib.client.gui;

import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.option.IntConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class RoundaLibIconButtons {
  private static final MinecraftClient client = MinecraftClient.getInstance();

  public static final int SIZE_L = 18;
  public static final int SIZE_M = 13;
  public static final int SIZE_S = 9;
  public static final int ORIGIN_L = 0;
  public static final int ORIGIN_M = ORIGIN_L + 2 * SIZE_L;
  public static final int ORIGIN_S = ORIGIN_M + 2 * SIZE_M;
  public static final int INDEX_RESET = 0;
  public static final int INDEX_CANCEL = 1;
  public static final int INDEX_CONFIRM = 2;
  public static final int INDEX_HELP = 3;
  public static final int INDEX_CLOSE = 4;
  public static final int INDEX_UP = 5;
  public static final int INDEX_DOWN = 6;
  public static final int INDEX_LEFT = 7;
  public static final int INDEX_RIGHT = 8;
  public static final int INDEX_PLUS = 9;
  public static final int INDEX_MINUS = 10;

  public static IconButtonWidget resetButton(ConfigOption<?> option) {
    return resetButton(0, 0, option, SIZE_M);
  }

  public static IconButtonWidget resetButton(int x, int y, ConfigOption<?> option) {
    return resetButton(x, y, option, SIZE_M);
  }

  public static IconButtonWidget resetButton(ConfigOption<?> option, int size) {
    return resetButton(0, 0, option, size);
  }

  public static IconButtonWidget resetButton(int x, int y, ConfigOption<?> option, int size) {
    return resetButton(x, y, size, option.getModId(), (buttonWidget) -> option.setDefault());
  }

  public static IconButtonWidget resetButton(
      int x, int y, int size, String modId, PressAction onPress
  ) {
    Identifier texture = new Identifier(modId, "textures/roundalib.png");

    return IconButtonWidget.builder(texture, castPressAction(onPress))
        .size(size)
        .position(x, y)
        .autoCalculateUV(INDEX_RESET, 0, getOriginForSize(size))
        .tooltip(Text.translatable(modId + ".roundalib.reset.tooltip"))
        .build();
  }

  public static IconButtonWidget closeButton(String modId, PressAction onPress) {
    return closeButton(0, 0, modId, onPress);
  }

  public static IconButtonWidget closeButton(int x, int y, String modId, PressAction onPress) {
    return closeButton(x, y, SIZE_M, modId, onPress);
  }

  public static IconButtonWidget closeButton(String modId, int size, PressAction onPress) {
    return closeButton(0, 0, size, modId, onPress);
  }

  public static IconButtonWidget closeButton(
      int x, int y, int size, String modId, PressAction onPress
  ) {
    Identifier texture = new Identifier(modId, "textures/roundalib.png");

    return IconButtonWidget.builder(texture, castPressAction(onPress))
        .size(size)
        .position(x, y)
        .autoCalculateUV(INDEX_CLOSE, 0, getOriginForSize(size))
        .tooltip(Text.translatable(modId + ".roundalib.close.tooltip"))
        .build();
  }

  public static IconButtonWidget intStepButton(
      int x, int y, IntConfigOption option, boolean increment
  ) {
    String modId = option.getModId();
    Identifier texture = new Identifier(modId, "textures/roundalib.png");

    return IconButtonWidget.builder(texture, (buttonWidget) -> {
          if (increment) {
            option.increment();
          } else {
            option.decrement();
          }
        })
        .size(SIZE_S)
        .position(x, y)
        .autoCalculateUV(increment ? INDEX_PLUS : INDEX_MINUS, 0, ORIGIN_S)
        .tooltip(Text.translatable(modId + ".roundalib." + (increment ? "step_up" : "step_down") + ".tooltip",
            option.getStep()
        ))
        .build();
  }

  public static IconButtonWidget saveButton(int x, int y, String modId, PressAction onPress) {
    return saveButton(x, y, modId, onPress, SIZE_M);
  }

  public static IconButtonWidget saveButton(
      int x, int y, String modId, PressAction onPress, int size
  ) {
    Identifier texture = new Identifier(modId, "textures/roundalib.png");

    return IconButtonWidget.builder(texture, castPressAction(onPress))
        .size(size)
        .position(x, y)
        .autoCalculateUV(INDEX_CONFIRM, 0, getOriginForSize(size))
        .tooltip(Text.translatable(modId + ".roundalib.save.tooltip"))
        .build();
  }

  public static IconButtonWidget discardButton(int x, int y, String modId, PressAction onPress) {
    return discardButton(x, y, modId, onPress, SIZE_M);
  }

  public static IconButtonWidget discardButton(
      int x, int y, String modId, PressAction onPress, int size
  ) {
    Identifier texture = new Identifier(modId, "textures/roundalib.png");

    return IconButtonWidget.builder(texture, castPressAction(onPress))
        .size(size)
        .position(x, y)
        .autoCalculateUV(INDEX_CANCEL, 0, getOriginForSize(size))
        .tooltip(Text.translatable(modId + ".roundalib.discard.tooltip"))
        .build();
  }

  public static IconButtonWidget upButton(String modId, PressAction onPress) {
    return upButton(0, 0, modId, onPress, SIZE_M);
  }

  public static IconButtonWidget upButton(int x, int y, String modId, PressAction onPress) {
    return upButton(x, y, modId, onPress, SIZE_M);
  }

  public static IconButtonWidget upButton(
      int x, int y, String modId, PressAction onPress, int size
  ) {
    Identifier texture = new Identifier(modId, "textures/roundalib.png");

    return IconButtonWidget.builder(texture, castPressAction(onPress))
        .size(size)
        .position(x, y)
        .autoCalculateUV(INDEX_UP, 0, getOriginForSize(size))
        .tooltip(Text.translatable(modId + ".roundalib.up.tooltip"))
        .build();
  }

  public static IconButtonWidget downButton(String modId, PressAction onPress) {
    return downButton(0, 0, modId, onPress, SIZE_M);
  }

  public static IconButtonWidget downButton(int x, int y, String modId, PressAction onPress) {
    return downButton(x, y, modId, onPress, SIZE_M);
  }

  public static IconButtonWidget downButton(
      int x, int y, String modId, PressAction onPress, int size
  ) {
    Identifier texture = new Identifier(modId, "textures/roundalib.png");

    return IconButtonWidget.builder(texture, castPressAction(onPress))
        .size(size)
        .position(x, y)
        .autoCalculateUV(INDEX_DOWN, 0, getOriginForSize(size))
        .tooltip(Text.translatable(modId + ".roundalib.down.tooltip"))
        .build();
  }

  public static IconButtonWidget leftButton(String modId, PressAction onPress) {
    return leftButton(0, 0, modId, onPress, SIZE_M);
  }

  public static IconButtonWidget leftButton(int x, int y, String modId, PressAction onPress) {
    return leftButton(x, y, modId, onPress, SIZE_M);
  }

  public static IconButtonWidget leftButton(
      int x, int y, String modId, PressAction onPress, int size
  ) {
    Identifier texture = new Identifier(modId, "textures/roundalib.png");

    return IconButtonWidget.builder(texture, castPressAction(onPress))
        .size(size)
        .position(x, y)
        .autoCalculateUV(INDEX_LEFT, 0, getOriginForSize(size))
        .tooltip(Text.translatable(modId + ".roundalib.left.tooltip"))
        .build();
  }

  public static IconButtonWidget rightButton(String modId, PressAction onPress) {
    return rightButton(0, 0, modId, onPress, SIZE_M);
  }

  public static IconButtonWidget rightButton(int x, int y, String modId, PressAction onPress) {
    return rightButton(x, y, modId, onPress, SIZE_M);
  }

  public static IconButtonWidget rightButton(
      int x, int y, String modId, PressAction onPress, int size
  ) {
    Identifier texture = new Identifier(modId, "textures/roundalib.png");

    return IconButtonWidget.builder(texture, castPressAction(onPress))
        .size(size)
        .position(x, y)
        .autoCalculateUV(INDEX_RIGHT, 0, getOriginForSize(size))
        .tooltip(Text.translatable(modId + ".roundalib.right.tooltip"))
        .build();
  }

  private static int getOriginForSize(int size) {
    return switch (size) {
      case SIZE_L -> ORIGIN_L;
      case SIZE_M -> ORIGIN_M;
      case SIZE_S -> ORIGIN_S;
      default -> throw new IllegalArgumentException("Invalid size: " + size);
    };
  }

  private static ButtonWidget.PressAction castPressAction(PressAction onPress) {
    return (buttonWidget) -> onPress.onPress((IconButtonWidget) buttonWidget);
  }

  @FunctionalInterface
  public interface PressAction {
    void onPress(IconButtonWidget button);
  }
}
