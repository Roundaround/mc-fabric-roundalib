package me.roundaround.roundalib.config.gui.control;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.roundalib.config.gui.OptionRow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class ButtonControl<T> extends AbstractClickableControlWidget<T> {
  protected static final Identifier BUTTON_TEXTURES = new Identifier("minecraft", "textures/gui/widgets.png");
  protected static final int BUTTON_TEXTURE_WIDTH = 200;
  protected static final int BUTTON_TEXTURE_HEIGHT = 20;

  private boolean isActive = true;

  protected ButtonControl(OptionRow parent, int top, int left, int height, int width) {
    super(parent, top, left, height, width);
  }

  protected abstract Text getCurrentText();

  protected abstract boolean handleValidClick(double mouseX, double mouseY, int button);

  @Override
  public boolean onMouseClicked(double mouseX, double mouseY, int button) {
    if (!isActive) {
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

    int imageOffset = getImageOffset();
    int textureV = 46 + imageOffset * 20;

    // Top left
    drawTexture(matrixStack, left, top, 0, textureV, width / 2, height / 2);

    // Top right
    drawTexture(
        matrixStack,
        left + width / 2,
        top,
        BUTTON_TEXTURE_WIDTH - width / 2,
        textureV,
        width / 2,
        height / 2);

    // Bottom left
    drawTexture(
        matrixStack,
        left,
        top + height / 2,
        0,
        textureV + BUTTON_TEXTURE_HEIGHT - height / 2,
        width / 2,
        height / 2);

    // Bottom right
    drawTexture(
        matrixStack,
        left + width / 2,
        top + height / 2,
        BUTTON_TEXTURE_WIDTH - width / 2,
        textureV + BUTTON_TEXTURE_HEIGHT - height / 2,
        width / 2,
        height / 2);

    int colorInt = isActive ? 0xFFFFFF : 0xA0A0A0;
    int color = colorInt | 255 << 24;
    TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    drawCenteredText(
        matrixStack,
        textRenderer,
        getCurrentText(),
        left + width / 2,
        top + (height - 8) / 2,
        color);
  }

  protected int getImageOffset() {
    if (!isActive) {
      return 0;
    } else if (isHoveredOrFocused()) {
      return 2;
    }

    return 1;
  }

  @Override
  public void appendNarrations(NarrationMessageBuilder builder) {
    // TODO Auto-generated method stub
  }
}
