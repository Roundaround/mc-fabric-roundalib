package me.roundaround.roundalib.config.gui.screen;

import org.lwjgl.glfw.GLFW;

import me.roundaround.roundalib.config.gui.GuiUtil;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class PositionEditScreen extends ConfigOptionSubScreen<Position, PositionConfigOption> {
  private boolean inverseX = false;
  private boolean inverseY = false;

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
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (super.keyPressed(keyCode, scanCode, modifiers)) {
      return true;
    }

    boolean shiftHeld = GuiUtil.isShiftHeld();
    int amount = shiftHeld ? 8 : 1;
    Position value = getValue();

    switch (keyCode) {
      case GLFW.GLFW_KEY_UP:
        setValue(value.movedUp(amount * (inverseY ? -1 : 1)));
        return true;
      case GLFW.GLFW_KEY_DOWN:
        setValue(value.movedDown(amount * (inverseY ? -1 : 1)));
        return true;
      case GLFW.GLFW_KEY_LEFT:
        setValue(value.movedLeft(amount * (inverseX ? -1 : 1)));
        return true;
      case GLFW.GLFW_KEY_RIGHT:
        setValue(value.movedRight(amount * (inverseX ? -1 : 1)));
        return true;
    }

    return false;
  }

  @Override
  protected void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    if (parent == null) {
      renderDarkenBackground(matrixStack, mouseX, mouseY, partialTicks);
    } else {
      renderTextureBackground(matrixStack, mouseX, mouseY, partialTicks);
    }
  }
}
