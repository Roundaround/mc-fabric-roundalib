package me.roundaround.roundalib.config.gui.screen;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.roundalib.config.gui.GuiUtil;
import me.roundaround.roundalib.config.option.ConfigOption;
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
public abstract class ConfigOptionSubScreen<D, C extends ConfigOption<D, ?>> extends Screen {
  protected static final int DARKEN_STRENGTH = 120;

  protected final Screen parent;
  protected final C configOption;

  private D value;

  protected ConfigOptionSubScreen(Text title, Screen parent, C configOption) {
    super(title);
    this.parent = parent;
    this.configOption = configOption;
    value = configOption.getValue();
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
      case GLFW.GLFW_KEY_R:
        if (GuiUtil.isShiftHeld()) {
          setValue(configOption.getDefault());
          return true;
        }
    }

    return false;
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderBackground(matrixStack, mouseX, mouseY, partialTicks);
    renderContent(matrixStack, mouseX, mouseY, partialTicks);
    renderHelp(matrixStack, mouseX, mouseY, partialTicks);
    renderOverlay(matrixStack, mouseX, mouseY, partialTicks);
  }

  protected void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderTextureBackground(matrixStack, mouseX, mouseY, partialTicks);
  }

  protected void renderTextureBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
    RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND_TEXTURE);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
    bufferBuilder
        .vertex(0, height, 0)
        .texture(0, height / 32f)
        .color(32, 32, 32, 255)
        .next();
    bufferBuilder
        .vertex(width, height, 0)
        .texture(width / 32f, height / 32f)
        .color(32, 32, 32, 255)
        .next();
    bufferBuilder
        .vertex(width, 0, 0)
        .texture(width / 32f, 0)
        .color(32, 32, 32, 255)
        .next();
    bufferBuilder
        .vertex(0, 0, 0)
        .texture(0, 0)
        .color(32, 32, 32, 255)
        .next();
    tessellator.draw();
  }

  protected void renderDarkenBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    RenderSystem.disableTexture();
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.setShader(GameRenderer::getPositionColorShader);

    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
    bufferBuilder
        .vertex(0, height, 0)
        .color(0, 0, 0, DARKEN_STRENGTH)
        .next();
    bufferBuilder
        .vertex(width, height, 0)
        .color(0, 0, 0, DARKEN_STRENGTH)
        .next();
    bufferBuilder
        .vertex(width, 0, 0)
        .color(0, 0, 0, DARKEN_STRENGTH)
        .next();
    bufferBuilder
        .vertex(0, 0, 0)
        .color(0, 0, 0, DARKEN_STRENGTH)
        .next();
    tessellator.draw();
  }

  protected void renderContent(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    drawCenteredText(matrixStack, textRenderer, title, width / 2, 17, GuiUtil.LABEL_COLOR);
  }

  protected void renderHelp(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    if (GuiUtil.isShiftHeld()) {
      renderHelpExpanded(matrixStack, mouseX, mouseY, partialTicks);
    } else {
      renderHelpPrompt(matrixStack, mouseX, mouseY, partialTicks);
    }
  }

  protected void renderHelpPrompt(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderHelpLines(matrixStack, getHelpShort(mouseX, mouseY, partialTicks));
  }

  protected void renderHelpExpanded(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderHelpLines(matrixStack, getHelpLong(mouseX, mouseY, partialTicks));
  }

  private void renderHelpLines(MatrixStack matrixStack, List<Text> lines) {
    int startingOffset = height - 4 - textRenderer.fontHeight
        - (lines.size() - 1) * (textRenderer.fontHeight + 2);

    for (int i = 0; i < lines.size(); i++) {
      drawTextWithShadow(
          matrixStack,
          textRenderer,
          lines.get(i),
          4,
          startingOffset + i * (textRenderer.fontHeight + 2),
          GuiUtil.LABEL_COLOR);
    }
  }

  protected List<Text> getHelpShort(int mouseX, int mouseY, float partialTicks) {
    return List.of(Text.literal("Hold shift for help"));
  }

  protected List<Text> getHelpLong(int mouseX, int mouseY, float partialTicks) {
    return List.of(
        Text.literal("ESCAPE: cancel and discard changes"),
        Text.literal("ENTER/RETURN: save changes"),
        Text.literal("SHIFT+R: reset value to default"));
  }

  protected void renderOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
  }

  protected void setValue(D value) {
    this.value = value;
  }

  protected D getValue() {
    return value;
  }

  protected boolean isDirty() {
    return value == null ? configOption.getValue() != null : !value.equals(configOption.getValue());
  }

  protected void commitValueToConfig() {
    configOption.setValue(value);
  }
}
