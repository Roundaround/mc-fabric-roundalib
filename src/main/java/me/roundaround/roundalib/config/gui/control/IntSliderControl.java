package me.roundaround.roundalib.config.gui.control;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.roundalib.config.gui.widget.OptionRowWidget;
import me.roundaround.roundalib.config.option.IntConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class IntSliderControl extends AbstractClickableControlWidget<IntConfigOption> {
  protected static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");
  protected static final int BACKGROUND_WIDTH = 200;
  protected static final int BACKGROUND_HEIGHT = 20;
  protected static final int BAR_WIDTH = 8;
  protected static final int BAR_HEIGHT = 20;

  private static final MinecraftClient MINECRAFT = MinecraftClient.getInstance();

  private boolean mouseDown = false;

  public IntSliderControl(
      IntConfigOption configOption,
      OptionRowWidget parent,
      int top,
      int left,
      int height,
      int width) {
    super(configOption, parent, top, left, height, width);
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    super.render(matrixStack, mouseX, mouseY, delta);

    renderBackground(matrixStack, mouseX, mouseY);
    renderBar(matrixStack, mouseX, mouseY);

    drawCenteredText(
        matrixStack,
        MINECRAFT.textRenderer,
        configOption.getValueAsString(),
        left + width / 2,
        top + (height - 8) / 2,
        0xFFFFFF | 255 << 24);
  }

  protected void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY) {
    RenderSystem.setShaderColor(1, 1, 1, 1);
    RenderSystem.enableBlend();
    RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
    RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
    RenderSystem.applyModelViewMatrix();

    // Top left
    drawTexture(matrixStack, left, top, 0, 46, width / 2, height / 2);

    // Top right
    drawTexture(
        matrixStack,
        left + width / 2,
        top,
        BACKGROUND_WIDTH - width / 2,
        46,
        width / 2,
        height / 2);

    // Bottom left
    drawTexture(
        matrixStack,
        left,
        top + height / 2,
        0,
        46 + BACKGROUND_HEIGHT - height / 2,
        width / 2,
        height / 2);

    // Bottom right
    drawTexture(
        matrixStack,
        left + width / 2,
        top + height / 2,
        BACKGROUND_WIDTH - width / 2,
        46 + BACKGROUND_HEIGHT - height / 2,
        width / 2,
        height / 2);
  }

  protected void renderBar(MatrixStack matrixStack, int mouseX, int mouseY) {
    RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

    int uLeft = 0;
    int uRight = 196;
    int v = 46 + (isHoveredOrFocused() ? 2 : 1) * 20;

    drawTexture(
        matrixStack,
        left + (int) (getScaledValue() * (width - BAR_WIDTH)),
        top,
        uLeft,
        v,
        BAR_WIDTH / 2, BAR_HEIGHT);
    drawTexture(
        matrixStack,
        left + (int) (getScaledValue() * (width - BAR_WIDTH)) + BAR_WIDTH / 2,
        top,
        uRight,
        v,
        BAR_WIDTH / 2,
        BAR_HEIGHT);
  }

  @Override
  public boolean onMouseClicked(double mouseX, double mouseY, int button) {
    if (button != 0) {
      return false;
    }

    mouseDown = true;
    setValueFromMouse(mouseX);
    return true;
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    if (button != 0 || !mouseDown) {
      return false;
    }

    setValueFromMouse(mouseX);
    return true;
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    if (button == 0) {
      if (mouseDown) {
        SoundManager soundManager = MINECRAFT.getSoundManager();
        soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1));
      }
      mouseDown = false;
    }
    return true;
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (!focused) {
      return false;
    }

    if (keyCode == GLFW.GLFW_KEY_LEFT) {
      configOption.decrement();
    } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
      configOption.increment();
    }

    return true;
  }

  private void setValueFromMouse(double mouseX) {
    setScaledValue((float) (mouseX - (float) (left + BAR_WIDTH / 2)) / (this.width - BAR_WIDTH));
  }

  private void setScaledValue(float value) {
    int min = configOption.getMinValue().get();
    int max = configOption.getMaxValue().get();

    configOption.setValue(MathHelper.clamp(Math.round(value * (max - min) + min), min, max));
  }

  private float getScaledValue() {
    int value = configOption.getValue();
    int min = configOption.getMinValue().get();
    int max = configOption.getMaxValue().get();

    return (float) (MathHelper.clamp(value, min, max) - min) / (max - min);
  }

  @Override
  public void appendNarrations(NarrationMessageBuilder builder) {
  }

  @Override
  public boolean isHoveredOrFocused() {
    return super.isHoveredOrFocused() || mouseDown;
  }
}
