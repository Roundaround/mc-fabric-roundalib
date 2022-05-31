package me.roundaround.roundalib.config.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.roundalib.config.gui.ConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public class GroupTitleWidget extends AbstractWidget<ConfigListWidget> {
  public static final int HEIGHT = 20;
  protected static final int LABEL_COLOR = 0xFFFFFFFF;
  protected static final int ROW_SHADE_STRENGTH = 85;
  protected static final int ROW_SHADE_FADE_WIDTH = 10;
  protected static final int ROW_SHADE_FADE_OVERFLOW = 10;

  protected final Text label;
  protected final int index;

  public GroupTitleWidget(
      ConfigListWidget parent,
      Text label,
      int index,
      int top,
      int left,
      int width) {
    super(parent, top, left, HEIGHT, width);

    this.label = label;
    this.index = index;
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderBackground(matrixStack, mouseX, mouseY, partialTicks);
    renderLabel(matrixStack, mouseX, mouseY, partialTicks);
    renderDecorations(matrixStack, mouseX, mouseY, partialTicks);
  }

  protected void renderBackground(
      MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    if (index % 2 == 0) {
      RenderSystem.disableTexture();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);

      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferBuilder = tessellator.getBuffer();
      Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

      int bgLeft = left - ROW_SHADE_FADE_OVERFLOW;
      int bgRight = right + ROW_SHADE_FADE_OVERFLOW;

      bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      bufferBuilder
          .vertex(matrix4f, bgLeft - 1 + ROW_SHADE_FADE_WIDTH, top - 1, 0)
          .color(0, 0, 0, ROW_SHADE_STRENGTH)
          .next();
      bufferBuilder.vertex(matrix4f, bgLeft - 1, top - 1, 0).color(0, 0, 0, 0).next();
      bufferBuilder.vertex(matrix4f, bgLeft - 1, bottom + 2, 0).color(0, 0, 0, 0).next();
      bufferBuilder
          .vertex(matrix4f, bgLeft - 1 + ROW_SHADE_FADE_WIDTH, bottom + 2, 0)
          .color(0, 0, 0, ROW_SHADE_STRENGTH)
          .next();

      bufferBuilder
          .vertex(matrix4f, bgRight + 2 - ROW_SHADE_FADE_WIDTH, top - 1, 0)
          .color(0, 0, 0, ROW_SHADE_STRENGTH)
          .next();
      bufferBuilder
          .vertex(matrix4f, bgLeft - 1 + ROW_SHADE_FADE_WIDTH, top - 1, 0)
          .color(0, 0, 0, ROW_SHADE_STRENGTH)
          .next();
      bufferBuilder
          .vertex(matrix4f, bgLeft - 1 + ROW_SHADE_FADE_WIDTH, bottom + 2, 0)
          .color(0, 0, 0, ROW_SHADE_STRENGTH)
          .next();
      bufferBuilder
          .vertex(matrix4f, bgRight + 2 - ROW_SHADE_FADE_WIDTH, bottom + 2, 0)
          .color(0, 0, 0, ROW_SHADE_STRENGTH)
          .next();

      bufferBuilder.vertex(matrix4f, bgRight + 2, top - 1, 0).color(0, 0, 0, 0).next();
      bufferBuilder
          .vertex(matrix4f, bgRight + 2 - ROW_SHADE_FADE_WIDTH, top - 1, 0)
          .color(0, 0, 0, ROW_SHADE_STRENGTH)
          .next();
      bufferBuilder
          .vertex(matrix4f, bgRight + 2 - ROW_SHADE_FADE_WIDTH, bottom + 2, 0)
          .color(0, 0, 0, ROW_SHADE_STRENGTH)
          .next();
      bufferBuilder.vertex(matrix4f, bgRight + 2, bottom + 2, 0).color(0, 0, 0, 0).next();
      tessellator.draw();

      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
    }
  }

  protected void renderLabel(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    drawCenteredTextWithShadow(
        matrixStack,
        TEXT_RENDERER,
        label.asOrderedText(),
        left + width / 2,
        top + (height - 8) / 2,
        LABEL_COLOR);
  }

  protected void renderDecorations(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
  }

  public ConfigListWidget getConfigList() {
    return getParent();
  }

  public ConfigScreen getConfigScreen() {
    return getConfigList().getConfigScreen();
  }
}
