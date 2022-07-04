package me.roundaround.roundalib.config.gui.screen;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.roundalib.config.gui.GuiUtil;
import me.roundaround.roundalib.config.gui.control.SubScreenControl.SubScreenFactory;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class PositionEditScreen extends Screen {
  private final Screen parent;
  private final PositionConfigOption configOption;

  private PositionEditScreen(Screen parent, PositionConfigOption configOption) {
    super(Text.literal("Example"));
    this.parent = parent;
    this.configOption = configOption;
  }

  public static SubScreenFactory<Position, PositionConfigOption> getSubScreenFactory() {
    return PositionEditScreen::new;
  }

  @Override
  public void close() {
    if (client == null) {
      return;
    }
    client.setScreen(parent);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    switch (keyCode) {
      case GLFW.GLFW_KEY_ESCAPE:
        close();
        return true;
      case GLFW.GLFW_KEY_ENTER:
        commitValueToConfig();
        close();
        return true;
      case GLFW.GLFW_KEY_UP:
        return true;
      case GLFW.GLFW_KEY_DOWN:
        return true;
      case GLFW.GLFW_KEY_LEFT:
        return true;
      case GLFW.GLFW_KEY_RIGHT:
        return true;
    }

    return false;
  }

  @Override
  public void tick() {

  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderBackground(matrixStack, mouseX, mouseY, partialTicks);
    renderContent(matrixStack, mouseX, mouseY, partialTicks);
    renderHelp(matrixStack, mouseX, mouseY, partialTicks);
    renderOverlay(matrixStack, mouseX, mouseY, partialTicks);
  }

  protected void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
    RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND_TEXTURE);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
    bufferBuilder
        .vertex(0, height, 0)
        .texture(0, height / 32f)
        .color(64, 64, 64, 255)
        .next();
    bufferBuilder
        .vertex(width, height, 0)
        .texture(width / 32f, height / 32f)
        .color(64, 64, 64, 255)
        .next();
    bufferBuilder
        .vertex(width, 0, 0)
        .texture(width / 32f, 0)
        .color(64, 64, 64, 255)
        .next();
    bufferBuilder
        .vertex(0, 0, 0)
        .texture(0, 0)
        .color(64, 64, 64, 255)
        .next();
    tessellator.draw();

    RenderSystem.disableTexture();
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.setShader(GameRenderer::getPositionColorShader);
    bufferBuilder = tessellator.getBuffer();

    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
    bufferBuilder
        .vertex(0, height, 0)
        .color(0, 0, 0, 10)
        .next();
    bufferBuilder
        .vertex(width, height, 0)
        .color(0, 0, 0, 10)
        .next();
    bufferBuilder
        .vertex(width, 0, 0)
        .color(0, 0, 0, 10)
        .next();
    bufferBuilder
        .vertex(0, 0, 0)
        .color(0, 0, 0, 10)
        .next();
    tessellator.draw();

    RenderSystem.enableTexture();
    RenderSystem.disableBlend();
  }

  protected void renderContent(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    drawCenteredTextWithShadow(
        matrixStack,
        textRenderer,
        Text.literal("Testing").asOrderedText(),
        width / 2,
        height / 2,
        GuiUtil.LABEL_COLOR);
  }

  protected void renderHelp(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
  }

  protected void renderOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
  }

  private void commitValueToConfig() {

  }
}
