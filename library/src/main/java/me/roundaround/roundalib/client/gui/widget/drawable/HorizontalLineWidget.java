package me.roundaround.roundalib.client.gui.widget.drawable;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

public class HorizontalLineWidget extends DrawableWidget {
  private static final int HEIGHT = 2;
  private static final Identifier TEXTURE_TOP = Screen.HEADER_SEPARATOR_TEXTURE;
  private static final Identifier TEXTURE_BOTTOM = Screen.FOOTER_SEPARATOR_TEXTURE;

  private final Identifier texture;

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
    this.texture = bottom ? TEXTURE_BOTTOM : TEXTURE_TOP;
  }

  @Override
  public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    context.drawGuiTexture(this.texture, this.getX(), this.getY(), this.getWidth(), this.getHeight());
  }
}
