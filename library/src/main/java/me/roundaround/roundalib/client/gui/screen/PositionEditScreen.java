package me.roundaround.roundalib.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public abstract class PositionEditScreen extends ConfigOptionSubScreen<Position, PositionConfigOption> {
  protected static final int CROSSHAIR_SIZE = 9;
  protected static final int MOVER_SIZE = CROSSHAIR_SIZE + 2 * (IconButtonWidget.SIZE_M + GuiUtil.PADDING);

  protected final Text helpMoveSingleText;
  protected final Text helpMoveMultiText;

  private final SimplePositioningWidget mover = new SimplePositioningWidget();
  private final DirectionalLayoutWidget column = DirectionalLayoutWidget.vertical().spacing(GuiUtil.PADDING);
  private final DirectionalLayoutWidget row = DirectionalLayoutWidget.horizontal().spacing(GuiUtil.PADDING);

  protected PositionEditScreen(
      Text title, Screen parent, PositionConfigOption configOption
  ) {
    super(title, parent, configOption);

    this.helpMoveSingleText = Text.translatable(this.modId + ".roundalib.help.position.single");
    this.helpMoveMultiText = Text.translatable(this.modId + ".roundalib.help.position.multi");

    this.column.getMainPositioner().alignHorizontalCenter();
    this.row.getMainPositioner().alignVerticalCenter();

    this.buttonRow.spacing(7);
    this.body.getMainPositioner().alignBottom().alignRight().margin(GuiUtil.PADDING);
  }

  @Override
  protected void initBody() {
    super.initBody();

    this.body.add(this.mover);
    this.mover.add(this.column, (positioner) -> positioner.alignBottom().alignRight());

    this.column.add(IconButtonWidget.builder(IconButtonWidget.BuiltinIcon.UP_13, this.modId)
        .dimensions(IconButtonWidget.SIZE_M)
        .messageAndTooltip(Text.translatable(this.modId + ".roundalib.up.tooltip"))
        .onPress((button) -> this.moveUp())
        .build());
    this.column.add(this.row);
    this.column.add(IconButtonWidget.builder(IconButtonWidget.BuiltinIcon.DOWN_13, this.modId)
        .dimensions(IconButtonWidget.SIZE_M)
        .messageAndTooltip(Text.translatable(this.modId + ".roundalib.down.tooltip"))
        .onPress((button) -> this.moveDown())
        .build());

    this.row.add(IconButtonWidget.builder(IconButtonWidget.BuiltinIcon.LEFT_13, this.modId)
        .dimensions(IconButtonWidget.SIZE_M)
        .messageAndTooltip(Text.translatable(this.modId + ".roundalib.left.tooltip"))
        .onPress((button) -> this.moveLeft())
        .build());
    this.row.add(EmptyWidget.ofWidth(CROSSHAIR_SIZE));
    this.row.add(IconButtonWidget.builder(IconButtonWidget.BuiltinIcon.RIGHT_13, this.modId)
        .dimensions(IconButtonWidget.SIZE_M)
        .messageAndTooltip(Text.translatable(this.modId + ".roundalib.right.tooltip"))
        .onPress((button) -> this.moveRight())
        .build());

    this.addDrawable((context, mouseX, mouseY, delta) -> {
      RenderSystem.enableBlend();
      context.drawGuiTexture(
          new Identifier(this.modId, "hud/roundalib/crosshair-9"),
          this.mover.getX() + (this.mover.getWidth() - CROSSHAIR_SIZE) / 2,
          this.mover.getY() + (this.mover.getHeight() - CROSSHAIR_SIZE) / 2, CROSSHAIR_SIZE, CROSSHAIR_SIZE
      );
      RenderSystem.disableBlend();
    });
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
    full.add(this.helpMoveSingleText);
    full.add(this.helpMoveMultiText);
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
