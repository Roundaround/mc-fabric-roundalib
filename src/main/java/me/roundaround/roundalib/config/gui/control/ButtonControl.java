package me.roundaround.roundalib.config.gui.control;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class ButtonControl<T> extends Control<T> {
  protected static final Identifier BUTTON_TEXTURES =
      new Identifier("minecraft", "textures/gui/widgets.png");
  protected static final int BUTTON_TEXTURE_WIDTH = 200;
  protected static final int BUTTON_TEXTURE_HEIGHT = 20;

  private boolean isActive = true;

  protected ButtonControl(
      OptionRow parent, ConfigOption<T> configOption, int top, int left, int height, int width) {
    super(parent, configOption, top, left, height, width);
  }

  protected abstract Text getCurrentText();

  protected abstract boolean handleValidClick(double mouseX, double mouseY, int button);

  @Override
  public boolean onMouseClicked(double mouseX, double mouseY, int button) {
    if (!this.isActive) {
      return false;
    }

    boolean handled = handleValidClick(mouseX, mouseY, button);

    if (handled) {
      SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
      soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1));
    }

    return handled;
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    RenderSystem.setShaderColor(1, 1, 1, 1);
    RenderSystem.enableBlend();
    RenderSystem.blendFunc(
        GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    RenderSystem.setShaderTexture(0, BUTTON_TEXTURES);
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.applyModelViewMatrix();

    int imageOffset = this.getImageOffset(this.isMouseOver(mouseX, mouseY));
    int textureV = 46 + imageOffset * 20;

    // Top left
    drawTexture(matrixStack, this.left, this.top, 0, textureV, this.width / 2, this.height / 2);

    // Top right
    drawTexture(
        matrixStack,
        this.left + this.width / 2,
        this.top,
        BUTTON_TEXTURE_WIDTH - this.width / 2,
        textureV,
        this.width / 2,
        this.height / 2);

    // Bottom left
    drawTexture(
        matrixStack,
        this.left,
        this.top + this.height / 2,
        0,
        textureV + BUTTON_TEXTURE_HEIGHT - this.height / 2,
        this.width / 2,
        this.height / 2);

    // Bottom right
    drawTexture(
        matrixStack,
        this.left + this.width / 2,
        this.top + this.height / 2,
        BUTTON_TEXTURE_WIDTH - this.width / 2,
        textureV + BUTTON_TEXTURE_HEIGHT - this.height / 2,
        this.width / 2,
        this.height / 2);

    int colorInt = this.isActive ? 0xFFFFFF : 0xA0A0A0;
    int color = colorInt | 255 << 24;
    TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    drawCenteredText(
        matrixStack,
        textRenderer,
        this.getCurrentText(),
        this.left + this.width / 2,
        this.top + (this.height - 8) / 2,
        color);
  }

  protected int getImageOffset(boolean hovered) {
    if (!this.isActive) {
      return 0;
    } else if (hovered) {
      return 2;
    }

    return 1;
  }
}
