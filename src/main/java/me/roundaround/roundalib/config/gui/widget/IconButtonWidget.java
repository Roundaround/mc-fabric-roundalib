package me.roundaround.roundalib.config.gui.widget;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.roundalib.config.gui.GuiUtil;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class IconButtonWidget<T> extends AbstractClickableWidget<T> {
  public static final int HEIGHT_LARGE = 12;
  public static final int WIDTH_LARGE = 12;
  public static final int HEIGHT_SMALL = 9;
  public static final int WIDTH_SMALL = 9;
  public static final int SMALL_TEX_START_X = 36;
  protected static final Identifier TEXTURE = new Identifier("roundalib", "textures/gui.png");

  private final int texIdx;
  private final Text hoverTooltip;
  private final PressAction<T> pressAction;
  private final boolean large;

  public IconButtonWidget(
      T parent,
      int top,
      int left,
      int texIdx,
      Text hoverTooltip,
      PressAction<T> pressAction) {
    this(parent, top, left, true, texIdx, hoverTooltip, pressAction);
  }

  public IconButtonWidget(
      T parent,
      int top,
      int left,
      boolean large,
      int texIdx,
      Text hoverTooltip,
      PressAction<T> pressAction) {
    super(parent, top, left, large ? HEIGHT_LARGE : HEIGHT_SMALL, large ? WIDTH_LARGE : WIDTH_SMALL);
    this.texIdx = texIdx;
    this.hoverTooltip = hoverTooltip;
    this.pressAction = pressAction;
    this.large = large;
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    super.render(matrixStack, mouseX, mouseY, delta);

    RenderSystem.setShaderColor(1, 1, 1, 1);
    RenderSystem.enableBlend();
    RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    RenderSystem.setShaderTexture(0, TEXTURE);
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.applyModelViewMatrix();

    int u = (large ? 0 : SMALL_TEX_START_X) + getImageXOffset() * width;
    int v = getImageYOffset() * height;

    drawTexture(matrixStack, left, top, u, v, width, height);
  }

  @Override
  public List<Text> getTooltip(int mouseX, int mouseY, float delta) {
    if (isDisabled() || !hovered) {
      return List.of();
    }

    return List.of(hoverTooltip);
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

  @Override
  public boolean isNarratable() {
    return !isDisabled();
  }

  protected void appendTitleNarration(NarrationMessageBuilder builder) {
    builder.put(NarrationPart.TITLE, hoverTooltip);
  }

  @Override
  public void appendNarrations(NarrationMessageBuilder builder) {
    appendTitleNarration(builder);
    if (focused) {
      builder.put(NarrationPart.USAGE, Text.translatable("narration.button.usage.focused"));
    } else if (hovered) {
      builder.put(NarrationPart.USAGE, Text.translatable("narration.button.usage.hovered"));
    }
    builder.put(NarrationPart.HINT, hoverTooltip);
  }

  protected int getImageXOffset() {
    if (isDisabled()) {
      return 0;
    } else if (isHoveredOrFocused()) {
      return 2;
    }

    return 1;
  }

  protected int getImageYOffset() {
    return texIdx + 1;
  }

  protected boolean isDisabled() {
    return false;
  }

  protected void onPress() {
    pressAction.apply(this);
    GuiUtil.playSoundEvent(SoundEvents.UI_BUTTON_CLICK);
  }

  @FunctionalInterface
  public interface PressAction<T> {
    public void apply(IconButtonWidget<T> button);
  }
}
