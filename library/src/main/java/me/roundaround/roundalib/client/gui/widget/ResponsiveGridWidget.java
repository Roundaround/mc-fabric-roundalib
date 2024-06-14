package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.client.gui.GuiUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ResponsiveGridWidget extends ContainerWidget implements LayoutWidget {
  private final List<CellWidget<?>> cells = new ArrayList<>();
  private final List<Widget> widgets = new ArrayList<>();
  private final Positioner positioner = Positioner.create();
  private final Axis flowAxis;

  private int columnWidth;
  private int rowHeight;
  private int rowSpacing = GuiUtil.PADDING;
  private int columnSpacing = GuiUtil.PADDING;

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
    super(x, y, width, height, ScreenTexts.EMPTY);

    this.columnWidth = columnWidth;
    this.rowHeight = rowHeight;
    this.flowAxis = flowAxis;
  }

  @Override
  public List<? extends Element> children() {
    return this.widgets.stream()
        .filter((widget) -> widget instanceof Element)
        .map((widget) -> (Element) widget)
        .toList();
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
    this.widgets.forEach(consumer);
  }

  @Override
  public void refreshPositions() {
    LayoutWidget.super.refreshPositions();

    int maxCount = this.maxCountInMainAxis();
    int main = 0;
    int other = 0;

    for (CellWidget<?> cell : this.cells) {
      int column = this.flowAxis == Axis.HORIZONTAL ? main : other;
      int row = this.flowAxis == Axis.HORIZONTAL ? other : main;

      cell.setPosition(this.xPos(column), this.yPos(row), this.columnWidth, this.rowHeight);

      main++;
      if (main > maxCount - 1) {
        main = 0;
        other++;
      }
    }
  }

  private int maxCountInMainAxis() {
    return switch (this.flowAxis) {
      case HORIZONTAL -> (this.width + this.columnSpacing) / (this.columnWidth + this.columnSpacing);
      case VERTICAL -> (this.height + this.rowSpacing) / (this.rowHeight + this.rowSpacing);
    };
  }

  private int xPos(int column) {
    return this.getX() + column * (this.columnWidth + this.columnSpacing);
  }

  private int yPos(int row) {
    return this.getY() + row * (this.rowHeight + this.rowSpacing);
  }

  @Override
  protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    // TODO: This, getNavigationPath, etc should probably all get implemented.
  }

  @Override
  protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    this.widgets.forEach((widget) -> {
      if (widget instanceof Drawable drawable) {
        drawable.render(context, mouseX, mouseY, delta);
      }
    });
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
    this.widgets.add(widget);
    CellWidget<T> cell = new CellWidget<>(widget, positioner);
    this.cells.add(cell);
    return cell;
  }

  public void clear() {
    this.widgets.clear();
    this.cells.clear();
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
