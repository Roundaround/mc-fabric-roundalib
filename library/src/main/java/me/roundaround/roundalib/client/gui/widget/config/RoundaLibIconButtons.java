package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class RoundaLibIconButtons {
  public static final int SIZE_LG = 13;
  public static final int SIZE_SM = 9;
  public static final int SMALL_TEX_START_X = 3 * SIZE_LG;
  public static final int INDEX_RESET = 0;

  public static IconButtonWidget resetButton(int x, int y, ConfigOption<?, ?> option) {
    String modId = option.getConfig().getModId();
    Identifier texture = new Identifier(modId, "textures/roundalib.png");

    IconButtonWidget button = IconButtonWidget.builder(texture, (buttonWidget) -> option.resetToDefault())
        .size(SIZE_LG)
        .position(x, y)
        .autoCalculateUV(INDEX_RESET)
        .tooltip(Text.translatable(modId + ".roundalib.reset.tooltip"))
        .build();

    option.subscribeToValueChanges((oldValue, newValue) -> {
      button.active = option.isModified();
    });

    return button;
  }

  public record Position(int x, int y) {
  }
}
