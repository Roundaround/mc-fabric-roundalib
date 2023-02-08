package me.roundaround.roundalib.config.gui.screen;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.roundalib.config.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class PositionEditScreen extends ConfigOptionSubScreen<Position, PositionConfigOption> {
  protected static final Identifier TEXTURE = new Identifier("roundalib", "textures/gui.png");
  protected static final Position CROSSHAIR_UV = new Position(247, 0);
  protected static final int CROSSHAIR_SIZE = 9;

  private boolean inverseX = false;
  private boolean inverseY = false;

  private IconButtonWidget<PositionEditScreen> upButton;
  private IconButtonWidget<PositionEditScreen> leftButton;
  private IconButtonWidget<PositionEditScreen> rightButton;
  private IconButtonWidget<PositionEditScreen> downButton;

  protected PositionEditScreen(
      Text title,
      Screen parent,
      PositionConfigOption configOption) {
    this(title, parent, configOption, false, false);
  }

  protected PositionEditScreen(
      Text title,
      Screen parent,
      PositionConfigOption configOption,
      boolean inverseX,
      boolean inverseY) {
    super(title, parent, configOption);
    this.inverseX = inverseX;
    this.inverseY = inverseY;
  }

  @Override
  protected void init() {
    int startY = height - 4 - IconButtonWidget.HEIGHT_LG - 4;
    int startX = width - 4;

    upButton = IconButtonWidget.large(
        this,
        startY - 3 * IconButtonWidget.HEIGHT_LG - 2 * 4,
        startX - 2 * IconButtonWidget.WIDTH_LG - 4,
        IconButtonWidget.UV_LG_ARROW_UP,
        Text.translatable("roundalib.move.up"),
        (button) -> {
          moveUp();
        });

    leftButton = IconButtonWidget.large(
        this,
        startY - 2 * IconButtonWidget.HEIGHT_LG - 4,
        startX - 3 * IconButtonWidget.WIDTH_LG - 2 * 4,
        IconButtonWidget.UV_LG_ARROW_LEFT,
        Text.translatable("roundalib.move.left"),
        (button) -> {
          moveLeft();
        });

    rightButton = IconButtonWidget.large(
        this,
        startY - 2 * IconButtonWidget.HEIGHT_LG - 4,
        startX - IconButtonWidget.WIDTH_LG,
        IconButtonWidget.UV_LG_ARROW_RIGHT,
        Text.translatable("roundalib.move.right"),
        (button) -> {
          moveRight();
        });

    downButton = IconButtonWidget.large(
        this,
        startY - IconButtonWidget.HEIGHT_LG,
        startX - 2 * IconButtonWidget.WIDTH_LG - 4,
        IconButtonWidget.UV_LG_ARROW_DOWN,
        Text.translatable("roundalib.move.down"),
        (button) -> {
          moveDown();
        });

    addSelectableChild(upButton);
    addSelectableChild(leftButton);
    addSelectableChild(rightButton);
    addSelectableChild(downButton);

    super.init();
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (super.keyPressed(keyCode, scanCode, modifiers)) {
      return true;
    }

    switch (keyCode) {
      case GLFW.GLFW_KEY_UP:
        moveUp();
        return true;
      case GLFW.GLFW_KEY_DOWN:
        moveDown();
        return true;
      case GLFW.GLFW_KEY_LEFT:
        moveLeft();
        return true;
      case GLFW.GLFW_KEY_RIGHT:
        moveRight();
        return true;
    }

    return false;
  }

  @Override
  protected List<Text> getHelpLong(int mouseX, int mouseY, float partialTicks) {
    ArrayList<Text> full = new ArrayList<>();
    full.add(Text.translatable("roundalib.help.position.single"));
    full.add(Text.translatable("roundalib.help.position.multi"));
    full.addAll(super.getHelpLong(mouseX, mouseY, partialTicks));
    return full;
  }

  @Override
  protected void renderContent(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    super.renderContent(matrixStack, mouseX, mouseY, partialTicks);

    int left = upButton.getLeft() + 2;
    int top = leftButton.getTop() + 2;
    
    RenderSystem.setShaderColor(1, 1, 1, 0.4f);
    RenderSystem.enableBlend();
    RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    RenderSystem.setShaderTexture(0, TEXTURE);
    RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
    RenderSystem.applyModelViewMatrix();

    matrixStack.push();
    matrixStack.translate(0, 0, 50);
    drawTexture(matrixStack, left, top, CROSSHAIR_UV.x(), CROSSHAIR_UV.y(), CROSSHAIR_SIZE, CROSSHAIR_SIZE);
    matrixStack.pop();
  }

  protected int getMoveAmount() {
    return Screen.hasShiftDown() ? 8 : 1;
  }

  private void moveUp() {
    setValue(getValue().movedUp(getMoveAmount() * (inverseY ? -1 : 1)));
  }

  private void moveDown() {
    setValue(getValue().movedDown(getMoveAmount() * (inverseY ? -1 : 1)));
  }

  private void moveLeft() {
    setValue(getValue().movedLeft(getMoveAmount() * (inverseX ? -1 : 1)));
  }

  private void moveRight() {
    setValue(getValue().movedRight(getMoveAmount() * (inverseX ? -1 : 1)));
  }
}
