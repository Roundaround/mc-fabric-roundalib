package me.roundaround.roundalib.client.gui.layout;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public enum TextAlignment {
  START, CENTER, END;

  public int getLeft(TextRenderer textRenderer, Text text, int x) {
    return this.getLeft(textRenderer, text.asOrderedText(), x);
  }

  public int getLeft(TextRenderer textRenderer, OrderedText text, int x) {
    int width = textRenderer.getWidth(text);
    return switch (this) {
      case START -> x;
      case CENTER -> x - width / 2;
      case END -> x - width;
    };
  }

  public int getLeft(int x, int width) {
    return switch (this) {
      case START -> x;
      case CENTER -> x - width / 2;
      case END -> x - width;
    };
  }

  public int getTop(TextRenderer textRenderer, int y) {
    return this.getTop(y, textRenderer.fontHeight);
  }

  public int getTop(int y, int height) {
    return switch (this) {
      case START -> y;
      case CENTER -> y - height / 2;
      case END -> y - height;
    };
  }
}
