package me.roundaround.roundalib.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.config.RoundaLibIconButtons;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public abstract class ConfigOptionSubScreen<D, O extends ConfigOption<D, ?>> extends Screen {
  protected static final int DARKEN_STRENGTH = 120;

  protected final Screen parent;
  protected final O configOption;
  protected final O workingCopy;

  @SuppressWarnings("unchecked")
  protected ConfigOptionSubScreen(Text title, Screen parent, O configOption) {
    super(title);
    this.parent = parent;
    this.configOption = configOption;
    this.workingCopy = (O) configOption.createWorkingCopy();
  }

  @Override
  protected void init() {
    this.addSelectableChild(RoundaLibIconButtons.resetButton(
        this.height - GuiUtil.PADDING - RoundaLibIconButtons.SIZE_M,
        this.width - 3 * (GuiUtil.PADDING - RoundaLibIconButtons.SIZE_M),
        this.workingCopy));

    this.addSelectableChild(RoundaLibIconButtons.discardButton(
        this.height - GuiUtil.PADDING - RoundaLibIconButtons.SIZE_M,
        this.width - 2 * (GuiUtil.PADDING - RoundaLibIconButtons.SIZE_M),
        this.configOption.getConfig().getModId(),
        (button) -> this.discardAndExit()));

    this.addSelectableChild(RoundaLibIconButtons.saveButton(
        this.height - GuiUtil.PADDING - RoundaLibIconButtons.SIZE_M,
        this.width - GuiUtil.PADDING - RoundaLibIconButtons.SIZE_M,
        this.configOption.getConfig().getModId(),
        (button) -> this.saveAndExit()));
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderBackground(matrixStack, mouseX, mouseY, partialTicks);
    renderContent(matrixStack, mouseX, mouseY, partialTicks);
  }

  protected void renderBackground(
      MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    if (parent == null) {
      renderTextureBackground(matrixStack, mouseX, mouseY, partialTicks);
    } else {
      renderDarkenBackground(matrixStack, mouseX, mouseY, partialTicks);
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
