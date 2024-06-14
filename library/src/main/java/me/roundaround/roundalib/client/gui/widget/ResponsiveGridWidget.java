package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.Spacing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ResponsiveGridWidget implements LayoutWidget {
  private final List<CellWidget<?>> cells = new ArrayList<>();
  private final Positioner positioner = Positioner.create();
  private final Axis flowAxis;

  private int x;
  private int y;
  private int width;
  private int height;
  private int columnWidth;
  private int rowHeight;
  private int rowSpacing = GuiUtil.PADDING;
  private int columnSpacing = GuiUtil.PADDING;
  private Spacing margin = Spacing.zero();

  public ResponsiveGridWidget(int width, int height, int columnWidth, int rowHeight) {
    this(0, 0, width, height, columnWidth, rowHeight, Axis.HORIZONTAL);
  }

  public ResponsiveGridWidget(int x, int y, int width, int height, int columnWidth, int rowHeight) {
    this(x, y, width, height, columnWidth, rowHeight, Axis.HORIZONTAL);
  }

  public ResponsiveGridWidget(int width, int height, int columnWidth, int rowHeight, Axis flowAxis) {
    this(0, 0, width, height, columnWidth, rowHeight, flowAxis);
  }

  public ResponsiveGridWidget(int x, int y, int width, int height, int columnWidth, int rowHeight, Axis flowAxis) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.columnWidth = columnWidth;
    this.rowHeight = rowHeight;
    this.flowAxis = flowAxis;
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
    this.cells.stream().map(CellWidget::getChild).forEach(consumer);
  }

  @Override
  public void refreshPositions() {
    LayoutWidget.super.refreshPositions();

    int x = this.margin.left();
    int y = this.margin.top();

    for (CellWidget<?> cell : this.cells) {
      cell.setPosition(x, y, this.columnWidth, this.rowHeight);

      if (this.flowAxis == Axis.HORIZONTAL) {
        x += this.columnSpacing + this.columnWidth;
        if (x > this.width - this.margin.right()) {
          x = this.margin.left();
          y += this.rowSpacing + this.rowHeight;
        }
      } else {
        y += this.rowSpacing + this.rowHeight;
        if (y > this.height - this.margin.top()) {
          y = this.margin.top();
          x += this.columnSpacing + this.columnWidth;
        }
      }
    }
  }

  @Override
  public void setX(int x) {
    this.x = x;
  }

  @Override
  public void setY(int y) {
    this.y = y;
  }

  @Override
  public int getX() {
    return this.x;
  }

  @Override
  public int getY() {
    return this.y;
  }

  @Override
  public int getWidth() {
    return this.width;
  }

  @Override
  public int getHeight() {
    return this.height;
  }

  public ResponsiveGridWidget setRowSpacing(int rowSpacing) {
    this.rowSpacing = rowSpacing;
    return this;
  }

  public ResponsiveGridWidget setColumnSpacing(int columnSpacing) {
    this.columnSpacing = columnSpacing;
    return this;
  }

  public ResponsiveGridWidget setSpacing(int spacing) {
    this.rowSpacing = spacing;
    this.columnSpacing = spacing;
    return this;
  }

  public ResponsiveGridWidget setMargin(Spacing margin) {
    this.margin = margin;
    return this;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public void setDimensions(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void setColumnWidth(int columnWidth) {
    this.columnWidth = columnWidth;
  }

  public void setRowHeight(int rowHeight) {
    this.rowHeight = rowHeight;
  }

  public void setCellDimensions(int columnWidth, int rowHeight) {
    this.columnWidth = columnWidth;
    this.rowHeight = rowHeight;
  }

  public Positioner copyPositioner() {
    return this.positioner.copy();
  }

  public Positioner getPositioner() {
    return this.positioner;
  }

  public <T extends Widget> CellWidget<T> add(T widget) {
    return this.add(widget, this.copyPositioner());
  }

  public <T extends Widget> CellWidget<T> add(T widget, Consumer<Positioner> consumer) {
    return this.add(widget, Util.make(this.copyPositioner(), consumer));
  }

  public <T extends Widget> CellWidget<T> add(T widget, Positioner positioner) {
    CellWidget<T> cell = new CellWidget<>(widget, positioner);
    this.cells.add(cell);
    return cell;
  }

  @Environment(EnvType.CLIENT)
  public enum Axis {
    HORIZONTAL, VERTICAL;
  }

  @Environment(EnvType.CLIENT)
  public static class CellWidget<T extends Widget> {
    private final T child;
    private final Positioner.Impl positioner;

    public CellWidget(T child, Positioner positioner) {
      this.child = child;
      this.positioner = positioner.toImpl();
    }

    public T getChild() {
      return this.child;
    }

    public void setPosition(int x, int y, int width, int height) {
      int marginLeft = this.positioner.marginLeft;
      int childLeft = x + width - this.child.getWidth() - this.positioner.marginRight;
      int offsetX = MathHelper.lerp(this.positioner.relativeX, marginLeft, childLeft);

      int marginTop = this.positioner.marginTop;
      int childTop = y + height - this.child.getHeight() - this.positioner.marginBottom;
      int offsetY = MathHelper.lerp(this.positioner.relativeY, marginTop, childTop);

      this.child.setPosition(x + offsetX, y + offsetY);
      if (this.child instanceof LayoutWidget layout) {
        layout.refreshPositions();
      }
    }
  }
}
