package me.roundaround.roundalib.client.gui.widget.layout;

import me.roundaround.roundalib.client.gui.layout.Spacing;
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

  private FlowAxis flowAxis;
  private float alignSelfX;
  private float alignSelfY;
  private int spacing;
  private float contentAlignMain;
  private float defaultContentAlignOff = 0.5f;
  private Spacing defaultContentMargin = Spacing.zero();

  private int contentWidth;
  private int contentHeight;

  public LinearLayoutWidget(FlowAxis flowAxis) {
    this(flowAxis, 0, 0, 0, 0);
  }

  public LinearLayoutWidget(FlowAxis flowAxis, int width, int height) {
    this(flowAxis, 0, 0, width, height);
  }

  public LinearLayoutWidget(FlowAxis flowAxis, int x, int y, int width, int height) {
    super(x, y, width, height);

    this.flowAxis = flowAxis;
  }

  public static LinearLayoutWidget horizontal() {
    return new LinearLayoutWidget(FlowAxis.HORIZONTAL);
  }

  public static LinearLayoutWidget horizontal(int width, int height) {
    return new LinearLayoutWidget(FlowAxis.HORIZONTAL, width, height);
  }

  public static LinearLayoutWidget horizontal(int x, int y, int width, int height) {
    return new LinearLayoutWidget(FlowAxis.HORIZONTAL, x, y, width, height);
  }

  public static LinearLayoutWidget vertical() {
    return new LinearLayoutWidget(FlowAxis.VERTICAL);
  }

  public static LinearLayoutWidget vertical(int width, int height) {
    return new LinearLayoutWidget(FlowAxis.VERTICAL, width, height);
  }

  public static LinearLayoutWidget vertical(int x, int y, int width, int height) {
    return new LinearLayoutWidget(FlowAxis.VERTICAL, x, y, width, height);
  }

  public LinearLayoutWidget flowAxis(FlowAxis flowAxis) {
    this.flowAxis = flowAxis;
    return this;
  }

  public LinearLayoutWidget alignSelfX(float alignX) {
    this.alignSelfX = alignX;
    return this;
  }

  public LinearLayoutWidget alignSelfLeft() {
    return this.alignSelfX(0f);
  }

  public LinearLayoutWidget alignSelfCenterX() {
    return this.alignSelfX(0.5f);
  }

  public LinearLayoutWidget alignSelfRight() {
    return this.alignSelfX(1f);
  }

  public LinearLayoutWidget alignSelfY(float alignY) {
    this.alignSelfY = alignY;
    return this;
  }

  public LinearLayoutWidget alignSelfTop() {
    return this.alignSelfY(0f);
  }

  public LinearLayoutWidget alignSelfCenterY() {
    return this.alignSelfY(0.5f);
  }

  public LinearLayoutWidget alignSelfBottom() {
    return this.alignSelfY(1f);
  }

  public LinearLayoutWidget alignSelf(float alignX, float alignY) {
    this.alignSelfX = alignX;
    this.alignSelfY = alignY;
    return this;
  }

  public LinearLayoutWidget spacing(int spacing) {
    this.spacing = spacing;
    return this;
  }

  public LinearLayoutWidget mainAxisContentAlign(float align) {
    this.contentAlignMain = align;
    return this;
  }

  public LinearLayoutWidget mainAxisContentAlignStart() {
    this.mainAxisContentAlign(0f);
    return this;
  }

  public LinearLayoutWidget mainAxisContentAlignCenter() {
    this.mainAxisContentAlign(0.5f);
    return this;
  }

  public LinearLayoutWidget mainAxisContentAlignEnd() {
    this.mainAxisContentAlign(1f);
    return this;
  }

  public LinearLayoutWidget defaultOffAxisContentAlign(float align) {
    this.defaultContentAlignOff = align;
    return this;
  }

  public LinearLayoutWidget defaultOffAxisContentAlignStart() {
    return this.defaultOffAxisContentAlign(0f);
  }

  public LinearLayoutWidget defaultOffAxisContentAlignCenter() {
    return this.defaultOffAxisContentAlign(0.5f);
  }

  public LinearLayoutWidget defaultOffAxisContentAlignEnd() {
    return this.defaultOffAxisContentAlign(1f);
  }

  public LinearLayoutWidget defaultContentMargin(Spacing defaultMargin) {
    this.defaultContentMargin = defaultMargin;
    return this;
  }

  public <T extends Widget> T add(T widget) {
    return this.add(widget, (Consumer<Adder<T>>) null);
  }

  public <T extends Widget> T add(T widget, LayoutHookWithParent<LinearLayoutWidget, T> layoutHook) {
    return this.add(widget, (adder) -> adder.layoutHook(layoutHook));
  }

  public <T extends Widget> T add(T widget, Consumer<Adder<T>> configure) {
    CellWidget<T> cell = new CellWidget<>(widget);
    this.cells.add(cell);
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
      case HORIZONTAL -> this.getX() + (int) ((this.getWidth() - this.getContentWidth()) * this.contentAlignMain);
      case VERTICAL -> this.getY() + (int) ((this.getHeight() - this.getContentHeight()) * this.contentAlignMain);
    };

    for (CellWidget<?> cell : this.cells) {
      int posOff = switch (this.flowAxis) {
        case HORIZONTAL -> this.getY() + (int) ((this.getHeight() - cell.getHeight()) * this.getCellAlign(cell));
        case VERTICAL -> this.getX() + (int) ((this.getWidth() - cell.getWidth()) * this.getCellAlign(cell));
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
    return super.getX() - (int) (this.getWidth() * this.alignSelfX);
  }

  @Override
  public int getY() {
    return super.getY() - (int) (this.getHeight() * this.alignSelfY);
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

  private float getCellAlign(CellWidget<?> cell) {
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
  public enum FlowAxis {
    HORIZONTAL, VERTICAL;
  }

  @Environment(EnvType.CLIENT)
  public interface Adder<T extends Widget> {
    Adder<T> layoutHook(LayoutHookWithParent<LinearLayoutWidget, T> layoutHook);

    Adder<T> margin(Spacing margin);

    Adder<T> align(Float align);

    default Adder<T> alignStart() {
      return this.align(0f);
    }

    default Adder<T> alignCenter() {
      return this.align(0.5f);
    }

    default Adder<T> alignEnd() {
      return this.align(1f);
    }
  }

  @Environment(EnvType.CLIENT)
  private static class CellWidget<T extends Widget> implements Adder<T>, Widget {
    private final T widget;

    private LayoutHookWithParent<LinearLayoutWidget, T> layoutHook = null;
    private Spacing margin = null;
    private Float align = null;

    public CellWidget(T widget) {
      this.widget = widget;
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
    public CellWidget<T> align(Float align) {
      this.align = align;
      return this;
    }

    public T getWidget() {
      return this.widget;
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
