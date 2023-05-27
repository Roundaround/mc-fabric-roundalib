package me.roundaround.roundalib.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.roundalib.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;

import java.util.List;

import static net.minecraft.client.gui.screen.Screen.OPTIONS_BACKGROUND_TEXTURE;

public class GuiUtil {
  public static int LABEL_COLOR = genColorInt(1f, 1f, 1f);
  public static int ERROR_COLOR = genColorInt(1f, 0.15f, 0.15f);
  public static int BACKGROUND_COLOR = genColorInt(0f, 0f, 0f, 0.5f);
  public static int PADDING = 4;
  public static int SCROLLBAR_WIDTH = 6;

  private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

  public static Identifier getWidgetsTexture(ModConfig config) {
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

  public static void renderInScissor(
      int x, int y, int width, int height, Runnable render) {
    renderInScissor(CLIENT, x, y, width, height, render);
  }

  public static void renderInScissor(
      MinecraftClient client, int x, int y, int width, int height, Runnable render) {
    Screen currentScreen = getCurrentScreen(client);
    if (currentScreen == null) {
      render.run();
    }

    int scissorLeft = x;
    int scissorTop = client.getWindow().getHeight() - (y + height);
    int scissorWidth = width;
    int scissorHeight = height;

    double scaleFactor = getScaleFactor(client);
    scissorLeft = MathHelper.floor(scissorLeft * scaleFactor);
    scissorTop = MathHelper.floor(scissorTop * scaleFactor);
    scissorWidth = MathHelper.ceil(scissorWidth * scaleFactor);
    scissorHeight = MathHelper.ceil(scissorHeight * scaleFactor);

    RenderSystem.enableScissor(scissorLeft, scissorTop, scissorWidth, scissorHeight);
    render.run();
    RenderSystem.disableScissor();
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

  public static void playSoundEvent(
      MinecraftClient client, SoundEvent soundEvent) {
    client.getSoundManager().play(PositionedSoundInstance.master(soundEvent, 1));
  }

  public static TextRenderer getTextRenderer() {
    return CLIENT.textRenderer;
  }

  public static void drawTruncatedCenteredTextWithShadow(
      DrawContext drawContext,
      TextRenderer textRenderer,
      Text text,
      int x,
      int y,
      int color,
      int maxWidth) {
    StringVisitable trimmed = text;
    if (textRenderer.getWidth(text) > maxWidth) {
      StringVisitable ellipsis = StringVisitable.plain("...");

      trimmed = StringVisitable.concat(textRenderer.trimToWidth(text,
          maxWidth - textRenderer.getWidth(ellipsis)), ellipsis);
    }

    drawContext.drawCenteredTextWithShadow(textRenderer,
        Language.getInstance().reorder(trimmed),
        x,
        y,
        color);
  }

  public static void drawWrappedCenteredTextWithShadow(
      DrawContext drawContext,
      TextRenderer textRenderer,
      Text text,
      int x,
      int y,
      int color,
      int maxWidth) {
    List<OrderedText> lines = textRenderer.wrapLines(text, maxWidth);
    int yCursor = y;
    for (OrderedText line : lines) {
      drawContext.drawCenteredTextWithShadow(textRenderer, line, x, yCursor, color);
      yCursor += textRenderer.fontHeight;
    }
  }

  public static void renderBackgroundInRegion(
      int brightness, int top, int bottom, int left, int right) {
    renderBackgroundInRegion(brightness, top, bottom, left, right, 0, 0);
  }

  public static void renderBackgroundInRegion(
      int brightness, int top, int bottom, int left, int right, double offsetX, double offsetY) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
    RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND_TEXTURE);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
    bufferBuilder.vertex(left, bottom, 0)
        .texture((float) (left + Math.round(offsetX)) / 32f,
            (float) (bottom + Math.round(offsetY)) / 32f)
        .color(brightness, brightness, brightness, 255)
        .next();
    bufferBuilder.vertex(right, bottom, 0)
        .texture((float) (right + Math.round(offsetX)) / 32f,
            (float) (bottom + Math.round(offsetY)) / 32f)
        .color(brightness, brightness, brightness, 255)
        .next();
    bufferBuilder.vertex(right, top, 0)
        .texture((float) (right + Math.round(offsetX)) / 32f,
            (float) (top + Math.round(offsetY)) / 32f)
        .color(brightness, brightness, brightness, 255)
        .next();
    bufferBuilder.vertex(left, top, 0)
        .texture((float) (left + Math.round(offsetX)) / 32f,
            (float) (top + Math.round(offsetY)) / 32f)
        .color(brightness, brightness, brightness, 255)
        .next();
    tessellator.draw();
  }

  public static int genColorInt(float r, float g, float b) {
    return genColorInt(r, g, b, 1f);
  }

  public static int genColorInt(float r, float g, float b, float a) {
    return ((int) (a * 255) << 24) | ((int) (r * 255) << 16) | ((int) (g * 255) << 8) |
        (int) (b * 255);
  }
}
