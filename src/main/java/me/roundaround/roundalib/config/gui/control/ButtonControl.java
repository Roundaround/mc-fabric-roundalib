package me.roundaround.roundalib.config.gui.control;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import org.lwjgl.glfw.GLFW;

import me.roundaround.roundalib.config.gui.widget.OptionRowWidget;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class ButtonControl<O extends ConfigOption<?, ?>> extends AbstractClickableControlWidget<O> {
  protected static final Identifier BUTTON_TEXTURES = new Identifier("minecraft", "textures/gui/widgets.png");
  protected static final int BUTTON_TEXTURE_WIDTH = 200;
  protected static final int BUTTON_TEXTURE_HEIGHT = 20;

  private boolean isActive = true;

  protected ButtonControl(O configOption, OptionRowWidget parent, int top, int left, int height, int width) {
    super(configOption, parent, top, left, height, width);
  }

  protected abstract Text getCurrentText();

  protected void onPress(int button) {
    SoundManager soundManager = MINECRAFT.getSoundManager();
    soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1));
  }

  @Override
  public boolean onMouseClicked(double mouseX, double mouseY, int button) {
    if (!isActive) {
      return false;
    }

    onPress(button);
    return true;
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (!isActive) {
      return false;
    }

    if (keyCode != GLFW.GLFW_KEY_ENTER && keyCode != GLFW.GLFW_KEY_SPACE && keyCode != GLFW.GLFW_KEY_KP_ENTER) {
      return false;
    }

    onPress(Screen.hasShiftDown() ? 1 : 0);
    return true;
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    super.render(matrixStack, mouseX, mouseY, delta);

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
    drawCenteredText(
        matrixStack,
        TEXT_RENDERER,
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
}
