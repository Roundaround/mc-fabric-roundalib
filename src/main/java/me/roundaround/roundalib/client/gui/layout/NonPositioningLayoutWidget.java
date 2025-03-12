package me.roundaround.roundalib.client.gui.layout;

import me.roundaround.roundalib.client.gui.util.Alignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class NonPositioningLayoutWidget extends SizableLayoutWidget {
  private final List<CellWidget<?>> cells = new ArrayList<>();

  private Alignment alignSelfX = Alignment.START;
  private Alignment alignSelfY = Alignment.START;

  public NonPositioningLayoutWidget() {
    this(0, 0, 0, 0);
  }

  public NonPositioningLayoutWidget(int width, int height) {
    this(0, 0, width, height);
  }

  public NonPositioningLayoutWidget(int x, int y, int width, int height) {
    super(x, y, width, height);
  }

  public NonPositioningLayoutWidget alignSelfX(Alignment alignX) {
    this.alignSelfX = alignX;
    return this;
  }

  public NonPositioningLayoutWidget alignSelfLeft() {
    return this.alignSelfX(Alignment.START);
  }

  public NonPositioningLayoutWidget alignSelfCenterX() {
    return this.alignSelfX(Alignment.CENTER);
  }

  public NonPositioningLayoutWidget alignSelfRight() {
    return this.alignSelfX(Alignment.END);
  }

  public NonPositioningLayoutWidget alignSelfY(Alignment alignY) {
    this.alignSelfY = alignY;
    return this;
  }

  public NonPositioningLayoutWidget alignSelfTop() {
    return this.alignSelfY(Alignment.START);
  }

  public NonPositioningLayoutWidget alignSelfCenterY() {
    return this.alignSelfY(Alignment.CENTER);
  }

  public NonPositioningLayoutWidget alignSelfBottom() {
    return this.alignSelfY(Alignment.END);
  }

  public NonPositioningLayoutWidget alignSelf(Alignment alignX, Alignment alignY) {
    this.alignSelfX = alignX;
    this.alignSelfY = alignY;
    return this;
  }

  public <T extends Widget> T add(T widget) {
    return this.add(widget, null);
  }

  public <T extends Widget> T add(
      T widget, LayoutHookWithParent<NonPositioningLayoutWidget, T> layoutHook
  ) {
    CellWidget<T> cell = new CellWidget<>(widget);
    this.cells.add(cell);

    if (layoutHook != null) {
      cell.layoutHook(layoutHook);
    }

    this.refreshPositions();
    return widget;
  }

  public void clearChildren() {
    this.cells.clear();
  }

  public List<Widget> getChildren() {
    return this.cells.stream().map((cell) -> (Widget) cell.getWidget()).toList();
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
    this.getChildren().forEach(consumer);
  }

  @Override
  public void refreshPositions() {
    this.cells.forEach((cell) -> cell.onLayout(this));
    super.refreshPositions();
  }

  @Override
  public int getX() {
    return this.alignSelfX.getPos(super.getX(), this.getWidth());
  }

  @Override
  public int getY() {
    return this.alignSelfY.getPos(super.getY(), this.getHeight());
  }

  @Environment(EnvType.CLIENT)
  private static class CellWidget<T extends Widget> implements Widget {
    private final T widget;

    private LayoutHookWithParent<NonPositioningLayoutWidget, T> layoutHook = null;

    public CellWidget(T widget) {
      this.widget = widget;
    }

    public T getWidget() {
      return this.widget;
    }

    public void layoutHook(LayoutHookWithParent<NonPositioningLayoutWidget, T> layoutHook) {
      this.layoutHook = layoutHook;
    }

    public void onLayout(NonPositioningLayoutWidget parent) {
      if (this.layoutHook != null) {
        this.layoutHook.run(parent, this.widget);
      }
    }

    @Override
    public void setX(int x) {
      this.widget.setX(x);
    }

    @Override
    public void setY(int y) {
      this.widget.setY(y);
    }

    @Override
    public int getX() {
      return this.widget.getX();
    }

    @Override
    public int getY() {
      return this.widget.getY();
    }

    @Override
    public int getWidth() {
      return this.widget.getWidth();
    }

    @Override
    public int getHeight() {
      return this.widget.getHeight();
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
      if (this.widget instanceof ClickableWidget clickableWidget) {
        consumer.accept(clickableWidget);
      }
    }
  }
}
