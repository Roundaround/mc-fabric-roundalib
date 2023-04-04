package me.roundaround.roundalib.config.gui.widget;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.gui.GuiUtil;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class IconButtonWidget<T> extends AbstractClickableWidget<T> {
  public static final int HEIGHT_LG = 13;
  public static final int WIDTH_LG = 13;
  public static final int HEIGHT_SM = 9;
  public static final int WIDTH_SM = 9;
  public static final int SMALL_TEX_START_X = 3 * WIDTH_LG;
  public static final Position UV_LG_UNDO = new Position(0, HEIGHT_LG);
  public static final Position UV_LG_CANCEL = new Position(0, 2 * HEIGHT_LG);
  public static final Position UV_LG_CONFIRM = new Position(0, 3 * HEIGHT_LG);
  public static final Position UV_LG_HELP = new Position(0, 4 * HEIGHT_LG);
  public static final Position UV_LG_CLOSE = new Position(0, 5 * HEIGHT_LG);
  public static final Position UV_LG_ARROW_UP = new Position(0, 6 * HEIGHT_LG);
  public static final Position UV_LG_ARROW_DOWN = new Position(0, 7 * HEIGHT_LG);
  public static final Position UV_LG_ARROW_LEFT = new Position(0, 8 * HEIGHT_LG);
  public static final Position UV_LG_ARROW_RIGHT = new Position(0, 9 * HEIGHT_LG);
  public static final Position UV_SM_PLUS = new Position(SMALL_TEX_START_X, HEIGHT_SM);
  public static final Position UV_SM_MINUS = new Position(SMALL_TEX_START_X, 2 * HEIGHT_SM);
  public static final Position UV_SM_ARROW_UP = new Position(SMALL_TEX_START_X, 3 * HEIGHT_SM);
  public static final Position UV_SM_ARROW_DOWN = new Position(SMALL_TEX_START_X, 4 * HEIGHT_SM);
  public static final Position UV_SM_ARROW_LEFT = new Position(SMALL_TEX_START_X, 5 * HEIGHT_SM);
  public static final Position UV_SM_ARROW_RIGHT = new Position(SMALL_TEX_START_X, 6 * HEIGHT_SM);

  private final Position texUV;
  private final Text hoverTooltip;
  private final PressAction<T> pressAction;

  protected IconButtonWidget(
      T parent,
      ModConfig config,
      int top,
      int left,
      boolean large,
      Position texUV,
      Text hoverTooltip,
      PressAction<T> pressAction) {
    super(parent, config, top, left, large ? HEIGHT_LG : HEIGHT_SM, large ? WIDTH_LG : WIDTH_SM);
    this.texUV = texUV;
    this.hoverTooltip = hoverTooltip;
    this.pressAction = pressAction;
  }

  public static <T> IconButtonWidget<T> small(
      T parent,
      ModConfig config,
      int top,
      int left,
      Position texUV,
      Text hoverTooltip,
      PressAction<T> pressAction) {
    return new IconButtonWidget<>(parent, config, top, left, false, texUV, hoverTooltip, pressAction);
  }

  public static <T> IconButtonWidget<T> large(
      T parent,
      ModConfig config,
      int top,
      int left,
      Position texUV,
      Text hoverTooltip,
      PressAction<T> pressAction) {
    return new IconButtonWidget<>(parent, config, top, left, true, texUV, hoverTooltip, pressAction);
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    super.render(matrixStack, mouseX, mouseY, delta);

    RenderSystem.setShaderColor(1, 1, 1, 1);
    RenderSystem.enableBlend();
    RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    RenderSystem.setShaderTexture(0, GuiUtil.getWidgetsTexture(this.config));
    RenderSystem.setShader(GameRenderer::getPositionTexProgram);
    RenderSystem.applyModelViewMatrix();

    int u = texUV.x() + getImageXOffset() * width;
    int v = texUV.y() + getImageYOffset() * height;

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
    return 0;
  }

  protected boolean isDisabled() {
    return false;
  }

  protected void onPress() {
    pressAction.apply(this);
    GuiUtil.playSoundEvent(SoundEvents.UI_BUTTON_CLICK.value());
  }

  @FunctionalInterface
  public interface PressAction<T> {
    public void apply(IconButtonWidget<T> button);
  }
}
