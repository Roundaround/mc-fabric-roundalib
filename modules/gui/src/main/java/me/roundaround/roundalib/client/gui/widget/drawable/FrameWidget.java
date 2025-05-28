package me.roundaround.roundalib.client.gui.widget.drawable;

import me.roundaround.roundalib.client.gui.util.GuiUtil;
import me.roundaround.roundalib.client.gui.util.IntRect;
import me.roundaround.roundalib.client.gui.util.Spacing;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class FrameWidget extends DrawableWidget implements LayoutWidget {
  public static final int DEFAULT_OVERFLOW = 2;

  private static final Identifier TEXTURE = Identifier.of(Identifier.DEFAULT_NAMESPACE, "hud/hotbar_selection");
  private static final Spacing NINE_SLIDE_BORDER = Spacing.of(4, 4, 3, 4);

  private Slot refSlot = null;
  private Widget refWidget = null;
  private int overflow;

  public FrameWidget() {
    this(0, 0, 0, 0, DEFAULT_OVERFLOW);
  }

  public FrameWidget(int overflow) {
    this(0, 0, 0, 0, overflow);
  }

  public FrameWidget(int width, int height) {
    this(0, 0, width, height, DEFAULT_OVERFLOW);
  }

  public FrameWidget(int width, int height, int overflow) {
    this(0, 0, width, height, overflow);
  }

  public FrameWidget(Slot slot) {
    this(slot, DEFAULT_OVERFLOW);
  }

  public FrameWidget(Slot slot, int overflow) {
    this(IntRect.fromSlot(slot), overflow);
    this.refSlot = slot;
  }

  public FrameWidget(Widget widget) {
    this(widget, DEFAULT_OVERFLOW);
  }

  public FrameWidget(Widget widget, int overflow) {
    this(IntRect.fromWidget(widget), overflow);
    this.refWidget = widget;
  }

  public FrameWidget(IntRect targetBounds) {
    this(targetBounds, DEFAULT_OVERFLOW);
  }

  public FrameWidget(IntRect targetBounds, int overflow) {
    this(targetBounds.left(), targetBounds.top(), targetBounds.getWidth(), targetBounds.getHeight(), overflow);
  }

  public FrameWidget(int x, int y, int width, int height) {
    this(x, y, width, height, DEFAULT_OVERFLOW);
  }

  public FrameWidget(int x, int y, int width, int height, int overflow) {
    super(x, y, width, height);
    this.overflow = overflow;
  }

  @Override
  public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    // Need to get around the fact the vanilla texture is only 23 pixels tall, so actually draw a 9-slice
    // that is height - 1 tall, then draw just the top border of the texture again, but at the bottom.

    int x = this.getX() - this.overflow;
    int y = this.getY() - this.overflow;
    int width = this.getWidth() + 2 * this.overflow;
    int height = this.getHeight() + 2 * this.overflow - 1;

    Sprite sprite = GuiUtil.getSprite(TEXTURE);
    GuiUtil.drawSpriteNineSliced(
        context, sprite, x, y, width, height, 24, 23, Colors.WHITE, NINE_SLIDE_BORDER);
    GuiUtil.drawSpriteRegion(
        context, sprite, 24, 23, 0, 0, x, y + height, width, 1, Colors.WHITE);
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
  }

  @Override
  public void refreshPositions() {
    if (this.refSlot != null) {
      this.setBounds(IntRect.fromSlot(this.refSlot));
    } else if (this.refWidget != null) {
      this.setBounds(IntRect.fromWidget(this.refWidget));
    }
  }

  public void setOverflow(int overflow) {
    this.overflow = overflow;
  }

  public void frame(Slot slot) {
    this.refSlot = slot;
    this.refWidget = null;
    this.setBounds(IntRect.fromSlot(slot));
  }

  public void frame(Widget widget) {
    this.refWidget = widget;
    this.refSlot = null;
    this.setBounds(IntRect.fromWidget(this.refWidget));
  }

  public void frame(IntRect targetBounds) {
    this.refSlot = null;
    this.refWidget = null;
    this.setBounds(targetBounds);
  }

  private void setBounds(IntRect targetBounds) {
    this.setPosition(targetBounds.left(), targetBounds.top());
    this.setDimensions(targetBounds.getWidth(), targetBounds.getHeight());
  }
}
