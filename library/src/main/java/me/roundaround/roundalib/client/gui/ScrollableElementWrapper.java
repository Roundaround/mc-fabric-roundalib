package me.roundaround.roundalib.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ScrollableElementWrapper implements Drawable, Element, LayoutWidget {
  private final Element element;
  private double scrollAmount;

  public ScrollableElementWrapper(Element element) {
    this.element = element;
  }

  public void setScrollAmount(double scrollAmount) {
    this.scrollAmount = scrollAmount;
  }

  public double getScrollAmount() {
    return this.scrollAmount;
  }

  public boolean isDrawable() {
    return this.element instanceof Drawable;
  }

  public boolean isWidget() {
    return this.element instanceof Widget;
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    if (this.element instanceof Drawable drawable) {
      drawable.render(context, mouseX, mouseY - (int) this.scrollAmount, delta);
    }
  }

  @Override
  public void mouseMoved(double mouseX, double mouseY) {
    this.element.mouseMoved(mouseX, mouseY - this.scrollAmount);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    return this.element.mouseClicked(mouseX, mouseY - this.scrollAmount, button);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    return this.element.mouseReleased(mouseX, mouseY - this.scrollAmount, button);
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    return this.element.mouseDragged(mouseX, mouseY - this.scrollAmount, button, deltaX, deltaY);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    return this.element.mouseScrolled(mouseX, mouseY - this.scrollAmount, horizontalAmount, verticalAmount);
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    return this.element.isMouseOver(mouseX, mouseY - this.scrollAmount);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    return this.element.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
    return this.element.keyReleased(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean charTyped(char chr, int modifiers) {
    return this.element.charTyped(chr, modifiers);
  }

  @Override
  public void setFocused(boolean focused) {
    this.element.setFocused(focused);
  }

  @Override
  public boolean isFocused() {
    return this.element.isFocused();
  }

  @Nullable
  @Override
  public GuiNavigationPath getFocusedPath() {
    return this.element.getFocusedPath();
  }

  @Nullable
  @Override
  public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
    return this.element.getNavigationPath(navigation);
  }

  @Override
  public ScreenRect getNavigationFocus() {
    return this.element.getNavigationFocus();
  }

  @Override
  public int getNavigationOrder() {
    return this.element.getNavigationOrder();
  }

  @Override
  public void setX(int x) {
    if (this.element instanceof Widget widget) {
      widget.setX(x);
    }
  }

  @Override
  public void setY(int y) {
    if (this.element instanceof Widget widget) {
      widget.setY(y);
    }
  }

  @Override
  public int getX() {
    if (this.element instanceof Widget widget) {
      return widget.getX();
    }
    return 0;
  }

  @Override
  public int getY() {
    if (this.element instanceof Widget widget) {
      return widget.getY();
    }
    return 0;
  }

  @Override
  public int getWidth() {
    if (this.element instanceof Widget widget) {
      return widget.getWidth();
    }
    return 0;
  }

  @Override
  public int getHeight() {
    if (this.element instanceof Widget widget) {
      return widget.getHeight();
    }
    return 0;
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
    if (this.element instanceof Widget widget) {
      consumer.accept(widget);
    }
  }
}
