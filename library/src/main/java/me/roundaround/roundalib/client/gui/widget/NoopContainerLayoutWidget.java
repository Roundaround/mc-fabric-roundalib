package me.roundaround.roundalib.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.Widget;

import java.util.ArrayList;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class NoopContainerLayoutWidget implements LayoutWidget {
  protected final ArrayList<WrappedWidget<?>> children = new ArrayList<>();

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
    this.children.add(new WrappedWidget<>(widget));
    return widget;
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
    this.children.forEach((wrappedWidget) -> consumer.accept(wrappedWidget.getWidget()));
  }

  @Override
  public void refreshPositions() {
    this.children.forEach((wrappedWidget) -> wrappedWidget.runHook(this));
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
  public static class WrappedWidget<T extends Widget> {
    private final T widget;
    private final LayoutHookWithRefs<NoopContainerLayoutWidget, T> layoutHook;

    public WrappedWidget(T widget) {
      this(widget, LayoutHookWithRefs.noop());
    }

    public WrappedWidget(T widget, LayoutHookWithRefs<NoopContainerLayoutWidget, T> layoutHook) {
      this.widget = widget;
      this.layoutHook = layoutHook;
    }

    public T getWidget() {
      return this.widget;
    }

    public void runHook(NoopContainerLayoutWidget parent) {
      this.layoutHook.run(parent, this.widget);
      if (this.widget instanceof LayoutWidget layoutWidget) {
        layoutWidget.refreshPositions();
      }
    }
  }
}
