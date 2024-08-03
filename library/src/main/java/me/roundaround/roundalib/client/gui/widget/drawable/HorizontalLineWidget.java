package me.roundaround.roundalib.client.gui.widget.drawable;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

public class HorizontalLineWidget extends DrawableWidget {
  private static final int HEIGHT = 2;

  private final Identifier texture;

  private int marginTop;
  private int marginBottom;

  public HorizontalLineWidget() {
    this(0);
  }

  public HorizontalLineWidget(int width) {
    this(width, false);
  }

  public HorizontalLineWidget(int width, boolean bottom) {
    this(0, 0, width, bottom);
  }

  public HorizontalLineWidget(int x, int y, int width, boolean bottom) {
    super(x, y, width, HEIGHT);
    this.texture = bottom ? Screen.FOOTER_SEPARATOR_TEXTURE : Screen.HEADER_SEPARATOR_TEXTURE;
  }

  public static HorizontalLineWidget ofWidth(int width) {
    return new HorizontalLineWidget(width);
  }

  public static HorizontalLineWidget ofWidth(int width, boolean bottom) {
    return new HorizontalLineWidget(width, bottom);
  }

  public static HorizontalLineWidget ofWidthBottom(int width) {
    return new HorizontalLineWidget(width, true);
  }

  @Override
  public int getHeight() {
    return this.height + this.marginTop + this.marginBottom;
  }

  public HorizontalLineWidget margin(int marginTop, int marginBottom) {
    this.marginTop = marginTop;
    this.marginBottom = marginBottom;
    return this;
  }

  public HorizontalLineWidget marginTop(int marginTop) {
    this.marginTop = marginTop;
    return this;
  }

  public HorizontalLineWidget marginBottom(int marginBottom) {
    this.marginBottom = marginBottom;
    return this;
  }

  @Override
  public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    RenderSystem.enableBlend();
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    context.drawTexture(
        this.texture, this.getX(), this.getY() + this.marginTop, 0, 0, this.width, this.height, 32, HEIGHT);
  }
}
