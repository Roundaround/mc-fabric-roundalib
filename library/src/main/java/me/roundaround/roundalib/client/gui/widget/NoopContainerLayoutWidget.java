package me.roundaround.roundalib.client.gui.widget;

import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.Widget;

import java.util.ArrayList;
import java.util.function.Consumer;

public class NoopContainerLayoutWidget implements LayoutWidget {
  protected final ArrayList<Widget> children = new ArrayList<>();

  protected NoopContainerLayoutWidget() {
  }

  public static NoopContainerLayoutWidget create() {
    return new NoopContainerLayoutWidget();
  }

  public static NoopContainerLayoutWidget create(Iterable<Widget> widgets) {
    NoopContainerLayoutWidget container = new NoopContainerLayoutWidget();
    widgets.forEach(container::add);
    return container;
  }

  public <T extends Widget> T add(T widget) {
    this.children.add(widget);
    return widget;
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
    this.children.forEach(consumer);
  }

  @Override
  public void setX(int x) {
    // Do nothing
  }

  @Override
  public void setY(int y) {
    // Do nothing
  }

  @Override
  public int getX() {
    return 0;
  }

  @Override
  public int getY() {
    return 0;
  }

  @Override
  public int getWidth() {
    return 0;
  }

  @Override
  public int getHeight() {
    return 0;
  }
}
