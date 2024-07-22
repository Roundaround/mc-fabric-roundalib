package me.roundaround.roundalib.client.gui;

import me.roundaround.roundalib.client.gui.util.Alignment;
import me.roundaround.roundalib.config.manage.ModConfigImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.Util;

import java.util.List;

@Environment(EnvType.CLIENT)
public final class GuiUtil {
  private GuiUtil() {
  }

  public static final int LABEL_COLOR = genColorInt(1f, 1f, 1f);
  public static final int ERROR_COLOR = genColorInt(1f, 0.15f, 0.15f);
  public static final int BACKGROUND_COLOR = genColorInt(0f, 0f, 0f, 0.5f);
  public static final int TRANSPARENT_COLOR = genColorInt(0f, 0f, 0f, 0f);
  public static final int PADDING = 4;
  public static final int SCROLLBAR_WIDTH = 6;
  public static final int DEFAULT_HEADER_FOOTER_HEIGHT = 33;
  public static final int COMPACT_HEADER_HEIGHT = 17;

  private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

  public static Identifier getWidgetsTexture(ModConfigImpl config) {
    return new Identifier(config.getModId(), "textures/roundalib.png");
  }

  public static int getScaledWindowWidth() {
    return getScaledWindowWidth(CLIENT);
  }

  public static int getScaledWindowWidth(MinecraftClient client) {
    return client.getWindow().getScaledWidth();
  }

  public static int getScaledWindowHeight() {
    return getScaledWindowHeight(CLIENT);
  }

  public static int getScaledWindowHeight(MinecraftClient client) {
    return client.getWindow().getScaledHeight();
  }

  public static int getDisplayWidth() {
    return getDisplayWidth(CLIENT);
  }

  public static int getDisplayWidth(MinecraftClient client) {
    return client.getWindow().getWidth();
  }

  public static int getDisplayHeight() {
    return getDisplayHeight(CLIENT);
  }

  public static int getDisplayHeight(MinecraftClient client) {
    return client.getWindow().getHeight();
  }

  public static double getScaleFactor() {
    return getScaleFactor(CLIENT);
  }

  public static double getScaleFactor(MinecraftClient client) {
    return client.getWindow().getScaleFactor();
  }

  public static Screen getCurrentScreen() {
    return getCurrentScreen(CLIENT);
  }

  public static Screen getCurrentScreen(MinecraftClient client) {
    return client.currentScreen;
  }

  public static void setScreen(Screen screen) {
    setScreen(CLIENT, screen);
  }

  public static void setScreen(MinecraftClient client, Screen screen) {
    client.setScreen(screen);
  }

  public static void playSoundEvent(SoundEvent soundEvent) {
    playSoundEvent(CLIENT, soundEvent);
  }

  public static void playSoundEvent(MinecraftClient client, SoundEvent soundEvent) {
    client.getSoundManager().play(PositionedSoundInstance.master(soundEvent, 1));
  }

  public static TextRenderer getTextRenderer() {
    return CLIENT.textRenderer;
  }

  public static void drawText(
      DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow
  ) {
    drawText(context, textRenderer, text.asOrderedText(), x, y, color, shadow);
  }

  public static void drawText(
      DrawContext context, TextRenderer textRenderer, OrderedText text, int x, int y, int color, boolean shadow
  ) {
    drawText(context, textRenderer, text, x, y, color, shadow, Alignment.START);
  }

  public static void drawText(
      DrawContext context,
      TextRenderer textRenderer,
      Text text,
      int x,
      int y,
      int color,
      boolean shadow,
      Alignment alignment
  ) {
    drawText(context, textRenderer, text.asOrderedText(), x, y, color, shadow, alignment);
  }

  public static void drawText(
      DrawContext context,
      TextRenderer textRenderer,
      OrderedText text,
      int x,
      int y,
      int color,
      boolean shadow,
      Alignment alignment
  ) {
    context.drawText(textRenderer, text, alignment.getPos(x, textRenderer.getWidth(text)), y, color, shadow);
  }

  public static void drawTruncatedText(
      DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow, int maxWidth
  ) {
    drawTruncatedText(context, textRenderer, text, x, y, color, shadow, maxWidth, Alignment.START);
  }

  public static void drawTruncatedText(
      DrawContext context,
      TextRenderer textRenderer,
      Text text,
      int x,
      int y,
      int color,
      boolean shadow,
      int maxWidth,
      Alignment alignment
  ) {
    if (maxWidth <= 0 || textRenderer.getWidth(text) < maxWidth) {
      drawText(context, textRenderer, text, x, y, color, shadow, alignment);
      return;
    }

    StringVisitable trimmed = text;
    MutableText ellipsis = ScreenTexts.ELLIPSIS.copy().setStyle(text.getStyle());
    trimmed = textRenderer.trimToWidth(text, maxWidth - textRenderer.getWidth(ellipsis));
    trimmed = StringVisitable.concat(trimmed, ellipsis);

    drawText(context, textRenderer, Language.getInstance().reorder(trimmed), x, y, color, shadow, alignment);
  }

  public static void drawWrappedText(
      DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow, int maxWidth
  ) {
    drawWrappedText(context, textRenderer, text, x, y, color, shadow, maxWidth, 0, 0);
  }

  public static void drawWrappedText(
      DrawContext context,
      TextRenderer textRenderer,
      Text text,
      int x,
      int y,
      int color,
      boolean shadow,
      int maxWidth,
      int lineSpacing,
      int maxLines
  ) {
    drawWrappedText(context, textRenderer, text, x, y, color, shadow, maxWidth, maxLines, lineSpacing, Alignment.START);
  }

  public static void drawWrappedText(
      DrawContext context,
      TextRenderer textRenderer,
      Text text,
      int x,
      int y,
      int color,
      boolean shadow,
      int maxWidth,
      int maxLines,
      int lineSpacing,
      Alignment alignment
  ) {
    if (maxWidth <= 0 || textRenderer.getWidth(text) < maxWidth) {
      drawText(context, textRenderer, text, x, y, color, shadow, alignment);
      return;
    }

    List<OrderedText> lines = textRenderer.wrapLines(text, maxWidth);
    int yCursor = y;
    for (OrderedText line : lines.subList(0, Math.min(lines.size(), maxLines))) {
      drawText(context, textRenderer, line, x, yCursor, color, shadow, alignment);
      yCursor += textRenderer.fontHeight + lineSpacing;
    }
  }

  public static int measureWrappedTextHeight(
      TextRenderer textRenderer, Text text, int maxWidth, int maxLines, int lineSpacing
  ) {
    if (maxWidth <= 0) {
      return textRenderer.fontHeight;
    }

    List<OrderedText> lines = textRenderer.wrapLines(text, maxWidth);
    if (lines.size() <= 1) {
      return textRenderer.fontHeight;
    }

    int lineCount = Math.min(lines.size(), maxLines);
    return textRenderer.fontHeight * lineCount + lineSpacing * (lineCount - 1);
  }

  public static void drawScrollingText(
      DrawContext context,
      TextRenderer textRenderer,
      Text text,
      int x,
      int y,
      int color,
      boolean shadow,
      int maxWidth,
      int margin,
      Alignment alignment
  ) {
    int textWidth = textRenderer.getWidth(text);
    if (maxWidth <= 0 || textWidth < maxWidth) {
      drawText(context, textRenderer, text, x, y, color, shadow, alignment);
      return;
    }

    int left = alignment.getPos(x, maxWidth);

    // Based largely on the scrolling text from ClickableWidget.
    double X = (double) textWidth - maxWidth + 2 * margin;
    double t = Util.getMeasuringTimeMs() / 1000.0;
    double T = Math.max(X / 2, 3);
    double c = Math.sin((Math.PI / 2) * Math.cos(2 * Math.PI * t / T)) / 2 + 0.5;
    double dx = c * X;

    context.enableScissor(left, y - textRenderer.fontHeight, left + maxWidth, y + 2 * textRenderer.fontHeight);
    drawText(context, textRenderer, text, left - (int) dx + margin, y, color, shadow, Alignment.START);
    context.disableScissor();
  }

  public static int genColorInt(float r, float g, float b) {
    return genColorInt(r, g, b, 1f);
  }

  public static int genColorInt(float r, float g, float b, float a) {
    return ((int) (a * 255) << 24) | ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
  }

  public static void playClickSound() {
    playClickSound(1f);
  }

  public static void playClickSound(float volume) {
    CLIENT.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, volume));
  }
}
