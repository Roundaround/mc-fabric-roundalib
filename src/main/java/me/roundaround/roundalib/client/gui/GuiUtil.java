package me.roundaround.roundalib.client.gui;

import me.roundaround.roundalib.client.gui.util.Alignment;
import me.roundaround.roundalib.client.gui.util.Dimensions;
import me.roundaround.roundalib.client.gui.util.IntRect;
import me.roundaround.roundalib.client.gui.util.Spacing;
import me.roundaround.roundalib.mixin.DrawContextAccessor;
import me.roundaround.roundalib.mixin.DrawContextNineSliceAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.Scaling;
import net.minecraft.client.texture.Sprite;
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
  public static final int CROSSHAIR_COLOR = genColorInt(1f, 1f, 1f, 0.7f);
  public static final int TRANSPARENT_COLOR = genColorInt(0f, 0f, 0f, 0f);
  public static final int PADDING = 4;
  public static final int SCROLLBAR_WIDTH = 6;
  public static final int DEFAULT_HEADER_FOOTER_HEIGHT = 33;
  public static final int COMPACT_HEADER_HEIGHT = 17;

  private static MinecraftClient client = null;

  public static MinecraftClient getClient() {
    if (client == null) {
      client = MinecraftClient.getInstance();
    }
    return client;
  }

  public static int getScaledWindowWidth() {
    return getScaledWindowWidth(getClient());
  }

  public static int getScaledWindowWidth(MinecraftClient client) {
    return client.getWindow().getScaledWidth();
  }

  public static int getScaledWindowHeight() {
    return getScaledWindowHeight(getClient());
  }

  public static int getScaledWindowHeight(MinecraftClient client) {
    return client.getWindow().getScaledHeight();
  }

  public static int getDisplayWidth() {
    return getDisplayWidth(getClient());
  }

  public static int getDisplayWidth(MinecraftClient client) {
    return client.getWindow().getWidth();
  }

  public static int getDisplayHeight() {
    return getDisplayHeight(getClient());
  }

  public static int getDisplayHeight(MinecraftClient client) {
    return client.getWindow().getHeight();
  }

  public static double getScaleFactor() {
    return getScaleFactor(getClient());
  }

  public static double getScaleFactor(MinecraftClient client) {
    return client.getWindow().getScaleFactor();
  }

  public static Screen getCurrentScreen() {
    return getCurrentScreen(getClient());
  }

  public static Screen getCurrentScreen(MinecraftClient client) {
    return client.currentScreen;
  }

  public static void setScreen(Screen screen) {
    getClient().setScreen(screen);
  }

  public static void drawText(
      DrawContext context,
      TextRenderer textRenderer,
      Text text,
      int x,
      int y,
      int color,
      boolean shadow) {
    drawText(context, textRenderer, text.asOrderedText(), x, y, color, shadow);
  }

  public static void drawText(
      DrawContext context,
      TextRenderer textRenderer,
      OrderedText text,
      int x,
      int y,
      int color,
      boolean shadow) {
    drawText(context, textRenderer, text, x, y, color, shadow, 0, Alignment.START);
  }

  public static void drawText(
      DrawContext context,
      TextRenderer textRenderer,
      Text text,
      int x,
      int y,
      int color,
      boolean shadow,
      int viewWidth,
      Alignment alignment) {
    drawText(context,
        textRenderer,
        text.asOrderedText(),
        x,
        y,
        color,
        shadow,
        viewWidth,
        alignment);
  }

  public static void drawText(
      DrawContext context,
      TextRenderer textRenderer,
      OrderedText text,
      int x,
      int y,
      int color,
      boolean shadow,
      int viewWidth,
      Alignment alignment) {
    int textWidth = textRenderer.getWidth(text);
    context.drawText(textRenderer,
        text,
        alignment.getPosInContainer(x, viewWidth, textWidth),
        y,
        color,
        shadow);
  }

  public static void drawTruncatedText(
      DrawContext context,
      TextRenderer textRenderer,
      Text text,
      int x,
      int y,
      int color,
      boolean shadow,
      int viewWidth) {
    drawTruncatedText(context, textRenderer, text, x, y, color, shadow, viewWidth, Alignment.START);
  }

  public static void drawTruncatedText(
      DrawContext context,
      TextRenderer textRenderer,
      Text text,
      int x,
      int y,
      int color,
      boolean shadow,
      int viewWidth,
      Alignment alignment) {
    if (textRenderer.getWidth(text) < viewWidth) {
      drawText(context, textRenderer, text, x, y, color, shadow, viewWidth, alignment);
      return;
    }

    MutableText ellipsis = ScreenTexts.ELLIPSIS.copy().setStyle(text.getStyle());
    StringVisitable trimmed =
        textRenderer.trimToWidth(text, viewWidth - textRenderer.getWidth(ellipsis));
    trimmed = StringVisitable.concat(trimmed, ellipsis);

    drawText(context,
        textRenderer,
        Language.getInstance().reorder(trimmed),
        x,
        y,
        color,
        shadow,
        viewWidth,
        alignment);
  }

  public static void drawWrappedText(
      DrawContext context,
      TextRenderer textRenderer,
      Text text,
      int x,
      int y,
      int color,
      boolean shadow,
      int viewWidth) {
    drawWrappedText(context, textRenderer, text, x, y, color, shadow, viewWidth, 0, 0);
  }

  public static void drawWrappedText(
      DrawContext context,
      TextRenderer textRenderer,
      Text text,
      int x,
      int y,
      int color,
      boolean shadow,
      int viewWidth,
      int lineSpacing,
      int maxLines) {
    drawWrappedText(context,
        textRenderer,
        text,
        x,
        y,
        color,
        shadow,
        viewWidth,
        maxLines,
        lineSpacing,
        Alignment.START);
  }

  public static void drawWrappedText(
      DrawContext context,
      TextRenderer textRenderer,
      Text text,
      int x,
      int y,
      int color,
      boolean shadow,
      int viewWidth,
      int maxLines,
      int lineSpacing,
      Alignment alignment) {
    if (textRenderer.getWidth(text) < viewWidth) {
      drawText(context, textRenderer, text, x, y, color, shadow, viewWidth, alignment);
      return;
    }

    List<OrderedText> lines = textRenderer.wrapLines(text, viewWidth);
    int cursorY = y;
    for (OrderedText line : lines.subList(0, Math.min(lines.size(), maxLines))) {
      int lineWidth = textRenderer.getWidth(line);
      int lineX = alignment.getPosInContainer(x, viewWidth, lineWidth);
      drawText(context, textRenderer, line, lineX, cursorY, color, shadow, viewWidth, alignment);
      cursorY += textRenderer.fontHeight + lineSpacing;
    }
  }

  public static Dimensions measureWrappedText(
      TextRenderer textRenderer, Text text, int maxWidth, int maxLines, int lineSpacing) {
    if (maxWidth <= 0) {
      return Dimensions.of(textRenderer.getWidth(text), textRenderer.fontHeight);
    }

    List<OrderedText> lines = textRenderer.wrapLines(text, maxWidth);
    if (lines.size() <= 1) {
      return Dimensions.of(textRenderer.getWidth(text), textRenderer.fontHeight);
    }

    int lineCount = Math.min(lines.size(), maxLines);
    return Dimensions.of(lines.stream().mapToInt(textRenderer::getWidth).max().orElse(0),
        lineCount * textRenderer.fontHeight + (lineCount - 1) * lineSpacing);
  }

  public static void drawScrollingText(
      DrawContext context,
      TextRenderer textRenderer,
      Text text,
      int x,
      int y,
      int color,
      boolean shadow,
      int viewWidth,
      Alignment alignment) {
    int textWidth = textRenderer.getWidth(text);
    if (textWidth < viewWidth) {
      drawText(context, textRenderer, text, x, y, color, shadow, viewWidth, alignment);
      return;
    }

    // Based largely on the scrolling text from ClickableWidget.
    double X = (double) textWidth - viewWidth;
    double t = Util.getMeasuringTimeMs() / 1000.0;
    double T = Math.max(X / 2, 3);
    double c =
        Math.sin((Math.PI / 2) * Math.cos(2 * Math.PI * (t + alignment.floatValue()) / T)) / 2 +
            0.5;
    double dx = c * X;

    context.enableScissor(x,
        y - textRenderer.fontHeight,
        x + viewWidth,
        y + 2 * textRenderer.fontHeight);
    drawText(context, textRenderer, text, x - (int) dx, y, color, shadow);
    context.disableScissor();
  }

  public static int getLineYOffset(TextRenderer textRenderer, int index, int lineSpacing) {
    return index * (textRenderer.fontHeight + lineSpacing);
  }

  public static void fill(DrawContext context, IntRect rect, int color) {
    context.fill(rect.left(), rect.top(), rect.right(), rect.bottom(), color);
  }

  public static void drawBorder(DrawContext context, IntRect rect, int color) {
    drawBorder(context, rect, color, false);
  }

  public static void drawBorder(DrawContext context, IntRect rect, int color, boolean outside) {
    if (outside) {
      rect = rect.expand(1);
    }
    context.drawBorder(rect.left(), rect.top(), rect.getWidth(), rect.getHeight(), color);
  }

  public static Sprite getSprite(Identifier texture) {
    return getClient().getGuiAtlasManager().getSprite(texture);
  }

  public static void drawNineSlice(
      DrawContext context,
      Sprite sprite,
      int x,
      int y,
      int width,
      int height,
      int texWidth,
      int texHeight,
      int border) {
    drawNineSlice(context, sprite, x, y, 0, width, height, texWidth, texHeight, border);
  }

  public static void drawNineSlice(
      DrawContext context,
      Identifier texture,
      int x,
      int y,
      int width,
      int height,
      int texWidth,
      int texHeight,
      int border) {
    drawNineSlice(context, texture, x, y, 0, width, height, texWidth, texHeight, border);
  }

  public static void drawNineSlice(
      DrawContext context,
      Sprite sprite,
      int x,
      int y,
      int z,
      int width,
      int height,
      int texWidth,
      int texHeight,
      int border) {
    drawNineSlice(context, sprite, x, y, z, width, height, texWidth, texHeight, Spacing.of(border));
  }

  public static void drawNineSlice(
      DrawContext context,
      Identifier texture,
      int x,
      int y,
      int z,
      int width,
      int height,
      int texWidth,
      int texHeight,
      int border) {
    drawNineSlice(context,
        texture,
        x,
        y,
        z,
        width,
        height,
        texWidth,
        texHeight,
        Spacing.of(border));
  }

  public static void drawNineSlice(
      DrawContext context,
      Identifier texture,
      int x,
      int y,
      int width,
      int height,
      int texWidth,
      int texHeight,
      Spacing border) {
    drawNineSlice(context, texture, x, y, 0, width, height, texWidth, texHeight, border);
  }

  public static void drawNineSlice(
      DrawContext context,
      Sprite sprite,
      int x,
      int y,
      int width,
      int height,
      int texWidth,
      int texHeight,
      Spacing border) {
    drawNineSlice(context, sprite, x, y, 0, width, height, texWidth, texHeight, border);
  }

  public static void drawNineSlice(
      DrawContext context,
      Identifier texture,
      int x,
      int y,
      int z,
      int width,
      int height,
      int texWidth,
      int texHeight,
      Spacing border) {
    drawNineSlice(context, getSprite(texture), x, y, z, width, height, texWidth, texHeight, border);
  }

  public static void drawNineSlice(
      DrawContext context,
      Sprite sprite,
      int x,
      int y,
      int z,
      int width,
      int height,
      int texWidth,
      int texHeight,
      Spacing border) {
    Scaling.NineSlice nineSlice = new Scaling.NineSlice(texWidth,
        texHeight,
        new Scaling.NineSlice.Border(border.left(), border.top(), border.right(), border.bottom()));
    ((DrawContextNineSliceAccessor) context).invokeDrawSprite(sprite,
        nineSlice,
        x,
        y,
        z,
        width,
        height);
  }

  public static void drawSprite(
      DrawContext context,
      Sprite sprite,
      int i,
      int j,
      int k,
      int l,
      int x,
      int y,
      int z,
      int width,
      int height) {
    ((DrawContextAccessor) context).invokeDrawSprite(sprite, i, j, k, l, x, y, z, width, height);
  }

  public static void enableScissor(DrawContext context, IntRect rect) {
    context.enableScissor(rect.left(), rect.top(), rect.right(), rect.bottom());
  }

  public static void disableScissor(DrawContext context) {
    context.disableScissor();
  }

  public static int genColorInt(float r, float g, float b) {
    return genColorInt(r, g, b, 1f);
  }

  public static int genColorInt(int r, int g, int b) {
    return genColorInt(r, g, b, 255);
  }

  public static int genColorInt(float r, float g, float b, float a) {
    return ((int) (a * 255) << 24) | ((int) (r * 255) << 16) | ((int) (g * 255) << 8) |
        (int) (b * 255);
  }

  public static int genColorInt(int r, int g, int b, int a) {
    return (a << 24) | (r << 16) | (g << 8) | b;
  }

  public static void playSound(SoundManager soundManager, SoundEvent soundEvent, float volume) {
    soundManager.play(PositionedSoundInstance.master(soundEvent, volume));
  }

  public static void playSound(SoundEvent soundEvent, float volume) {
    playSound(getClient().getSoundManager(), soundEvent, volume);
  }

  public static void playClickSound() {
    playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1f);
  }

  public static void playClickSound(SoundManager soundManager) {
    playSound(soundManager, SoundEvents.UI_BUTTON_CLICK.value(), 1f);
  }
}
