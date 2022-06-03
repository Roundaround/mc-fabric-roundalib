package me.roundaround.roundalib.config.gui.widget;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.roundalib.config.gui.control.IntInputControl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class IntStepButtonWidget extends AbstractClickableWidget<IntInputControl> {
  public static final int TEX_START_X = 36;
  public static final int HEIGHT = 9;
  public static final int WIDTH = 9;
  protected static final Identifier TEXTURE = new Identifier("roundalib", "textures/gui.png");

  private boolean increment;
  private Text tooltip;

  public IntStepButtonWidget(IntInputControl parent, boolean increment, int top, int left) {
    super(parent, top, left, HEIGHT, WIDTH);
    this.increment = increment;
    tooltip = new TranslatableText(increment ? "config.step_up.tooltip" : "config.step_down.tooltip",
        parent.getConfigOption().getStep());
  }

  @Override
  public void tick() {
    if (isDisabled() && isFocused()) {
      getOptionRow().focusPrimaryElement();
    }
  }

  @Override
  public List<Text> getTooltip(int mouseX, int mouseY, float delta) {
    if (isDisabled() || !hovered) {
      return List.of();
    }

    return List.of(tooltip);
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

    int u = TEX_START_X + getImageOffset() * WIDTH;
    int v = increment ? HEIGHT : 2 * HEIGHT;

    drawTexture(matrixStack, left, top, u, v, WIDTH, HEIGHT);
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
    return increment && !parent.getConfigOption().canIncrement()
        || !increment && !parent.getConfigOption().canDecrement();
  }

  protected int getImageOffset() {
    if (isDisabled()) {
      return 0;
    } else if (isHoveredOrFocused()) {
      return 2;
    }

    return 1;
  }

  @Override
  public boolean isNarratable() {
    return !isDisabled();
  }

  @Override
  public void appendNarrations(NarrationMessageBuilder builder) {
    builder.put(NarrationPart.TITLE,
        new TranslatableText("config.step.narration", parent.getConfigOption().getLabel()));
    if (focused) {
      builder.put(NarrationPart.USAGE, new TranslatableText("narration.button.usage.focused"));
    } else if (hovered) {
      builder.put(NarrationPart.USAGE, new TranslatableText("narration.button.usage.hovered"));
    }
    builder.put(NarrationPart.HINT, tooltip);
  }

  private void onPress() {
    if (increment) {
      parent.getConfigOption().increment();
    } else {
      parent.getConfigOption().decrement();
    }
    SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
    soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1));

  }

  public OptionRowWidget getOptionRow() {
    return getParent().getOptionRow();
  }
}
