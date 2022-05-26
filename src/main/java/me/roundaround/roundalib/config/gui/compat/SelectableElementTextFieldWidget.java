package me.roundaround.roundalib.config.gui.compat;

import me.roundaround.roundalib.config.gui.SelectableElement;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class SelectableElementTextFieldWidget extends TextFieldWidget implements SelectableElement {
  public SelectableElementTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height,
      TextFieldWidget copyFrom, Text text) {
    super(textRenderer, x, y, width, height, copyFrom, text);
  }

  public SelectableElementTextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
    super(textRenderer, x, y, width, height, text);
  }
}
