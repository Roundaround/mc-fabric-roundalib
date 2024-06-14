package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.client.gui.layout.Spacing;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;

import java.util.function.Consumer;

public class FullBodyWrapperWidget implements LayoutWidget {
  private final Widget widget;
  private final ThreePartsLayoutWidget layout;
  private Spacing margin = Spacing.zero();

  public FullBodyWrapperWidget(Widget widget, ThreePartsLayoutWidget layout) {
    this.widget = widget;
    this.layout = layout;
  }

  public FullBodyWrapperWidget margin(Spacing margin) {
    this.margin = margin;
    return this;
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
    consumer.accept(this.widget);
  }

  @Override
  public void refreshPositions() {
    this.widget.setPosition(
        this.layout.getX() + this.margin.left(),
        this.layout.getY() + this.layout.getHeaderHeight() + this.margin.top()
    );
    if (this.widget instanceof ClickableWidget clickableWidget) {
      clickableWidget.setDimensions(
          this.layout.getWidth() - this.margin.getHorizontal(),
          this.layout.getContentHeight() - this.margin.getVertical()
      );
    }
    LayoutWidget.super.refreshPositions();
  }

  @Override
  public void setX(int x) {
    // Do nothing. Positioning is handled automatically.
  }

  @Override
  public void setY(int y) {
    // Do nothing. Positioning is handled automatically.
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
}
