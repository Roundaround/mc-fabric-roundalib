package me.roundaround.roundalib.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.RoundaLibIconButtons;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public abstract class PositionEditScreen extends ConfigOptionSubScreen<Position, PositionConfigOption> {
  protected static final Position CROSSHAIR_UV = new Position(0, 247);
  protected static final int CROSSHAIR_SIZE = 9;

  private boolean inverseX = false;
  private boolean inverseY = false;
  private IconButtonWidget upButton;
  private IconButtonWidget leftButton;
  private IconButtonWidget rightButton;
  private IconButtonWidget downButton;

  protected PositionEditScreen(
      Text title, Screen parent, PositionConfigOption configOption
  ) {
    this(title, parent, configOption, false, false);
  }

  protected PositionEditScreen(
      Text title, Screen parent, PositionConfigOption configOption, boolean invertX, boolean invertY
  ) {
    super(title, parent, configOption);
    this.inverseX = invertX;
    this.inverseY = invertY;
  }

  @Override
  protected void init() {
    int startX = this.width - GuiUtil.PADDING;
    int startY = this.height - GuiUtil.PADDING - RoundaLibIconButtons.SIZE_M - GuiUtil.PADDING;

    this.upButton = addSelectableChild(
        RoundaLibIconButtons.upButton(startX - 2 * RoundaLibIconButtons.SIZE_M - GuiUtil.PADDING,
            startY - 3 * RoundaLibIconButtons.SIZE_M - 2 * GuiUtil.PADDING, this.modId, (button) -> this.moveUp()
        ));

    this.leftButton = addSelectableChild(
        RoundaLibIconButtons.leftButton(startX - 3 * RoundaLibIconButtons.SIZE_M - 2 * GuiUtil.PADDING,
            startY - 2 * RoundaLibIconButtons.SIZE_M - GuiUtil.PADDING, this.modId, (button) -> this.moveLeft()
        ));

    this.rightButton = addSelectableChild(RoundaLibIconButtons.rightButton(startX - RoundaLibIconButtons.SIZE_M,
        startY - 2 * RoundaLibIconButtons.SIZE_M - GuiUtil.PADDING, this.modId, (button) -> this.moveRight()
    ));

    this.downButton = addSelectableChild(
        RoundaLibIconButtons.downButton(startX - 2 * RoundaLibIconButtons.SIZE_M - GuiUtil.PADDING,
            startY - RoundaLibIconButtons.SIZE_M, this.modId, (button) -> this.moveDown()
        ));

    super.init();
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    switch (keyCode) {
      case GLFW.GLFW_KEY_UP -> {
        moveUp();
        return true;
      }
      case GLFW.GLFW_KEY_DOWN -> {
        moveDown();
        return true;
      }
      case GLFW.GLFW_KEY_LEFT -> {
        moveLeft();
        return true;
      }
      case GLFW.GLFW_KEY_RIGHT -> {
        moveRight();
        return true;
      }
    }

    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  protected List<Text> getHelpLong(int mouseX, int mouseY, float partialTicks) {
    ArrayList<Text> full = new ArrayList<>();
    full.add(Text.translatable(this.modId + ".roundalib.help.position.single"));
    full.add(Text.translatable(this.modId + ".roundalib.help.position.multi"));
    full.addAll(super.getHelpLong(mouseX, mouseY, partialTicks));
    return full;
  }

  @Override
  protected void renderContent(
      DrawContext drawContext, int mouseX, int mouseY, float partialTicks
  ) {
    super.renderContent(drawContext, mouseX, mouseY, partialTicks);

    RenderSystem.setShaderColor(1, 1, 1, 0.4f);
    RenderSystem.enableBlend();

    drawContext.drawTexture(new Identifier(this.modId, "textures/roundalib.png"),
        this.upButton.getX() + GuiUtil.PADDING / 2, leftButton.getY() + GuiUtil.PADDING / 2, CROSSHAIR_UV.x(),
        CROSSHAIR_UV.y(), CROSSHAIR_SIZE, CROSSHAIR_SIZE
    );

    RenderSystem.setShaderColor(1, 1, 1, 1);
    RenderSystem.disableBlend();
  }

  protected int getMoveAmount() {
    return hasShiftDown() ? 8 : 1;
  }

  protected void moveUp() {
    this.setValue(this.getValue().movedUp(this.getMoveAmount() * (this.inverseY ? -1 : 1)));
  }

  protected void moveDown() {
    this.setValue(this.getValue().movedDown(this.getMoveAmount() * (this.inverseY ? -1 : 1)));
  }

  protected void moveLeft() {
    this.setValue(this.getValue().movedLeft(this.getMoveAmount() * (this.inverseX ? -1 : 1)));
  }

  protected void moveRight() {
    this.setValue(this.getValue().movedRight(this.getMoveAmount() * (this.inverseX ? -1 : 1)));
  }
}
