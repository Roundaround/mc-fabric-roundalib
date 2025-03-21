package me.roundaround.roundalib.client.gui.screen;

import me.roundaround.roundalib.client.gui.icon.BuiltinIcon;
import me.roundaround.roundalib.client.gui.util.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.linear.LinearLayoutWidget;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.drawable.CrosshairWidget;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public abstract class PositionEditScreen extends ConfigOptionSubScreen<Position, PositionConfigOption> {
  protected static final int CROSSHAIR_SIZE = 9;

  protected final Text helpMoveSingleText;
  protected final Text helpMoveMultiText;

  protected LinearLayoutWidget bottomRight;
  protected LinearLayoutWidget mover;

  protected PositionEditScreen(
      Text title, ConfigScreen parent, PositionConfigOption configOption
  ) {
    super(title, parent, configOption);

    this.helpMoveSingleText = Text.translatable(this.modId + ".roundalib.help.position.single");
    this.helpMoveMultiText = Text.translatable(this.modId + ".roundalib.help.position.multi");
  }

  @Override
  protected void initElements() {
    this.bottomRight = LinearLayoutWidget.vertical()
        .spacing(GuiUtil.PADDING)
        .defaultOffAxisContentAlignCenter()
        .alignSelfRight()
        .alignSelfBottom();
    super.placeActionRow(this.bottomRight);

    this.mover = this.createMover();
    this.placeMover(this.mover);

    super.initElements();
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

    GuiUtil.playClickSound();
    this.move(direction);
    return true;
  }

  @Override
  protected List<Text> getHelpLong() {
    ArrayList<Text> full = new ArrayList<>();
    full.add(this.helpMoveSingleText);
    full.add(this.helpMoveMultiText);
    full.addAll(super.getHelpLong());
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

  protected LinearLayoutWidget createMover() {
    LinearLayoutWidget mover = LinearLayoutWidget.vertical()
        .spacing(GuiUtil.PADDING)
        .defaultOffAxisContentAlignCenter();

    mover.add(IconButtonWidget.builder(BuiltinIcon.UP_13, this.modId)
        .dimensions(IconButtonWidget.SIZE_M)
        .messageAndTooltip(Text.translatable(this.modId + ".roundalib.up.tooltip"))
        .onPress((button) -> this.moveUp())
        .build());

    LinearLayoutWidget centerRow = LinearLayoutWidget.horizontal()
        .spacing(GuiUtil.PADDING)
        .defaultOffAxisContentAlignCenter();
    centerRow.add(IconButtonWidget.builder(BuiltinIcon.LEFT_13, this.modId)
        .dimensions(IconButtonWidget.SIZE_M)
        .messageAndTooltip(Text.translatable(this.modId + ".roundalib.left.tooltip"))
        .onPress((button) -> this.moveLeft())
        .build());
    centerRow.add(new CrosshairWidget(1, 1, 3, GuiUtil.CROSSHAIR_COLOR));
    centerRow.add(IconButtonWidget.builder(BuiltinIcon.RIGHT_13, this.modId)
        .dimensions(IconButtonWidget.SIZE_M)
        .messageAndTooltip(Text.translatable(this.modId + ".roundalib.right.tooltip"))
        .onPress((button) -> this.moveRight())
        .build());
    mover.add(centerRow);

    mover.add(IconButtonWidget.builder(BuiltinIcon.DOWN_13, this.modId)
        .dimensions(IconButtonWidget.SIZE_M)
        .messageAndTooltip(Text.translatable(this.modId + ".roundalib.down.tooltip"))
        .onPress((button) -> this.moveDown())
        .build());

    return mover;
  }

  protected void placeMover(LinearLayoutWidget mover) {
    this.bottomRight.add(mover);
  }

  @Override
  protected void placeActionRow(LinearLayoutWidget actionRow) {
    this.bottomRight.add(actionRow);
  }
}
