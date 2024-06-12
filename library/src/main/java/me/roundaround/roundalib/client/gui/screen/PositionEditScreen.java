package me.roundaround.roundalib.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.IconButtons;
import me.roundaround.roundalib.client.gui.layout.Coords;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public abstract class PositionEditScreen extends ConfigOptionSubScreen<Position, PositionConfigOption> {
  protected static final int CROSSHAIR_SIZE = 9;
  protected static final Coords CROSSHAIR_UV = new Coords(0, 256 - CROSSHAIR_SIZE);
  protected static final int MOVER_SIZE = CROSSHAIR_SIZE + 2 * (IconButtons.SIZE_M + GuiUtil.PADDING);

  private DirectionalLayoutWidget column;
  private DirectionalLayoutWidget row;

  protected PositionEditScreen(
      Text title, Screen parent, PositionConfigOption configOption
  ) {
    super(title, parent, configOption);
  }

  @Override
  protected void init() {
    this.column = DirectionalLayoutWidget.vertical();
    this.column.spacing(GuiUtil.PADDING);
    this.column.setPosition(
        this.width - GuiUtil.PADDING - MathHelper.ceil((MOVER_SIZE + IconButtons.SIZE_M) / 2f),
        this.height - GuiUtil.PADDING - MOVER_SIZE
    );

    this.row = DirectionalLayoutWidget.horizontal();
    this.row.spacing(GuiUtil.PADDING);
    this.row.setPosition(this.width - GuiUtil.PADDING - MOVER_SIZE,
        this.height - GuiUtil.PADDING - MathHelper.ceil((MOVER_SIZE + IconButtons.SIZE_M) / 2f)
    );

    super.init();

    IconButtonWidget upButton = this.column.add(
        IconButtons.upButton(this.modId, (button) -> this.moveUp()),
        Positioner::alignHorizontalCenter
    );
    this.column.add(EmptyWidget.ofHeight(CROSSHAIR_SIZE));
    IconButtonWidget downButton = this.column.add(
        IconButtons.downButton(this.modId, (button) -> this.moveDown()), Positioner::alignHorizontalCenter);

    IconButtonWidget leftButton = this.row.add(
        IconButtons.leftButton(this.modId, (button) -> this.moveLeft()), Positioner::alignVerticalCenter);
    this.row.add(EmptyWidget.ofWidth(CROSSHAIR_SIZE));
    IconButtonWidget rightButton = this.row.add(
        IconButtons.rightButton(this.modId, (button) -> this.moveRight()), Positioner::alignVerticalCenter);

    this.addDrawableChild(upButton);
    this.addDrawableChild(leftButton);
    this.addDrawableChild(rightButton);
    this.addDrawableChild(downButton);

    this.addDrawable((context, mouseX, mouseY, delta) -> {
      RenderSystem.setShaderColor(1, 1, 1, 0.8f);
      RenderSystem.enableBlend();

      context.drawTexture(new Identifier(this.modId, "textures/roundalib.png"),
          this.width - 2 * GuiUtil.PADDING - IconButtons.SIZE_M - CROSSHAIR_SIZE,
          this.height - 2 * GuiUtil.PADDING - IconButtons.SIZE_M - CROSSHAIR_SIZE, CROSSHAIR_UV.x(),
          CROSSHAIR_UV.y(), CROSSHAIR_SIZE, CROSSHAIR_SIZE
      );

      RenderSystem.setShaderColor(1, 1, 1, 1);
      RenderSystem.disableBlend();
    });

    this.initTabNavigation();
  }

  @Override
  protected void initTabNavigation() {
    super.initTabNavigation();

    if (this.column != null) {
      this.column.setPosition(
          this.width - GuiUtil.PADDING - MathHelper.ceil((MOVER_SIZE + IconButtons.SIZE_M) / 2f),
          this.height - GuiUtil.PADDING - MOVER_SIZE
      );
      this.column.refreshPositions();
    }

    if (this.row != null) {
      this.row.setPosition(this.width - GuiUtil.PADDING - MOVER_SIZE,
          this.height - GuiUtil.PADDING - MathHelper.ceil((MOVER_SIZE + IconButtons.SIZE_M) / 2f)
      );
      this.row.refreshPositions();
    }
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

  protected int getMoveAmount(Position.Direction direction) {
    return hasShiftDown() ? this.getLargeMoveAmount(direction) : this.getSmallMoveAmount(direction);
  }

  protected int getSmallMoveAmount(Position.Direction direction) {
    return 1;
  }

  protected int getLargeMoveAmount(Position.Direction direction) {
    return 8;
  }

  protected void move(Position.Direction direction) {
    this.setValue(this.getValue().moved(direction, this.getMoveAmount(direction)));
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
