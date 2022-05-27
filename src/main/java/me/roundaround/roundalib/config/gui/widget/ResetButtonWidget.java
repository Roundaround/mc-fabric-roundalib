package me.roundaround.roundalib.config.gui.widget;

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import org.lwjgl.glfw.GLFW;

import me.roundaround.roundalib.config.gui.AbstractClickableWidget;
import me.roundaround.roundalib.config.gui.OptionRow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class ResetButtonWidget extends AbstractClickableWidget<OptionRow> {
  public static final int HEIGHT = 12;
  public static final int WIDTH = 12;
  protected static final Identifier TEXTURE = new Identifier("roundalib", "textures/gui.png");
  protected static final TranslatableText TOOLTIP = new TranslatableText("config.reset.tooltip");

  public ResetButtonWidget(OptionRow parent, int top, int left) {
    super(parent, top, left, HEIGHT, WIDTH);
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    super.render(matrixStack, mouseX, mouseY, delta);

    RenderSystem.setShaderColor(1, 1, 1, 1);
    RenderSystem.enableBlend();
    RenderSystem.blendFunc(
        GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    RenderSystem.setShaderTexture(0, TEXTURE);
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.applyModelViewMatrix();

    int u = getImageOffset(isHoveredOrFocused()) * WIDTH;
    int v = HEIGHT;

    drawTexture(matrixStack, left, top, u, v, WIDTH, HEIGHT);
  }

  @Override
  public void tick() {
    if (isDisabled() && isFocused()) {
      getParent().focusPrimaryElement();
    }
  }

  @Override
  public List<Text> getTooltip(int mouseX, int mouseY, float delta) {
    if (isDisabled() || !hovered) {
      return List.of();
    }

    return List.of(TOOLTIP);
  }

  @Override
  public boolean onMouseClicked(double mouseX, double mouseY, int button) {
    if (isDisabled()) {
      return false;
    }

    onPress();
    return true;
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (isDisabled()) {
      return false;
    }

    if (keyCode != GLFW.GLFW_KEY_ENTER && keyCode != GLFW.GLFW_KEY_SPACE && keyCode != GLFW.GLFW_KEY_KP_ENTER) {
      return false;
    }

    onPress();
    return true;
  }

  @Override
  public boolean setIsFocused(boolean focused) {
    if (focused && isDisabled()) {
      return false;
    }
    return super.setIsFocused(focused);
  }

  protected boolean isDisabled() {
    return !parent.getConfigOption().isModified();
  }

  protected int getImageOffset(boolean hovered) {
    if (isDisabled()) {
      return 0;
    } else if (hovered) {
      return 2;
    }

    return 1;
  }

  @Override
  public void appendNarrations(NarrationMessageBuilder builder) {
    // TODO Auto-generated method stub
  }

  private void onPress() {
    parent.getConfigOption().resetToDefault();
    SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
    soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1));
  }
}
