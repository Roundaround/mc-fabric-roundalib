package me.roundaround.roundalib.config.gui.screen;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import me.roundaround.roundalib.config.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class PositionEditScreen extends ConfigOptionSubScreen<Position, PositionConfigOption> {
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
    int startY = height - 4 - IconButtonWidget.HEIGHT_LARGE - 4;
    int startX = width - 4;

    upButton = new IconButtonWidget<PositionEditScreen>(
        this,
        startY - 3 * IconButtonWidget.HEIGHT_SMALL - 2 * 2,
        startX - 2 * IconButtonWidget.HEIGHT_SMALL - 2,
        false,
        2,
        Text.translatable("roundalib.move.up"),
        (button) -> {
          moveUp();
        });

    downButton = new IconButtonWidget<PositionEditScreen>(
        this,
        startY - IconButtonWidget.HEIGHT_SMALL,
        startX - 2 * IconButtonWidget.HEIGHT_SMALL - 2,
        false,
        3,
        Text.translatable("roundalib.move.down"),
        (button) -> {
          moveDown();
        });

    leftButton = new IconButtonWidget<PositionEditScreen>(
        this,
        startY - 2 * IconButtonWidget.HEIGHT_SMALL - 2,
        startX - 3 * IconButtonWidget.HEIGHT_SMALL - 2 * 2,
        false,
        4,
        Text.translatable("roundalib.move.left"),
        (button) -> {
          moveLeft();
        });

    rightButton = new IconButtonWidget<PositionEditScreen>(
        this,
        startY - 2 * IconButtonWidget.HEIGHT_SMALL - 2,
        startX - IconButtonWidget.HEIGHT_SMALL,
        false,
        5,
        Text.translatable("roundalib.move.right"),
        (button) -> {
          moveRight();
        });

    addSelectableChild(upButton);
    addSelectableChild(downButton);
    addSelectableChild(leftButton);
    addSelectableChild(rightButton);

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
