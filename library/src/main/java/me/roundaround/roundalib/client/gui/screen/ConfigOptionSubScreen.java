package me.roundaround.roundalib.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.config.RoundaLibIconButtons;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public abstract class ConfigOptionSubScreen<D, O extends ConfigOption<D, ?>> extends Screen {
  protected static final int DARKEN_STRENGTH = 120;

  protected final Screen parent;
  protected final O configOption;
  protected final O workingCopy;
  protected final String modId;

  @SuppressWarnings("unchecked")
  protected ConfigOptionSubScreen(Text title, Screen parent, O configOption) {
    super(title);
    this.parent = parent;
    this.configOption = configOption;
    this.workingCopy = (O) configOption.createWorkingCopy();
    this.modId = configOption.getConfig().getModId();
  }

  @Override
  protected void init() {
    this.addSelectableChild(RoundaLibIconButtons.resetButton(
        this.width - 3 * (GuiUtil.PADDING + RoundaLibIconButtons.SIZE_M),
        this.height - GuiUtil.PADDING - RoundaLibIconButtons.SIZE_M,
        this.workingCopy));

    this.addSelectableChild(RoundaLibIconButtons.discardButton(
        this.width - 2 * (GuiUtil.PADDING + RoundaLibIconButtons.SIZE_M),
        this.height - GuiUtil.PADDING - RoundaLibIconButtons.SIZE_M,
        this.configOption.getConfig().getModId(),
        (button) -> this.discardAndExit()));

    this.addSelectableChild(RoundaLibIconButtons.saveButton(
        this.width - GuiUtil.PADDING - RoundaLibIconButtons.SIZE_M,
        this.height - GuiUtil.PADDING - RoundaLibIconButtons.SIZE_M,
        this.configOption.getConfig().getModId(),
        (button) -> this.saveAndExit()));
  }

  @Override
  public void close() {
    if (this.client == null) {
      return;
    }
    this.client.setScreen(this.parent);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    switch (keyCode) {
      case GLFW.GLFW_KEY_ESCAPE:
        this.discardAndExit();
        return true;
      case GLFW.GLFW_KEY_S:
        if (Screen.hasControlDown()) {
          this.saveAndExit();
          return true;
        }
      case GLFW.GLFW_KEY_R:
        if (Screen.hasControlDown()) {
          this.resetToDefault();
          return true;
        }
    }

    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
    this.renderContent(matrixStack, mouseX, mouseY, partialTicks);
    this.renderHelp(matrixStack, mouseX, mouseY, partialTicks);
  }

  protected void renderBackground(
      MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    if (parent == null) {
      this.renderTextureBackground(matrixStack, mouseX, mouseY, partialTicks);
    } else {
      this.renderDarkenBackground(matrixStack, mouseX, mouseY, partialTicks);
    }
  }

  protected void renderTextureBackground(
      MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
    RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND_TEXTURE);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
    bufferBuilder.vertex(0, height, 0).texture(0, height / 32f).color(64, 64, 64, 255).next();
    bufferBuilder.vertex(width, height, 0)
        .texture(width / 32f, height / 32f)
        .color(64, 64, 64, 255)
        .next();
    bufferBuilder.vertex(width, 0, 0).texture(width / 32f, 0).color(64, 64, 64, 255).next();
    bufferBuilder.vertex(0, 0, 0).texture(0, 0).color(64, 64, 64, 255).next();
    tessellator.draw();
  }

  protected void renderDarkenBackground(
      MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.disableDepthTest();
    RenderSystem.colorMask(true, true, true, false);
    RenderSystem.setShader(GameRenderer::getPositionColorProgram);

    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
    bufferBuilder.vertex(0, height, 0).color(0, 0, 0, DARKEN_STRENGTH).next();
    bufferBuilder.vertex(width, height, 0).color(0, 0, 0, DARKEN_STRENGTH).next();
    bufferBuilder.vertex(width, 0, 0).color(0, 0, 0, DARKEN_STRENGTH).next();
    bufferBuilder.vertex(0, 0, 0).color(0, 0, 0, DARKEN_STRENGTH).next();
    tessellator.draw();

    RenderSystem.disableBlend();
    RenderSystem.colorMask(true, true, true, true);
    RenderSystem.enableDepthTest();
  }

  protected void renderContent(
      MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    drawCenteredTextWithShadow(matrixStack,
        textRenderer,
        title,
        width / 2,
        17,
        GuiUtil.LABEL_COLOR);

    this.children().forEach((child) -> {
      if (child instanceof Drawable) {
        ((Drawable) child).render(matrixStack, mouseX, mouseY, partialTicks);
      }
    });
  }

  protected void renderHelp(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    if (hasShiftDown()) {
      this.renderHelpExpanded(matrixStack, mouseX, mouseY, partialTicks);
    } else {
      this.renderHelpPrompt(matrixStack, mouseX, mouseY, partialTicks);
    }
  }

  protected void renderHelpPrompt(
      MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.renderHelpLines(matrixStack, getHelpShort(mouseX, mouseY, partialTicks));
  }

  protected void renderHelpExpanded(
      MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.renderHelpLines(matrixStack, getHelpLong(mouseX, mouseY, partialTicks));
  }

  private void renderHelpLines(MatrixStack matrixStack, List<Text> lines) {
    this.renderHelpLines(matrixStack, lines, false);
  }

  private void renderHelpLines(MatrixStack matrixStack, List<Text> lines, boolean offsetForIcon) {
    int startingOffset =
        height - 4 - textRenderer.fontHeight - (lines.size() - 1) * (textRenderer.fontHeight + 2);

    for (int i = 0; i < lines.size(); i++) {
      drawTextWithShadow(matrixStack,
          textRenderer,
          lines.get(i),
          4,
          startingOffset + i * (textRenderer.fontHeight + 2),
          GuiUtil.LABEL_COLOR);
    }
  }

  protected List<Text> getHelpShort(int mouseX, int mouseY, float partialTicks) {
    return List.of(Text.translatable(this.modId + ".roundalib.help.short"));
  }

  protected List<Text> getHelpLong(int mouseX, int mouseY, float partialTicks) {
    return List.of(Text.translatable(this.modId + ".roundalib.help.cancel"),
        (MinecraftClient.IS_SYSTEM_MAC
            ? Text.translatable(this.modId + ".roundalib.help.save.mac")
            : Text.translatable(this.modId + ".roundalib.help.save.win")),
        (MinecraftClient.IS_SYSTEM_MAC
            ? Text.translatable(this.modId + ".roundalib.help.reset.mac")
            : Text.translatable(this.modId + ".roundalib.help.reset.win")));
  }

  protected void setValue(D value) {
    this.workingCopy.setValue(value);
  }

  protected D getValue() {
    return this.workingCopy.getValue();
  }

  protected void resetToDefault() {
    this.workingCopy.resetToDefault();
  }

  protected boolean isDirty() {
    return this.workingCopy.isDirty();
  }

  protected void commitValueToConfig() {
    this.configOption.setValue(this.getValue());
  }

  protected void discardAndExit() {
    this.close();
  }

  protected void saveAndExit() {
    this.commitValueToConfig();
    this.close();
  }
}
