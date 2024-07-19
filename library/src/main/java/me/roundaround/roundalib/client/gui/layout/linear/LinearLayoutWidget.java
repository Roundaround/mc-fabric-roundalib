package me.roundaround.roundalib.client.gui.layout.linear;

import me.roundaround.roundalib.client.gui.layout.LayoutHookWithParent;
import me.roundaround.roundalib.client.gui.util.Alignment;
import me.roundaround.roundalib.client.gui.util.Axis;
import me.roundaround.roundalib.client.gui.util.Spacing;
import me.roundaround.roundalib.client.gui.layout.SizableLayoutWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

@Environment(EnvType.CLIENT)
public class LinearLayoutWidget extends SizableLayoutWidget {
  private final List<CellWidget<?>> cells = new ArrayList<>();

  private Axis flowAxis;
  private Alignment alignSelfX = Alignment.START;
  private Alignment alignSelfY = Alignment.START;
  private int spacing;
  private Alignment contentAlignMain = Alignment.START;
  private Alignment defaultContentAlignOff = Alignment.CENTER;
  private Spacing defaultContentMargin = Spacing.zero();

  private int contentWidth;
  private int contentHeight;

  public LinearLayoutWidget(Axis flowAxis) {
    this(flowAxis, 0, 0, 0, 0);
  }

  public LinearLayoutWidget(Axis flowAxis, int width, int height) {
    this(flowAxis, 0, 0, width, height);
  }

  public LinearLayoutWidget(Axis flowAxis, int x, int y, int width, int height) {
    super(x, y, width, height);

    this.flowAxis = flowAxis;
  }

  public static LinearLayoutWidget horizontal() {
    return new LinearLayoutWidget(Axis.HORIZONTAL);
  }

  public static LinearLayoutWidget horizontal(int width, int height) {
    return new LinearLayoutWidget(Axis.HORIZONTAL, width, height);
  }

  public static LinearLayoutWidget horizontal(int x, int y, int width, int height) {
    return new LinearLayoutWidget(Axis.HORIZONTAL, x, y, width, height);
  }

  public static LinearLayoutWidget vertical() {
    return new LinearLayoutWidget(Axis.VERTICAL);
  }

  public static LinearLayoutWidget vertical(int width, int height) {
    return new LinearLayoutWidget(Axis.VERTICAL, width, height);
  }

  public static LinearLayoutWidget vertical(int x, int y, int width, int height) {
    return new LinearLayoutWidget(Axis.VERTICAL, x, y, width, height);
  }

  public LinearLayoutWidget flowAxis(Axis flowAxis) {
    this.flowAxis = flowAxis;
    return this;
  }

  public LinearLayoutWidget alignSelfX(Alignment alignX) {
    this.alignSelfX = alignX;
    return this;
  }

  public LinearLayoutWidget alignSelfLeft() {
    return this.alignSelfX(Alignment.START);
  }

  public LinearLayoutWidget alignSelfCenterX() {
    return this.alignSelfX(Alignment.CENTER);
  }

  public LinearLayoutWidget alignSelfRight() {
    return this.alignSelfX(Alignment.END);
  }

  public LinearLayoutWidget alignSelfY(Alignment alignY) {
    this.alignSelfY = alignY;
    return this;
  }

  public LinearLayoutWidget alignSelfTop() {
    return this.alignSelfY(Alignment.START);
  }

  public LinearLayoutWidget alignSelfCenterY() {
    return this.alignSelfY(Alignment.CENTER);
  }

  public LinearLayoutWidget alignSelfBottom() {
    return this.alignSelfY(Alignment.END);
  }

  public LinearLayoutWidget alignSelf(Alignment alignX, Alignment alignY) {
    this.alignSelfX = alignX;
    this.alignSelfY = alignY;
    return this;
  }

  public LinearLayoutWidget spacing(int spacing) {
    this.spacing = spacing;
    return this;
  }

  public LinearLayoutWidget mainAxisContentAlign(Alignment align) {
    this.contentAlignMain = align;
    return this;
  }

  public LinearLayoutWidget mainAxisContentAlignStart() {
    this.mainAxisContentAlign(Alignment.START);
    return this;
  }

  public LinearLayoutWidget mainAxisContentAlignCenter() {
    this.mainAxisContentAlign(Alignment.CENTER);
    return this;
  }

  public LinearLayoutWidget mainAxisContentAlignEnd() {
    this.mainAxisContentAlign(Alignment.END);
    return this;
  }

  public LinearLayoutWidget defaultOffAxisContentAlign(Alignment align) {
    this.defaultContentAlignOff = align;
    return this;
  }

  public LinearLayoutWidget defaultOffAxisContentAlignStart() {
    return this.defaultOffAxisContentAlign(Alignment.START);
  }

  public LinearLayoutWidget defaultOffAxisContentAlignCenter() {
    return this.defaultOffAxisContentAlign(Alignment.CENTER);
  }

  public LinearLayoutWidget defaultOffAxisContentAlignEnd() {
    return this.defaultOffAxisContentAlign(Alignment.END);
  }

  public LinearLayoutWidget defaultContentMargin(Spacing defaultMargin) {
    this.defaultContentMargin = defaultMargin;
    return this;
  }

  public <T extends Widget> T add(T widget) {
    return this.add(widget, (Consumer<Configurator<T>>) null);
  }

  public <T extends Widget> T add(T widget, LayoutHookWithParent<LinearLayoutWidget, T> layoutHook) {
    return this.add(widget, (configurator) -> configurator.layoutHook(layoutHook));
  }

  public <T extends Widget> T add(T widget, Consumer<Configurator<T>> configure) {
    CellWidget<T> cell = new CellWidget<>(widget);
    this.cells.add(cell);

    if (widget instanceof LinearLayoutAdapter linearLayoutAdapter) {
      cell.margin(linearLayoutAdapter.getDefaultLinearLayoutMargin());
    }

    if (configure != null) {
      configure.accept(cell);
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

  private void calculateContentDimensions() {
    this.contentWidth = switch (this.flowAxis) {
      case HORIZONTAL -> this.getMainContentDimension(CellWidget::getWidth);
      case VERTICAL -> this.getOffContentDimension(CellWidget::getWidth);
    };
    this.contentHeight = switch (this.flowAxis) {
      case HORIZONTAL -> this.getOffContentDimension(CellWidget::getHeight);
      case VERTICAL -> this.getMainContentDimension(CellWidget::getHeight);
    };
  }

  private int getMainContentDimension(ToIntFunction<CellWidget<?>> mapper) {
    int totalSpacing = Math.max(0, this.cells.size() - 1) * this.spacing;
    return this.cells.stream().mapToInt(mapper).sum() + totalSpacing;
  }

  private int getOffContentDimension(ToIntFunction<CellWidget<?>> mapper) {
    return this.cells.stream().mapToInt(mapper).max().orElse(0);
  }

  @Override
  public void refreshPositions() {
    this.cells.forEach((cell) -> cell.onLayout(this));
    this.calculateContentDimensions();

    int posMain = switch (this.flowAxis) {
      case HORIZONTAL ->
          this.getX() + (int) ((this.getWidth() - this.getContentWidth()) * this.contentAlignMain.floatValue());
      case VERTICAL ->
          this.getY() + (int) ((this.getHeight() - this.getContentHeight()) * this.contentAlignMain.floatValue());
    };

    for (CellWidget<?> cell : this.cells) {
      int posOff = switch (this.flowAxis) {
        case HORIZONTAL ->
            this.getY() + (int) ((this.getHeight() - cell.getHeight()) * this.getCellAlign(cell).floatValue());
        case VERTICAL ->
            this.getX() + (int) ((this.getWidth() - cell.getWidth()) * this.getCellAlign(cell).floatValue());
      };

      int main = posMain + this.getMainLeadingCellMargin(cell);
      int off = posOff + this.getOffLeadingCellMargin(cell);

      switch (this.flowAxis) {
        case HORIZONTAL -> cell.setPosition(main, off);
        case VERTICAL -> cell.setPosition(off, main);
      }

      posMain += this.getMainCellDimension(cell) + this.spacing;
    }

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

  @Override
  public int getWidth() {
    return this.width != 0 ? this.width : this.contentWidth;
  }

  @Override
  public int getHeight() {
    return this.height != 0 ? this.height : this.contentHeight;
  }

  public int getContentWidth() {
    return this.contentWidth;
  }

  public int getContentHeight() {
    return this.contentHeight;
  }

  public int getSpacing() {
    return this.spacing;
  }

  private Spacing getCellMargin(CellWidget<?> cell) {
    return cell.margin != null ? cell.margin : this.defaultContentMargin;
  }

  private Alignment getCellAlign(CellWidget<?> cell) {
    return cell.align != null ? cell.align : this.defaultContentAlignOff;
  }

  private int getCellWidth(CellWidget<?> cell) {
    return cell.getWidth() + this.getCellMargin(cell).getHorizontal();
  }

  private int getCellHeight(CellWidget<?> cell) {
    return cell.getHeight() + this.getCellMargin(cell).getVertical();
  }

  private int getMainLeadingCellMargin(CellWidget<?> cell) {
    Spacing margin = this.getCellMargin(cell);
    return switch (this.flowAxis) {
      case HORIZONTAL -> margin.left();
      case VERTICAL -> margin.top();
    };
  }

  private int getOffLeadingCellMargin(CellWidget<?> cell) {
    Spacing margin = this.getCellMargin(cell);
    return switch (this.flowAxis) {
      case HORIZONTAL -> margin.top();
      case VERTICAL -> margin.left();
    };
  }

  private int getMainCellDimension(CellWidget<?> cell) {
    return switch (this.flowAxis) {
      case HORIZONTAL -> this.getCellWidth(cell);
      case VERTICAL -> this.getCellHeight(cell);
    };
  }

  @Environment(EnvType.CLIENT)
  public interface Configurator<T extends Widget> {
    T getWidget();

    Configurator<T> layoutHook(LayoutHookWithParent<LinearLayoutWidget, T> layoutHook);

    Configurator<T> margin(Spacing margin);

    Configurator<T> align(Alignment align);

    default Configurator<T> alignStart() {
      return this.align(Alignment.START);
    }

    default Configurator<T> alignCenter() {
      return this.align(Alignment.CENTER);
    }

    default Configurator<T> alignEnd() {
      return this.align(Alignment.END);
    }
  }

  @Environment(EnvType.CLIENT)
  private static class CellWidget<T extends Widget> implements Configurator<T>, Widget {
    private final T widget;

    private LayoutHookWithParent<LinearLayoutWidget, T> layoutHook = null;
    private Spacing margin = null;
    private Alignment align = null;

    public CellWidget(T widget) {
      this.widget = widget;
    }

    @Override
    public T getWidget() {
      return this.widget;
    }

    @Override
    public CellWidget<T> layoutHook(LayoutHookWithParent<LinearLayoutWidget, T> layoutHook) {
      this.layoutHook = layoutHook;
      return this;
    }

    @Override
    public CellWidget<T> margin(Spacing margin) {
      this.margin = margin;
      return this;
    }

    @Override
    public CellWidget<T> align(Alignment align) {
      this.align = align;
      return this;
    }

    public void onLayout(LinearLayoutWidget parent) {
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
