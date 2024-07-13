package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.client.gui.layout.Coords;
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
public class LinearLayoutWidget extends SizableLayoutWidget<LinearLayoutWidget> {
  private final Positioner positioner = Positioner.create();
  private final List<CellWidget<?>> cells = new ArrayList<>();

  private Axis flowAxis;
  private int spacing;
  private float relative;
  private int totalSpacing;
  private int contentMain;
  private int contentOff;

  public LinearLayoutWidget(Axis flowAxis, LayoutHook<LinearLayoutWidget> layoutHook) {
    this(flowAxis, 0, 0, 0, 0, layoutHook);
  }

  public LinearLayoutWidget(Axis flowAxis, int width, int height) {
    this(flowAxis, 0, 0, width, height);
  }

  public LinearLayoutWidget(Axis flowAxis, int x, int y, int width, int height) {
    this(flowAxis, x, y, width, height, null);
  }

  private LinearLayoutWidget(
      Axis flowAxis, int x, int y, int width, int height, LayoutHook<LinearLayoutWidget> layoutHook
  ) {
    super(x, y, width, height, layoutHook);

    this.flowAxis = flowAxis;
  }

  public static LinearLayoutWidget horizontal() {
    return horizontal(null);
  }

  public static LinearLayoutWidget horizontal(LayoutHook<LinearLayoutWidget> layoutHook) {
    return new LinearLayoutWidget(Axis.HORIZONTAL, layoutHook);
  }

  public static LinearLayoutWidget vertical() {
    return vertical(null);
  }

  public static LinearLayoutWidget vertical(LayoutHook<LinearLayoutWidget> layoutHook) {
    return new LinearLayoutWidget(Axis.VERTICAL, layoutHook);
  }

  public <T extends Widget> T add(T widget) {
    return this.add(widget, LayoutHookWithParent.noop());
  }

  public <T extends Widget> T add(T widget, Consumer<Positioner> consumer) {
    return this.add(widget, Util.make(this.copyPositioner(), consumer));
  }

  public <T extends Widget> T add(T widget, Positioner positioner) {
    return this.add(widget, LayoutHookWithParent.noop(), positioner);
  }

  public <T extends Widget> T add(T widget, LayoutHookWithParent<LinearLayoutWidget, T> layoutHook) {
    return this.add(widget, layoutHook, this.copyPositioner());
  }

  public <T extends Widget> T add(
      T widget, LayoutHookWithParent<LinearLayoutWidget, T> layoutHook, Consumer<Positioner> consumer
  ) {
    return this.add(widget, layoutHook, Util.make(this.copyPositioner(), consumer));
  }

  public <T extends Widget> T add(
      T widget, LayoutHookWithParent<LinearLayoutWidget, T> layoutHook, Positioner positioner
  ) {
    this.cells.add(new CellWidget<>(widget, layoutHook, positioner));
    this.calculateContentDimensions();
    return widget;
  }

  public List<Widget> getChildren() {
    return this.cells.stream().map((cell) -> (Widget) cell.getChild()).toList();
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
    this.getChildren().forEach(consumer);
  }

  private void calculateContentDimensions() {
    this.totalSpacing = Math.max(0, this.cells.size() - 1) * this.spacing;
    this.contentMain = this.cells.stream().mapToInt(this.flowAxis::getDimension).sum() + this.totalSpacing;
    this.contentOff = this.cells.stream().mapToInt(this.flowAxis::getOffDimension).max().orElse(0);
  }

  @Override
  public void beforeRefreshPositions() {
    super.beforeRefreshPositions();
    this.calculateContentDimensions();

    int mainStart = (int) -(this.flowAxis.getDimension(this) * this.relative);
    int offSize = this.flowAxis.getOffDimension(this);
    int mainRef = mainStart - this.spacing;

    for (CellWidget<?> cell : this.cells) {
      cell.onLayout(this);

      int main = mainRef + this.spacing + this.flowAxis.getLeadingMargin(cell.positioner);

      int offRef = (int) ((offSize - this.flowAxis.getOffDimension(cell)) *
          this.flowAxis.getOffRelative(cell.positioner));
      int off = offRef + this.flowAxis.getOffLeadingMargin(cell.positioner);

      this.flowAxis.setPosition(cell, this.getX(), this.getY(), main, off);

      mainRef += this.flowAxis.getDimension(cell) + this.spacing;
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

  public LinearLayoutWidget alignedStartMain() {
    return this.alignedMain(0f);
  }

  public LinearLayoutWidget centeredMain() {
    return this.alignedMain(0.5f);
  }

  public LinearLayoutWidget alignedEndMain() {
    return this.alignedMain(1f);
  }

  public LinearLayoutWidget alignedMain(float relative) {
    this.relative = relative;
    return this;
  }

  public LinearLayoutWidget alignedStartOff() {
    return this.alignedOff(0f);
  }

  public LinearLayoutWidget centeredOff() {
    return this.alignedOff(0.5f);
  }

  public LinearLayoutWidget alignedEndOff() {
    return this.alignedOff(1f);
  }

  public LinearLayoutWidget alignedOff(float relative) {
    switch (this.flowAxis) {
      case HORIZONTAL -> this.positioner.relativeY(relative);
      case VERTICAL -> this.positioner.relativeX(relative);
    }
    return this;
  }

  public Float getRelative() {
    return this.relative;
  }

  public int getTotalSpacing() {
    return this.totalSpacing;
  }

  public int getContentMain() {
    return this.contentMain;
  }

  public int getContentOff() {
    return this.contentOff;
  }

  @Override
  public int getWidth() {
    return this.width != 0 ? this.width : this.flowAxis.getWidth(this.contentMain, this.contentOff);
  }

  @Override
  public int getHeight() {
    return this.height != 0 ? this.height : this.flowAxis.getHeight(this.contentMain, this.contentOff);
  }

  @Environment(EnvType.CLIENT)
  public enum Axis {
    HORIZONTAL, VERTICAL;

    public Coords getCoords(int main, int off) {
      return switch (this) {
        case HORIZONTAL -> Coords.of(main, off);
        case VERTICAL -> Coords.of(off, main);
      };
    }

    public int getWidth(int main, int off) {
      return switch (this) {
        case HORIZONTAL -> main;
        case VERTICAL -> off;
      };
    }

    public int getHeight(int main, int off) {
      return switch (this) {
        case HORIZONTAL -> off;
        case VERTICAL -> main;
      };
    }

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

    public void setPosition(Widget widget, int baseX, int baseY, int main, int off) {
      switch (this) {
        case HORIZONTAL -> widget.setPosition(baseX + main, baseY + off);
        case VERTICAL -> widget.setPosition(baseX + off, baseY + main);
      }
    }
  }

  @Environment(EnvType.CLIENT)
  public static class CellWidget<T extends Widget> implements Widget {
    private final T child;
    private final LayoutHookWithParent<LinearLayoutWidget, T> layoutHook;
    private final Positioner.Impl positioner;

    public CellWidget(T child, LayoutHookWithParent<LinearLayoutWidget, T> layoutHook, Positioner positioner) {
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
