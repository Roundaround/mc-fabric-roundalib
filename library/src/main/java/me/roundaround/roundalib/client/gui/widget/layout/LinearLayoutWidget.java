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
  private Spacing padding = Spacing.zero();
  private float alignX;
  private float alignY;
  private int spacing;
  private Spacing defaultMargin = Spacing.zero();

  private int contentWidth;
  private int contentHeight;

  public LinearLayoutWidget(FlowAxis flowAxis) {
    this(flowAxis, 0, 0, 0, 0);
  }

  public LinearLayoutWidget(FlowAxis flowAxis, int width, int height) {
    this(flowAxis, 0, 0, width, height);
  }

  private LinearLayoutWidget(FlowAxis flowAxis, int x, int y, int width, int height) {
    super(x, y, width, height);

    this.flowAxis = flowAxis;
  }

  public static LinearLayoutWidget horizontal() {
    return new LinearLayoutWidget(FlowAxis.HORIZONTAL);
  }

  public static LinearLayoutWidget vertical() {
    return new LinearLayoutWidget(FlowAxis.VERTICAL);
  }

  public LinearLayoutWidget flowAxis(FlowAxis flowAxis) {
    this.flowAxis = flowAxis;
    return this;
  }

  public LinearLayoutWidget padding(Spacing padding) {
    this.padding = padding;
    return this;
  }

  public LinearLayoutWidget alignX(float alignX) {
    this.alignX = alignX;
    return this;
  }

  public LinearLayoutWidget alignLeft() {
    return this.alignX(0f);
  }

  public LinearLayoutWidget alignCenterX() {
    return this.alignX(0.5f);
  }

  public LinearLayoutWidget alignRight() {
    return this.alignX(1f);
  }

  public LinearLayoutWidget alignY(float alignY) {
    this.alignY = alignY;
    return this;
  }

  public LinearLayoutWidget alignTop() {
    return this.alignY(0f);
  }

  public LinearLayoutWidget alignCenterY() {
    return this.alignY(0.5f);
  }

  public LinearLayoutWidget alignBottom() {
    return this.alignY(1f);
  }

  public LinearLayoutWidget align(float alignX, float alignY) {
    this.alignX = alignX;
    this.alignY = alignY;
    return this;
  }

  public LinearLayoutWidget spacing(int spacing) {
    this.spacing = spacing;
    return this;
  }

  public LinearLayoutWidget defaultMargin(Spacing defaultMargin) {
    this.defaultMargin = defaultMargin;
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
      case VERTICAL -> this.getOffContentDimension(CellWidget::getHeight);
    };
    this.contentHeight = switch (this.flowAxis) {
      case HORIZONTAL -> this.getOffContentDimension(CellWidget::getWidth);
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
      int main = mainPos + this.spacing + this.getMainLeadingCellMargin(cell);

      float offAlign = switch (this.flowAxis) {
        case HORIZONTAL -> this.alignY;
        case VERTICAL -> this.alignX;
      };
      int offPos = offStart - (int) (this.getOffCellDimension(cell) * offAlign * this.getCellAlign(cell));
      int off = offPos + this.getOffLeadingCellMargin(cell);

      switch (this.flowAxis) {
        case HORIZONTAL -> cell.setPosition(main, off);
        case VERTICAL -> cell.setPosition(off, main);
      }

      mainPos += this.getMainCellDimension(cell) + this.spacing;
    }

    super.refreshPositions();
  }

  private int getStartX() {
    return this.getX() - (int) (this.getWidth() * this.alignX);
  }

  private int getStartY() {
    return this.getY() - (int) (this.getHeight() * this.alignY);
  }

  @Override
  public int getWidth() {
    int baseWidth = this.width != 0 ? this.width : this.contentWidth;
    return baseWidth + this.padding.getHorizontal();
  }

  @Override
  public int getHeight() {
    int baseHeight = this.height != 0 ? this.height : this.contentHeight;
    return baseHeight + this.padding.getVertical();
  }

  public int getSpacing() {
    return this.spacing;
  }

  private Spacing getCellMargin(CellWidget<?> cell) {
    return cell.margin != null ? cell.margin : this.defaultMargin;
  }

  private float getCellAlign(CellWidget<?> cell) {
    if (cell.align != null) {
      return cell.align;
    }
    return switch (this.flowAxis) {
      case HORIZONTAL -> this.alignY;
      case VERTICAL -> this.alignX;
    };
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

  private int getOffCellDimension(CellWidget<?> cell) {
    return switch (this.flowAxis) {
      case HORIZONTAL -> this.getCellHeight(cell);
      case VERTICAL -> this.getCellWidth(cell);
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
