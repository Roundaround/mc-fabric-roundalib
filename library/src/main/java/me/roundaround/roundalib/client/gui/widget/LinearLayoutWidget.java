package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.client.gui.layout.Spacing;
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
  private final List<CellWidget<?>> cells = new ArrayList<>();

  private Axis flowAxis;
  private Spacing padding;
  private float alignX;
  private float alignY;
  private int spacing;

  private int contentWidth;
  private int contentHeight;

  public LinearLayoutWidget(Axis flowAxis) {
    this(flowAxis, 0, 0, 0, 0);
  }

  public LinearLayoutWidget(Axis flowAxis, int width, int height) {
    this(flowAxis, 0, 0, width, height);
  }

  private LinearLayoutWidget(
      Axis flowAxis, int x, int y, int width, int height) {
    super(x, y, width, height);

    this.flowAxis = flowAxis;
  }

  public static LinearLayoutWidget horizontal() {
    return new LinearLayoutWidget(Axis.HORIZONTAL);
  }

  public static LinearLayoutWidget vertical() {
    return new LinearLayoutWidget(Axis.VERTICAL);
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
  public void refreshPositions() {
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
      int offPos =
          offStart - (int) (this.getCellOff(cell) * offRelative * cell.positioner.relative);
      int off = offPos + cell.positioner.marginStartOff;

      switch (this.flowAxis) {
        case HORIZONTAL -> cell.setPosition(main, off);
        case VERTICAL -> cell.setPosition(off, main);
      }

      mainPos += this.getCellMain(cell) + this.spacing;
    }

    super.refreshPositions();
  }

  private int getCellMain(CellWidget<?> cell) {
    return this.flowAxis.getDimensionMain(cell) + cell.positioner.marginStartMain +
        cell.positioner.marginEndMain;
  }

  private int getCellOff(CellWidget<?> cell) {
    return this.flowAxis.getDimensionOff(cell) + cell.positioner.marginStartOff +
        cell.positioner.marginEndOff;
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
  private static class CellWidget<T extends Widget> implements Widget {
    private final T child;

    private Spacing margin = Spacing.zero();
    private Float alignment = null;

    public CellWidget(T child) {
      this.child = child;
    }

    public T getChild() {
      return this.child;
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
}
