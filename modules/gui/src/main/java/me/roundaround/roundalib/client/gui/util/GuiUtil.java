package me.roundaround.roundalib.client.gui.util;

import java.util.List;
import java.util.function.Function;

import org.joml.Matrix4f;

import me.roundaround.roundalib.mixin.DrawContextAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
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
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.Util;

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
      DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow) {
    drawText(context, textRenderer, text.asOrderedText(), x, y, color, shadow);
  }

  public static void drawText(
      DrawContext context, TextRenderer textRenderer, OrderedText text, int x, int y, int color, boolean shadow) {
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
    drawText(context, textRenderer, text.asOrderedText(), x, y, color, shadow, viewWidth, alignment);
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
    context.drawText(textRenderer, text, alignment.getPosInContainer(x, viewWidth, textWidth), y, color, shadow);
  }

  public static void drawTruncatedText(
      DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow,
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
    StringVisitable trimmed = textRenderer.trimToWidth(text, viewWidth - textRenderer.getWidth(ellipsis));
    trimmed = StringVisitable.concat(trimmed, ellipsis);

    drawText(context, textRenderer, Language.getInstance().reorder(trimmed), x, y, color, shadow, viewWidth, alignment);
  }

  public static void drawWrappedText(
      DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow,
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
    drawWrappedText(context, textRenderer, text, x, y, color, shadow, viewWidth, maxLines, lineSpacing,
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
    double c = Math.sin((Math.PI / 2) * Math.cos(2 * Math.PI * (t + alignment.floatValue()) / T)) / 2 + 0.5;
    double dx = c * X;

    context.enableScissor(x, y - textRenderer.fontHeight, x + viewWidth, y + 2 * textRenderer.fontHeight);
    drawText(context, textRenderer, text, x - (int) dx, y, color, shadow);
    context.disableScissor();
  }

  public static int getLineYOffset(TextRenderer textRenderer, int index, int lineSpacing) {
    return index * (textRenderer.fontHeight + lineSpacing);
  }

  public static void fill(DrawContext context, IntRect rect, int color) {
    context.fill(rect.left(), rect.top(), rect.right(), rect.bottom(), color);
  }

  public static void fill(DrawContext context, FloatRect rect, int color) {
    fill(context, rect.left(), rect.top(), rect.right(), rect.bottom(), color);
  }

  public static void fill(DrawContext context, float x1, float y1, float x2, float y2, int color) {
    fill(context, x1, y1, x2, y2, 0, color);
  }

  public static void fill(
      DrawContext context,
      float x1, float y1, float x2, float y2, float z, int color) {
    fill(context, RenderLayer.getGui(), x1, y1, x2, y2, z, color);
  }

  public static void fill(
      DrawContext context, RenderLayer layer,
      float x1, float y1, float x2, float y2, int color) {
    fill(context, layer, x1, y1, x2, y2, 0, color);
  }

  public static void fill(
      DrawContext context, RenderLayer layer,
      float x1, float y1, float x2, float y2, float z, int color) {
    if (x1 < x2) {
      float temp = x1;
      x1 = x2;
      x2 = temp;
    }
    if (y1 < y2) {
      float temp = y1;
      y1 = y2;
      y2 = temp;
    }

    Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
    VertexConsumer vertexConsumer = ((DrawContextAccessor) context).getVertexConsumers().getBuffer(layer);
    vertexConsumer.vertex(matrix4f, x1, y1, z).color(color);
    vertexConsumer.vertex(matrix4f, x1, y2, z).color(color);
    vertexConsumer.vertex(matrix4f, x2, y2, z).color(color);
    vertexConsumer.vertex(matrix4f, x2, y1, z).color(color);
  }

  public static void fillHorizontalGradient(
      DrawContext context, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
    fillHorizontalGradient(context, startX, startY, endX, endY, 0, colorStart, colorEnd);
  }

  public static void fillHorizontalGradient(
      DrawContext context, int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd) {
    fillHorizontalGradient(context, RenderLayer.getGui(), startX, startY, endX, endY, colorStart, colorEnd, z);
  }

  public static void fillHorizontalGradient(
      DrawContext context,
      RenderLayer layer,
      int startX,
      int startY,
      int endX,
      int endY,
      int colorStart,
      int colorEnd,
      int z) {
    VertexConsumer vertexConsumer = ((DrawContextAccessor) context).getVertexConsumers().getBuffer(layer);
    fillHorizontalGradient(context, vertexConsumer, startX, startY, endX, endY, z, colorStart, colorEnd);
  }

  public static void fillHorizontalGradient(
      DrawContext context,
      VertexConsumer vertexConsumer,
      int startX,
      int startY,
      int endX,
      int endY,
      int z,
      int colorStart,
      int colorEnd) {
    Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
    vertexConsumer.vertex(matrix4f, startX, startY, z).color(colorStart);
    vertexConsumer.vertex(matrix4f, startX, endY, z).color(colorStart);
    vertexConsumer.vertex(matrix4f, endX, endY, z).color(colorEnd);
    vertexConsumer.vertex(matrix4f, endX, startY, z).color(colorEnd);
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

  public static void drawBorder(DrawContext context, FloatRect rect, int color) {
    drawBorder(context, rect, color, false);
  }

  public static void drawBorder(DrawContext context, FloatRect rect, int color, boolean outside) {
    if (outside) {
      rect = rect.expand(1);
    }
    drawBorder(context, rect.left(), rect.top(), rect.getWidth(), rect.getHeight(), color);
  }

  public static void drawBorder(DrawContext context, float x, float y, float width, float height, int color) {
    fill(context, x, y, x + width, y + 1, color);
    fill(context, x, y + height - 1, x + width, y + height, color);
    fill(context, x, y + 1, x + 1, y + height - 1, color);
    fill(context, x + width - 1, y + 1, x + width, y + height - 1, color);
  }

  public static Sprite getSprite(Identifier texture) {
    return getClient().getGuiAtlasManager().getSprite(texture);
  }

  public static void drawSpriteNineSliced(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Identifier texture,
      int x,
      int y,
      int width,
      int height,
      int texWidth,
      int texHeight,
      int color,
      int border) {
    drawSpriteNineSliced(
        context, renderLayers, texture, x, y, width, height, texWidth, texHeight, color, Spacing.of(border));
  }

  public static void drawSpriteNineSliced(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Identifier texture,
      int x,
      int y,
      int width,
      int height,
      int texWidth,
      int texHeight,
      int color,
      Spacing border) {
    drawSpriteNineSliced(
        context, renderLayers, getSprite(texture), x, y, width, height, texWidth, texHeight, color, border);
  }

  public static void drawSpriteNineSliced(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Sprite sprite,
      int x,
      int y,
      int width,
      int height,
      int texWidth,
      int texHeight,
      int color,
      Spacing border) {
    Scaling.NineSlice nineSlice = new Scaling.NineSlice(texWidth, texHeight,
        new Scaling.NineSlice.Border(border.left(), border.top(), border.right(), border.bottom()), false);
    ((DrawContextAccessor) context).invokeDrawSpriteNineSliced(
        renderLayers, sprite, nineSlice, x, y, width, height, color);
  }

  public static void drawSpriteNineSliced(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Identifier texture,
      float x,
      float y,
      float width,
      float height,
      int texWidth,
      int texHeight,
      int color,
      int border) {
    drawSpriteNineSliced(
        context, renderLayers, texture, x, y, width, height, texWidth, texHeight, color, Spacing.of(border));
  }

  public static void drawSpriteNineSliced(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Identifier texture,
      float x,
      float y,
      float width,
      float height,
      int texWidth,
      int texHeight,
      int color,
      Spacing border) {
    drawSpriteNineSliced(
        context, renderLayers, getSprite(texture), x, y, width, height, texWidth, texHeight, color, border);
  }

  public static void drawSpriteNineSliced(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Sprite sprite,
      float x,
      float y,
      float width,
      float height,
      int texWidth,
      int texHeight,
      int color,
      Spacing border) {
    Scaling.NineSlice nineSlice = new Scaling.NineSlice(texWidth, texHeight,
        new Scaling.NineSlice.Border(border.left(), border.top(), border.right(), border.bottom()), false);
    drawSpriteNineSliced(
        context, renderLayers, sprite, nineSlice, x, y, width, height, color);
  }

  public static void drawSpriteNineSliced(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Sprite sprite,
      Scaling.NineSlice nineSlice,
      float x,
      float y,
      float width,
      float height,
      int color) {
    Scaling.NineSlice.Border border = nineSlice.border();
    float left = Math.min(border.left(), width / 2);
    float right = Math.min(border.right(), width / 2);
    float top = Math.min(border.top(), height / 2);
    float bottom = Math.min(border.bottom(), height / 2);

    if (width == nineSlice.width() && height == nineSlice.height()) {
      drawSpriteRegion(
          context,
          renderLayers,
          sprite,
          nineSlice.width(), nineSlice.height(),
          0, 0,
          x, y,
          width, height,
          color);
    } else if (height == nineSlice.height()) {
      drawSpriteSliced3x1(
          context, renderLayers, sprite, nineSlice,
          x, y, width, height, color, left, right);
    } else if (width == nineSlice.width()) {
      drawSpriteSliced1x3(
          context, renderLayers, sprite, nineSlice,
          x, y, width, height, color, top, bottom);
    } else {
      drawSpriteSliced3x3(
          context, renderLayers, sprite, nineSlice,
          x, y, width, height, color, left, right, top, bottom);
    }
  }

  private static void drawSpriteSliced3x1(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Sprite sprite,
      Scaling.NineSlice nineSlice,
      float x,
      float y,
      float width,
      float height,
      int color,
      float left,
      float right) {
    drawSpriteRegion(
        context,
        renderLayers,
        sprite,
        nineSlice.width(), nineSlice.height(),
        0, 0,
        x, y,
        left, height,
        color);
    drawSpriteRegion(
        context,
        renderLayers,
        nineSlice,
        sprite,
        x + left, y,
        width - right - left, height,
        left, 0,
        nineSlice.width() - right - left, nineSlice.height(),
        nineSlice.width(), nineSlice.height(),
        color);
    drawSpriteRegion(
        context,
        renderLayers,
        sprite,
        nineSlice.width(), nineSlice.height(),
        nineSlice.width() - right, 0,
        x + width - right, y,
        right, height,
        color);
  }

  private static void drawSpriteSliced1x3(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Sprite sprite,
      Scaling.NineSlice nineSlice,
      float x,
      float y,
      float width,
      float height,
      int color,
      float top,
      float bottom) {
    drawSpriteRegion(
        context,
        renderLayers,
        sprite,
        nineSlice.width(), nineSlice.height(),
        0, 0,
        x, y,
        width, top,
        color);
    drawSpriteRegion(
        context,
        renderLayers,
        nineSlice,
        sprite,
        x, y + top,
        width, height - bottom - top,
        0, top,
        nineSlice.width(), nineSlice.height() - bottom - top,
        nineSlice.width(), nineSlice.height(),
        color);
    drawSpriteRegion(
        context,
        renderLayers,
        sprite,
        nineSlice.width(), nineSlice.height(),
        0, nineSlice.height() - bottom,
        x, y + height - bottom,
        width, bottom,
        color);
  }

  private static void drawSpriteSliced3x3(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Sprite sprite,
      Scaling.NineSlice nineSlice,
      float x,
      float y,
      float width,
      float height,
      int color,
      float left,
      float right,
      float top,
      float bottom) {
    drawSpriteRegion(
        context,
        renderLayers,
        sprite,
        nineSlice.width(), nineSlice.height(),
        0, 0,
        x, y,
        left, top,
        color);
    drawSpriteRegion(
        context,
        renderLayers,
        nineSlice,
        sprite,
        x + left, y,
        width - right - left, top,
        left, 0,
        nineSlice.width() - right - left, top,
        nineSlice.width(), nineSlice.height(),
        color);
    drawSpriteRegion(
        context,
        renderLayers,
        sprite,
        nineSlice.width(), nineSlice.height(),
        nineSlice.width() - right, 0,
        x + width - right, y,
        right, top,
        color);
    drawSpriteRegion(
        context,
        renderLayers,
        sprite,
        nineSlice.width(), nineSlice.height(),
        0, nineSlice.height() - bottom,
        x, y + height - bottom,
        left, bottom,
        color);
    drawSpriteRegion(
        context,
        renderLayers,
        nineSlice,
        sprite,
        x + left, y + height - bottom,
        width - right - left, bottom,
        left, nineSlice.height() - bottom,
        nineSlice.width() - right - left, bottom,
        nineSlice.width(), nineSlice.height(),
        color);
    drawSpriteRegion(
        context,
        renderLayers,
        sprite,
        nineSlice.width(), nineSlice.height(),
        nineSlice.width() - right, nineSlice.height() - bottom,
        x + width - right, y + height - bottom,
        right, bottom,
        color);
    drawSpriteRegion(
        context,
        renderLayers,
        nineSlice,
        sprite,
        x, y + top,
        left, height - bottom - top,
        0, top,
        left, nineSlice.height() - bottom - top,
        nineSlice.width(), nineSlice.height(),
        color);
    drawSpriteRegion(
        context,
        renderLayers,
        nineSlice,
        sprite,
        x + left, y + top,
        width - right - left, height - bottom - top,
        left, top,
        nineSlice.width() - right - left, nineSlice.height() - bottom - top,
        nineSlice.width(), nineSlice.height(),
        color);
    drawSpriteRegion(
        context,
        renderLayers,
        nineSlice,
        sprite,
        x + width - right, y + top,
        right, height - bottom - top,
        nineSlice.width() - right, top,
        right, nineSlice.height() - bottom - top,
        nineSlice.width(), nineSlice.height(),
        color);
  }

  private static void drawSpriteRegion(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Scaling.NineSlice nineSlice,
      Sprite sprite,
      float x,
      float y,
      float width,
      float height,
      float u,
      float v,
      float tileWidth,
      float tileHeight,
      int textureWidth,
      int textureHeight,
      int color) {
    if (width > 0 && height > 0) {
      if (nineSlice.stretchInner()) {
        drawTexturedQuad(
            context,
            renderLayers,
            sprite.getAtlasId(),
            x,
            x + width,
            y,
            y + height,
            sprite.getFrameU(u / textureWidth),
            sprite.getFrameU((u + tileWidth) / textureWidth),
            sprite.getFrameV(v / textureHeight),
            sprite.getFrameV((v + tileHeight) / textureHeight),
            color);
      } else {
        drawSpriteTiled(
            context,
            renderLayers,
            sprite,
            x,
            y,
            width,
            height,
            u,
            v,
            tileWidth,
            tileHeight,
            textureWidth,
            textureHeight,
            color);
      }
    }
  }

  private static void drawSpriteTiled(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Sprite sprite,
      float x,
      float y,
      float width,
      float height,
      float u,
      float v,
      float tileWidth,
      float tileHeight,
      int textureWidth,
      int textureHeight,
      int color) {
    if (width > 0 && height > 0) {
      if (tileWidth > 0 && tileHeight > 0) {
        for (float dx = 0; dx < width; dx += tileWidth) {
          float x2 = Math.min(tileWidth, width - dx);

          for (float dy = 0; dy < height; dy += tileHeight) {
            float y2 = Math.min(tileHeight, height - dy);
            drawSpriteRegion(
                context,
                renderLayers,
                sprite,
                textureWidth,
                textureHeight,
                u,
                v,
                x + dx,
                y + dy,
                x2,
                y2,
                color);
          }
        }
      } else {
        throw new IllegalArgumentException(
            "Tiled sprite texture size must be positive, got " + tileWidth + "x" + tileHeight);
      }
    }
  }

  public static void drawSpriteRegion(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Sprite sprite,
      int textureWidth,
      int textureHeight,
      int u,
      int v,
      int x,
      int y,
      int width,
      int height,
      int color) {
    ((DrawContextAccessor) context).invokeDrawSpriteRegion(
        renderLayers, sprite, textureWidth, textureHeight, u, v, x, y, width, height, color);
  }

  public static void drawSpriteRegion(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Sprite sprite,
      int textureWidth,
      int textureHeight,
      float u,
      float v,
      float x,
      float y,
      float width,
      float height,
      int color) {
    if (width == 0 || height == 0) {
      return;
    }

    drawTexturedQuad(
        context,
        renderLayers,
        sprite.getAtlasId(),
        x,
        x + width,
        y,
        y + height,
        sprite.getFrameU(u / textureWidth),
        sprite.getFrameU((u + width) / textureWidth),
        sprite.getFrameV(v / textureHeight),
        sprite.getFrameV((v + height) / textureHeight),
        color);
  }

  public static void drawTexturedQuad(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Identifier sprite,
      int x1,
      int x2,
      int y1,
      int y2) {
    drawTexturedQuad(
        context, renderLayers, sprite,
        x1, x2, y1, y2,
        0, 1, 0, 1);
  }

  public static void drawTexturedQuad(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Identifier sprite,
      int x1,
      int x2,
      int y1,
      int y2,
      float u1,
      float u2,
      float v1,
      float v2) {
    drawTexturedQuad(
        context, renderLayers, sprite,
        x1, x2, y1, y2,
        u1, u2, v1, v2,
        Colors.WHITE);
  }

  public static void drawTexturedQuad(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Identifier sprite,
      int x1,
      int x2,
      int y1,
      int y2,
      int color) {
    drawTexturedQuad(
        context, renderLayers, sprite,
        x1, x2, y1, y2,
        0, 1, 0, 1,
        color);
  }

  public static void drawTexturedQuad(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Identifier sprite,
      int x1,
      int x2,
      int y1,
      int y2,
      float u1,
      float u2,
      float v1,
      float v2,
      int color) {
    drawTexturedQuad(
        context, renderLayers, sprite,
        (float) x1, (float) x2, (float) y1, (float) y2,
        u1, u2, v1, v2,
        color);
  }

  public static void drawTexturedQuad(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Identifier sprite,
      float x1,
      float x2,
      float y1,
      float y2) {
    drawTexturedQuad(
        context, renderLayers, sprite,
        x1, x2, y1, y2,
        0, 1, 0, 1);
  }

  public static void drawTexturedQuad(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Identifier sprite,
      float x1,
      float x2,
      float y1,
      float y2,
      float u1,
      float u2,
      float v1,
      float v2) {
    drawTexturedQuad(
        context, renderLayers, sprite,
        x1, x2, y1, y2,
        u1, u2, v1, v2,
        Colors.WHITE);
  }

  public static void drawTexturedQuad(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Identifier sprite,
      float x1,
      float x2,
      float y1,
      float y2,
      int color) {
    drawTexturedQuad(
        context, renderLayers, sprite,
        x1, x2, y1, y2,
        0, 1, 0, 1,
        color);
  }

  public static void drawTexturedQuad(
      DrawContext context,
      Function<Identifier, RenderLayer> renderLayers,
      Identifier sprite,
      float x1,
      float x2,
      float y1,
      float y2,
      float u1,
      float u2,
      float v1,
      float v2,
      int color) {
    RenderLayer renderLayer = (RenderLayer) renderLayers.apply(sprite);
    Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
    VertexConsumer vertexConsumer = ((DrawContextAccessor) context).getVertexConsumers().getBuffer(renderLayer);
    vertexConsumer.vertex(matrix4f, x1, y1, 0.0F).texture(u1, v1).color(color);
    vertexConsumer.vertex(matrix4f, x1, y2, 0.0F).texture(u1, v2).color(color);
    vertexConsumer.vertex(matrix4f, x2, y2, 0.0F).texture(u2, v2).color(color);
    vertexConsumer.vertex(matrix4f, x2, y1, 0.0F).texture(u2, v1).color(color);
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
    return ((int) (a * 255) << 24) | ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
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
