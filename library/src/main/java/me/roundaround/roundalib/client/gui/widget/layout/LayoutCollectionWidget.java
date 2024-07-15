package me.roundaround.roundalib.client.gui.widget.layout;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.Widget;

import java.util.ArrayList;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class LayoutCollectionWidget implements LayoutWidget {
  protected final ArrayList<Wrapper<?>> wrappers = new ArrayList<>();

  protected LayoutCollectionWidget() {
  }

  public static LayoutCollectionWidget create() {
    return new LayoutCollectionWidget();
  }

  public static LayoutCollectionWidget create(Iterable<Widget> widgets) {
    LayoutCollectionWidget container = new LayoutCollectionWidget();
    widgets.forEach(container::add);
    return container;
  }

  public <T extends Widget> T add(T widget) {
    this.wrappers.add(new Wrapper<>(widget));
    return widget;
  }

  public <T extends Widget> T add(T widget, LayoutHookWithParent<LayoutCollectionWidget, T> layoutHook) {
    this.wrappers.add(new Wrapper<>(widget, layoutHook));
    return widget;
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
    this.wrappers.forEach((wrapper) -> consumer.accept(wrapper.getWidget()));
  }

  @Override
  public void refreshPositions() {
    this.wrappers.forEach((wrapper) -> wrapper.runHook(this));
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

  @Environment(EnvType.CLIENT)
  public static class Wrapper<T extends Widget> {
    private final T widget;
    private final LayoutHookWithParent<LayoutCollectionWidget, T> layoutHook;

    public Wrapper(T widget) {
      this(widget, LayoutHookWithParent.noop());
    }

    public Wrapper(T widget, LayoutHookWithParent<LayoutCollectionWidget, T> layoutHook) {
      this.widget = widget;
      this.layoutHook = layoutHook;
    }

    public T getWidget() {
      return this.widget;
    }

    public void runHook(LayoutCollectionWidget parent) {
      this.layoutHook.run(parent, this.widget);
      if (this.widget instanceof LayoutWidget layoutWidget) {
        layoutWidget.refreshPositions();
      }
    }
  }
}
