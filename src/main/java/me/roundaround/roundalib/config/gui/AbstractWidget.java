package me.roundaround.roundalib.config.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public abstract class AbstractWidget<T> extends DrawableHelper implements Widget {
  protected static final MinecraftClient MINECRAFT = MinecraftClient.getInstance();
  protected static final TextRenderer TEXT_RENDERER = MINECRAFT.textRenderer;

  protected T parent;
  protected int width;
  protected int height;
  protected int top;
  protected int bottom;
  protected int left;
  protected int right;
  protected boolean hovered;

  protected AbstractWidget(T parent, int top, int left, int height, int width) {
    this.parent = parent;
    this.top = top;
    this.bottom = top + height - 1;
    this.left = left;
    this.right = left + width - 1;
    this.height = height;
    this.width = width;
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    hovered = isMouseOver(mouseX, mouseY);
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    return (!(parent instanceof Widget) || ((Widget) parent).isMouseOver(mouseX, mouseY))
        && mouseX >= this.left
        && mouseX <= this.right
        && mouseY >= this.top
        && mouseY <= this.bottom;
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    return this.isMouseOver(mouseX, mouseY) && this.onMouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    return this.isMouseOver(mouseX, mouseY) && this.onMouseReleased(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseDragged(
      double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    return this.isMouseOver(mouseX, mouseY)
        && this.onMouseDragged(mouseX, mouseY, button, deltaX, deltaY);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    return this.isMouseOver(mouseX, mouseY) && this.onMouseScrolled(mouseX, mouseY, amount);
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getTop() {
    return top;
  }

  public int getBottom() {
    return bottom;
  }

  public int getLeft() {
    return left;
  }

  public int getRight() {
    return right;
  }

  public void moveTop(int top) {
    this.top = top;
    this.bottom = this.top + this.height - 1;
  }
}
