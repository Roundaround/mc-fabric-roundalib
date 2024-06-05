package me.roundaround.roundalib.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.RoundaLibIconButtons;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public abstract class PositionEditScreen extends ConfigOptionSubScreen<Position, PositionConfigOption> {
  protected static final Position CROSSHAIR_UV = new Position(0, 247);
  protected static final int CROSSHAIR_SIZE = 9;

  private IconButtonWidget upButton;
  private IconButtonWidget leftButton;
  private IconButtonWidget rightButton;
  private IconButtonWidget downButton;

  protected PositionEditScreen(
      Text title, Screen parent, PositionConfigOption configOption
  ) {
    super(title, parent, configOption);
  }

  @Override
  protected void init() {
    int startX = this.width - GuiUtil.PADDING;
    int startY = this.height - GuiUtil.PADDING - RoundaLibIconButtons.SIZE_M - GuiUtil.PADDING;

    this.upButton = this.addDrawableChild(
        RoundaLibIconButtons.upButton(startX - 2 * RoundaLibIconButtons.SIZE_M - GuiUtil.PADDING,
            startY - 3 * RoundaLibIconButtons.SIZE_M - 2 * GuiUtil.PADDING, this.modId, (button) -> this.moveUp()
        ));

    this.leftButton = this.addDrawableChild(
        RoundaLibIconButtons.leftButton(startX - 3 * RoundaLibIconButtons.SIZE_M - 2 * GuiUtil.PADDING,
            startY - 2 * RoundaLibIconButtons.SIZE_M - GuiUtil.PADDING, this.modId, (button) -> this.moveLeft()
        ));

    this.rightButton = this.addDrawableChild(RoundaLibIconButtons.rightButton(startX - RoundaLibIconButtons.SIZE_M,
        startY - 2 * RoundaLibIconButtons.SIZE_M - GuiUtil.PADDING, this.modId, (button) -> this.moveRight()
    ));

    this.downButton = this.addDrawableChild(
        RoundaLibIconButtons.downButton(startX - 2 * RoundaLibIconButtons.SIZE_M - GuiUtil.PADDING,
            startY - RoundaLibIconButtons.SIZE_M, this.modId, (button) -> this.moveDown()
        ));

    this.addDrawable((context, mouseX, mouseY, delta) -> {
      RenderSystem.setShaderColor(1, 1, 1, 0.8f);
      RenderSystem.enableBlend();

      context.drawTexture(new Identifier(this.modId, "textures/roundalib.png"),
          this.upButton.getX() + GuiUtil.PADDING / 2, leftButton.getY() + GuiUtil.PADDING / 2, CROSSHAIR_UV.x(),
          CROSSHAIR_UV.y(), CROSSHAIR_SIZE, CROSSHAIR_SIZE
      );

      RenderSystem.setShaderColor(1, 1, 1, 1);
      RenderSystem.disableBlend();
    });

    super.init();
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    Position.Direction direction = switch (keyCode) {
      case GLFW.GLFW_KEY_UP -> Position.Direction.UP;
      case GLFW.GLFW_KEY_DOWN -> Position.Direction.DOWN;
      case GLFW.GLFW_KEY_LEFT -> Position.Direction.LEFT;
      case GLFW.GLFW_KEY_RIGHT -> Position.Direction.RIGHT;
      default -> null;
    };

    if (direction == null) {
      return super.keyPressed(keyCode, scanCode, modifiers);
    }

    this.move(direction);
    return true;
  }

  @Override
  protected List<Text> getHelpLong(int mouseX, int mouseY, float partialTicks) {
    ArrayList<Text> full = new ArrayList<>();
    full.add(Text.translatable(this.modId + ".roundalib.help.position.single"));
    full.add(Text.translatable(this.modId + ".roundalib.help.position.multi"));
    full.addAll(super.getHelpLong(mouseX, mouseY, partialTicks));
    return full;
  }

  protected int getMoveAmount(Position.Direction direction, boolean largeStep) {
    return largeStep ? 8 : 1;
  }

  protected void move(Position.Direction direction) {
    this.setValue(this.getValue().moved(direction, this.getMoveAmount(direction, hasShiftDown())));
  }

  protected void moveUp() {
    this.move(Position.Direction.UP);
  }

  protected void moveDown() {
    this.move(Position.Direction.DOWN);
  }

  protected void moveLeft() {
    this.move(Position.Direction.LEFT);
  }

  protected void moveRight() {
    this.move(Position.Direction.RIGHT);
  }
}
