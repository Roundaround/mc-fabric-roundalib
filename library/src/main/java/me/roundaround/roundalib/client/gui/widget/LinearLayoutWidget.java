package me.roundaround.roundalib.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class LinearLayoutWidget extends SizableLayoutWidget {
  private final Positioner positioner = Positioner.create();
  private final List<Widget> widgets = new ArrayList<>();
  private final List<CellWidget<?>> cells = new ArrayList<>();

  private Axis flowAxis;
  private int spacing;
  private Float relative = null;

  public LinearLayoutWidget(Axis flowAxis, DimensionsSupplier dimensionsSupplier) {
    this(flowAxis, 0, 0, 0, 0, dimensionsSupplier);
  }

  public LinearLayoutWidget(Axis flowAxis, int width, int height) {
    this(flowAxis, 0, 0, width, height);
  }

  public LinearLayoutWidget(Axis flowAxis, int x, int y, int width, int height) {
    this(flowAxis, x, y, width, height, null);
  }

  private LinearLayoutWidget(
      Axis flowAxis, int x, int y, int width, int height, DimensionsSupplier dimensionsSupplier
  ) {
    super(x, y, width, height, dimensionsSupplier);

    this.flowAxis = flowAxis;
  }

  public static LinearLayoutWidget horizontal() {
    return horizontal(null);
  }

  public static LinearLayoutWidget horizontal(DimensionsSupplier dimensionsSupplier) {
    return new LinearLayoutWidget(Axis.HORIZONTAL, dimensionsSupplier);
  }

  public static LinearLayoutWidget vertical() {
    return vertical(null);
  }

  public static LinearLayoutWidget vertical(DimensionsSupplier dimensionsSupplier) {
    return new LinearLayoutWidget(Axis.VERTICAL, dimensionsSupplier);
  }

  public <T extends Widget> T add(T widget) {
    return this.add(widget, LayoutHookWithRefs.noop());
  }

  public <T extends Widget> T add(T widget, Consumer<Positioner> consumer) {
    return this.add(widget, Util.make(this.copyPositioner(), consumer));
  }

  public <T extends Widget> T add(T widget, Positioner positioner) {
    return this.add(widget, LayoutHookWithRefs.noop(), positioner);
  }

  public <T extends Widget> T add(T widget, LayoutHookWithRefs<LinearLayoutWidget, T> layoutHook) {
    return this.add(widget, layoutHook, this.copyPositioner());
  }

  public <T extends Widget> T add(
      T widget, LayoutHookWithRefs<LinearLayoutWidget, T> layoutHook, Consumer<Positioner> consumer
  ) {
    return this.add(widget, layoutHook, Util.make(this.copyPositioner(), consumer));
  }

  public <T extends Widget> T add(
      T widget, LayoutHookWithRefs<LinearLayoutWidget, T> layoutHook, Positioner positioner
  ) {
    this.widgets.add(widget);
    this.cells.add(new CellWidget<>(widget, layoutHook, positioner));
    return widget;
  }

  public List<Widget> getChildren() {
    return this.widgets;
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
    this.widgets.forEach(consumer);
  }

  @Override
  public void beforeRefreshPositions() {
    super.beforeRefreshPositions();

    int offSize = this.flowAxis.getOffDimension(this);
    int pos = this.getRelativeOffset() - this.spacing;

    for (CellWidget<?> cell : this.cells) {
      cell.onLayout(this);

      int main = pos + this.spacing + this.flowAxis.getLeadingMargin(cell.positioner);

      int offRef = (int) ((offSize - this.flowAxis.getOffDimension(cell)) *
          this.flowAxis.getOffRelative(cell.positioner));
      int off = offRef + this.flowAxis.getOffLeadingMargin(cell.positioner);

      this.flowAxis.setPosition(cell, main, off);

      pos += this.flowAxis.getDimension(cell) + this.spacing;
    }
  }

  public void setFlowAxis(Axis flowAxis) {
    this.flowAxis = flowAxis;
  }

  public Axis getFlowAxis() {
    return this.flowAxis;
  }

  public Positioner copyPositioner() {
    return this.positioner.copy();
  }

  public Positioner getMainPositioner() {
    return this.positioner;
  }

  public LinearLayoutWidget spacing(int spacing) {
    this.spacing = spacing;
    return this;
  }

  public int getSpacing() {
    return this.spacing;
  }

  public LinearLayoutWidget alignedStart() {
    return this.aligned(0f);
  }

  public LinearLayoutWidget centered() {
    return this.aligned(0.5f);
  }

  public LinearLayoutWidget alignedEnd() {
    return this.aligned(1f);
  }

  public LinearLayoutWidget aligned(float relative) {
    this.relative = relative;
    return this;
  }

  public Float getRelative() {
    return this.relative;
  }

  protected int getRelativeOffset() {
    if (this.relative == null || this.widgets.isEmpty()) {
      return 0;
    }

    int totalSpacing = Math.max(0, this.widgets.size() - 1) * this.spacing;
    int contentSize = this.cells.stream().mapToInt(this.flowAxis::getDimension).sum() + totalSpacing;

    return (int) ((this.flowAxis.getDimension(this) - contentSize) * this.relative);
  }

  @Environment(EnvType.CLIENT)
  public enum Axis {
    HORIZONTAL, VERTICAL;

    public int getDimension(Widget widget) {
      return switch (this) {
        case HORIZONTAL -> widget.getWidth();
        case VERTICAL -> widget.getHeight();
      };
    }

    public int getOffDimension(Widget widget) {
      return switch (this) {
        case VERTICAL -> widget.getWidth();
        case HORIZONTAL -> widget.getHeight();
      };
    }

    public int getLeadingMargin(Positioner.Impl positioner) {
      return switch (this) {
        case HORIZONTAL -> positioner.marginLeft;
        case VERTICAL -> positioner.marginTop;
      };
    }

    public float getOffRelative(Positioner.Impl positioner) {
      return switch (this) {
        case HORIZONTAL -> positioner.relativeY;
        case VERTICAL -> positioner.relativeX;
      };
    }

    public int getOffLeadingMargin(Positioner.Impl positioner) {
      return switch (this) {
        case VERTICAL -> positioner.marginLeft;
        case HORIZONTAL -> positioner.marginTop;
      };
    }

    public void setPosition(Widget widget, int main, int off) {
      switch (this) {
        case HORIZONTAL -> widget.setPosition(main, off);
        case VERTICAL -> widget.setPosition(off, main);
      }
    }
  }

  @Environment(EnvType.CLIENT)
  public static class CellWidget<T extends Widget> implements Widget {
    private final T child;
    private final LayoutHookWithRefs<LinearLayoutWidget, T> layoutHook;
    private final Positioner.Impl positioner;

    public CellWidget(T child, LayoutHookWithRefs<LinearLayoutWidget, T> layoutHook, Positioner positioner) {
      this.child = child;
      this.layoutHook = layoutHook;
      this.positioner = positioner.toImpl();
    }

    public T getChild() {
      return this.child;
    }

    public void onLayout(LinearLayoutWidget parent) {
      this.layoutHook.run(parent, this.getChild());
    }

    @Override
    public void setX(int x) {
      this.child.setX(x);
    }

    @Override
    public void setY(int y) {
      this.child.setY(y);
    }

    @Override
    public int getX() {
      return this.child.getX();
    }

    @Override
    public int getY() {
      return this.child.getY();
    }

    @Override
    public int getWidth() {
      return this.child.getWidth() + this.positioner.marginLeft + this.positioner.marginRight;
    }

    @Override
    public int getHeight() {
      return this.child.getHeight() + this.positioner.marginTop + this.positioner.marginBottom;
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
      if (this.child instanceof ClickableWidget clickableWidget) {
        consumer.accept(clickableWidget);
      }
    }
  }
}
