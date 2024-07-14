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

  public <T extends Widget> T add(T widget, Consumer<CellPositioner> consumer) {
    return this.add(widget, Util.make(this.cellPositioner.copy(), consumer));
  }

  public <T extends Widget> T add(T widget, CellPositioner positioner) {
    return this.add(widget, LayoutHookWithParent.noop(), positioner);
  }

  public <T extends Widget> T add(T widget, LayoutHookWithParent<LinearLayoutWidget, T> layoutHook) {
    return this.add(widget, layoutHook, this.cellPositioner.copy());
  }

  public <T extends Widget> T add(
      T widget, LayoutHookWithParent<LinearLayoutWidget, T> layoutHook, Consumer<CellPositioner> consumer
  ) {
    return this.add(widget, layoutHook, Util.make(this.cellPositioner.copy(), consumer));
  }

  public <T extends Widget> T add(
      T widget, LayoutHookWithParent<LinearLayoutWidget, T> layoutHook, CellPositioner positioner
  ) {
    this.cells.add(new CellWidget<>(this.flowAxis, widget, layoutHook, positioner));
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
    this.contentMain = this.cells.stream().mapToInt(this.flowAxis::getDimensionMain).sum() + this.totalSpacing;
    this.contentOff = this.cells.stream().mapToInt(this.flowAxis::getDimensionOff).max().orElse(0);
  }

  @Override
  public void beforeRefreshPositions() {
    super.beforeRefreshPositions();
    this.calculateContentDimensions();

    int mainStart = this.flowAxis.getPosMain(this) - (int) (this.flowAxis.getDimensionMain(this) * this.relative);
    int mainRef = mainStart - this.spacing;

    int offStart = this.flowAxis.getPosOff(this) - this.flowAxis.getDimensionOff(this);

    for (CellWidget<?> cell : this.cells) {
      cell.onLayout(this);

      int main = mainRef + this.spacing + this.flowAxis.getLeadingMargin(cell.positioner);

      int offRef = (int) ((offSize - this.flowAxis.getDimensionOff(cell)) *
          this.flowAxis.getOffRelative(cell.positioner));
      int off = offRef + this.flowAxis.getOffLeadingMargin(cell.positioner);

      this.flowAxis.setPosition(cell, this.getX(), this.getY(), main, off);

      mainRef += this.flowAxis.getDimensionMain(cell) + this.spacing;
    }
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

  public int getContentMain() {
    return this.contentMain;
  }

  public int getContentOff() {
    return this.contentOff;
  }

  @Override
  public int getWidth() {
    int baseWidth = this.width != 0 ? this.width : this.flowAxis.getWidth(this.contentMain, this.contentOff);
    return baseWidth + this.positioner.
  }

  @Override
  public int getHeight() {
    int baseHeight = this.height != 0 ? this.height : this.flowAxis.getHeight(this.contentMain, this.contentOff);
    return baseHeight + this.margin.getVertical();
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

    public int getPosMain(Widget widget) {
      return switch (this) {
        case HORIZONTAL -> widget.getX();
        case VERTICAL -> widget.getY();
      };
    }

    public int getPosOff(Widget widget) {
      return switch (this) {
        case HORIZONTAL -> widget.getY();
        case VERTICAL -> widget.getX();
      };
    }

    public int getWidth(int main, int off) {
      return switch (this) {
        case HORIZONTAL -> main;
        case VERTICAL -> off;
      };
    }

    public int getWidth(CellPositioner positioner) {
      return switch (this) {
        case HORIZONTAL -> positioner.marginStartMain + positioner.marginEndMain;
        case VERTICAL -> positioner.marginStartOff + positioner.marginEndOff;
      };
    }

    public int getHeight(int main, int off) {
      return switch (this) {
        case HORIZONTAL -> off;
        case VERTICAL -> main;
      };
    }

    public int getHeight(CellPositioner positioner) {
      return switch (this) {
        case HORIZONTAL -> positioner.marginStartOff + positioner.marginEndOff;
        case VERTICAL -> positioner.marginStartMain + positioner.marginEndMain;
      };
    }

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
    private final CellPositioner positioner;

    private Axis flowAxis;

    public CellWidget(
        Axis flowAxis, T child, LayoutHookWithParent<LinearLayoutWidget, T> layoutHook, CellPositioner positioner
    ) {
      this.flowAxis = flowAxis;
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
      return this.child.getWidth() + this.flowAxis.getWidth(this.positioner);
    }

    @Override
    public int getHeight() {
      return this.child.getHeight() + this.flowAxis.getHeight(this.positioner);
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
      if (this.child instanceof ClickableWidget clickableWidget) {
        consumer.accept(clickableWidget);
      }
    }

    public void setFlowAxis(Axis flowAxis) {
      this.flowAxis = flowAxis;
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
      this.marginStartMain = main;
      this.marginEndMain = main;
      return this;
    }

    public CellPositioner marginOff(int off) {
      this.marginStartOff = off;
      this.marginEndOff = off;
      return this;
    }

    public CellPositioner copy() {
      return new CellPositioner(this);
    }
  }

  @Environment(EnvType.CLIENT)
  @FunctionalInterface
  public interface CellLayoutHook<T extends Widget> {
    void run(LinearLayoutWidget parent, T self, CellPositioner positioner);
  }
}
