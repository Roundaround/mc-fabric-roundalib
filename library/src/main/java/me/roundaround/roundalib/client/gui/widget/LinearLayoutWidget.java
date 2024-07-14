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
public class LinearLayoutWidget extends SizableLayoutWidget<LinearLayoutWidget> {
  private final List<CellWidget<?>> cells = new ArrayList<>();
  private final Positioner.Impl positioner = Positioner.create().toImpl();
  private final CellPositioner cellPositioner = new CellPositioner();

  private Axis flowAxis;
  private int spacing;
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
      Axis flowAxis,
      int x,
      int y,
      int width,
      int height,
      LayoutHook<LinearLayoutWidget> layoutHook) {
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

  public <T extends Widget> T add(T widget, Consumer<CellPositioner> consumer) {
    return this.add(widget, Util.make(this.cellPositioner.copy(), consumer));
  }

  public <T extends Widget> T add(T widget, CellPositioner positioner) {
    return this.add(widget, LayoutHookWithParent.noop(), positioner);
  }

  public <T extends Widget> T add(
      T widget, LayoutHookWithParent<LinearLayoutWidget, T> layoutHook) {
    return this.add(widget, layoutHook, this.cellPositioner.copy());
  }

  public <T extends Widget> T add(
      T widget,
      LayoutHookWithParent<LinearLayoutWidget, T> layoutHook,
      Consumer<CellPositioner> consumer) {
    return this.add(widget, layoutHook, Util.make(this.cellPositioner.copy(), consumer));
  }

  public <T extends Widget> T add(
      T widget, LayoutHookWithParent<LinearLayoutWidget, T> layoutHook, CellPositioner positioner) {
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
    this.contentMain = this.cells.stream().mapToInt(this::getCellMain).sum() + this.totalSpacing;
    this.contentOff = this.cells.stream().mapToInt(this::getCellOff).max().orElse(0);
  }

  @Override
  public void beforeRefreshPositions() {
    super.beforeRefreshPositions();
    this.calculateContentDimensions();

    int mainStart = switch (this.flowAxis) {
      case HORIZONTAL -> this.getStartX();
      case VERTICAL -> this.getStartY();
    };
    int offStart = switch (this.flowAxis) {
      case HORIZONTAL -> this.getStartY();
      case VERTICAL -> this.getStartX();
    };

    int mainPos = mainStart - this.spacing;
    for (CellWidget<?> cell : this.cells) {
      cell.onLayout(this);

      int main = mainPos + this.spacing + cell.positioner.marginStartMain;

      float offRelative = switch (this.flowAxis) {
        case HORIZONTAL -> this.positioner.relativeY;
        case VERTICAL -> this.positioner.relativeX;
      };
      int offPos = offStart - (int) (this.getCellOff(cell) * offRelative * cell.positioner.relative);
      int off = offPos + cell.positioner.marginStartOff;

      switch (this.flowAxis) {
        case HORIZONTAL -> cell.setPosition(main, off);
        case VERTICAL -> cell.setPosition(off, main);
      }

      mainPos += this.getCellMain(cell) + this.spacing;
    }
  }

  private int getCellMain(CellWidget<?> cell) {
    return this.flowAxis.getDimensionMain(cell) + cell.positioner.marginStartMain + cell.positioner.marginEndMain;
  }

  private int getCellOff(CellWidget<?> cell) {
    return this.flowAxis.getDimensionOff(cell) + cell.positioner.marginStartOff + cell.positioner.marginEndOff;
  }

  private int getStartX() {
    return this.getX() - (int) (this.getWidth() * this.positioner.relativeX);
  }

  private int getStartY() {
    return this.getY() - (int) (this.getHeight() * this.positioner.relativeY);
  }

  public void setFlowAxis(Axis flowAxis) {
    this.flowAxis = flowAxis;
  }

  public Axis getFlowAxis() {
    return this.flowAxis;
  }

  public Positioner getMainPositioner() {
    return this.positioner;
  }

  public CellPositioner getCellPositioner() {
    return this.cellPositioner;
  }

  public LinearLayoutWidget spacing(int spacing) {
    this.spacing = spacing;
    return this;
  }

  public int getSpacing() {
    return this.spacing;
  }

  public int getTotalSpacing() {
    return this.totalSpacing;
  }

  public int getContentWidth() {
    return switch (this.flowAxis) {
      case HORIZONTAL -> this.contentMain;
      case VERTICAL -> this.contentOff;
    };
  }

  public int getContentHeight() {
    return switch (this.flowAxis) {
      case HORIZONTAL -> this.contentOff;
      case VERTICAL -> this.contentMain;
    };
  }

  @Override
  public int getWidth() {
    int baseWidth = this.width != 0 ? this.width : this.getContentWidth();
    return baseWidth + this.getMarginX();
  }

  @Override
  public int getHeight() {
    int baseHeight = this.height != 0 ? this.height : this.getContentHeight();
    return baseHeight + this.getMarginY();
  }

  private int getMarginX() {
    return this.positioner.marginLeft + this.positioner.marginRight;
  }

  private int getMarginY() {
    return this.positioner.marginTop + this.positioner.marginBottom;
  }

  @Environment(EnvType.CLIENT)
  public enum Axis {
    HORIZONTAL,
    VERTICAL;

    public int getDimensionMain(Widget widget) {
      return switch (this) {
        case HORIZONTAL -> widget.getWidth();
        case VERTICAL -> widget.getHeight();
      };
    }

    public int getDimensionOff(Widget widget) {
      return switch (this) {
        case VERTICAL -> widget.getWidth();
        case HORIZONTAL -> widget.getHeight();
      };
    }
  }

  @Environment(EnvType.CLIENT)
  public static class CellWidget<T extends Widget> implements Widget {
    private final T child;
    private final LayoutHookWithParent<LinearLayoutWidget, T> layoutHook;
    private final CellPositioner positioner;

    public CellWidget(
        T child,
        LayoutHookWithParent<LinearLayoutWidget, T> layoutHook,
        CellPositioner positioner) {
      this.child = child;
      this.layoutHook = layoutHook;
      this.positioner = positioner;
    }

    public T getChild() {
      return this.child;
    }

    public void onLayout(LinearLayoutWidget parent) {
      this.layoutHook.run(parent, this.getChild());
    }

    public CellPositioner getPositioner() {
      return this.positioner;
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
      return this.child.getWidth();
    }

    @Override
    public int getHeight() {
      return this.child.getHeight();
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
      if (this.child instanceof ClickableWidget clickableWidget) {
        consumer.accept(clickableWidget);
      }
    }
  }

  @Environment(EnvType.CLIENT)
  public static class CellPositioner {
    private float relative;
    private int marginStartMain;
    private int marginEndMain;
    private int marginStartOff;
    private int marginEndOff;

    public CellPositioner() {
    }

    public CellPositioner(CellPositioner other) {
      this.relative = other.relative;
      this.marginStartMain = other.marginStartMain;
      this.marginEndMain = other.marginEndMain;
      this.marginStartOff = other.marginStartOff;
      this.marginEndOff = other.marginEndOff;
    }

    public CellPositioner alignedStart() {
      return this.aligned(0f);
    }

    public CellPositioner alignedCenter() {
      return this.aligned(0.5f);
    }

    public CellPositioner alignedEnd() {
      return this.aligned(1f);
    }

    public CellPositioner aligned(float relative) {
      this.relative = relative;
      return this;
    }

    public CellPositioner margin(int margin) {
      return this.margin(margin, margin, margin, margin);
    }

    public CellPositioner margin(int main, int off) {
      return this.margin(main, main, off, off);
    }

    public CellPositioner margin(int startMain, int endMain, int startOff, int endOff) {
      this.marginStartMain = startMain;
      this.marginEndMain = endMain;
      this.marginStartOff = startOff;
      this.marginEndOff = endOff;
      return this;
    }

    public CellPositioner marginMain(int main) {
      return this.marginMain(main, main);
    }

    public CellPositioner marginMain(int start, int end) {
      this.marginStartMain = start;
      this.marginEndMain = end;
      return this;
    }

    public CellPositioner marginOff(int off) {
      return this.marginOff(off, off);
    }

    public CellPositioner marginOff(int start, int end) {
      this.marginStartOff = start;
      this.marginEndOff = end;
      return this;
    }

    public CellPositioner copy() {
      return new CellPositioner(this);
    }
  }
}
