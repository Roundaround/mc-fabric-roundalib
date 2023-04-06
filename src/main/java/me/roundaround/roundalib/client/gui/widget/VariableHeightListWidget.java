package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.client.gui.GuiUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public abstract class VariableHeightListWidget<E extends VariableHeightListWidget.Entry<E>>
    extends AbstractParentElement implements Drawable, Selectable {
  protected static final int SCROLLBAR_WIDTH = 6;

  protected final MinecraftClient client;
  protected final CachingPositionalLinkedList<E> entries = new CachingPositionalLinkedList<>();

  protected int left;
  protected int top;
  protected int right;
  protected int bottom;
  protected int width;
  protected int height;
  protected E hoveredEntry;
  protected int contentPadding = GuiUtil.PADDING;
  protected double scrollUnit;
  protected boolean autoCalculateScrollUnit = true;

  private double scrollAmount;
  private boolean scrolling;

  public VariableHeightListWidget(
      MinecraftClient client, int left, int top, int width, int height) {
    this.client = client;
    this.left = left;
    this.right = left + width;
    this.top = top;
    this.bottom = top + height;
    this.width = width;
    this.height = height;
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    this.hoveredEntry =
        this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;

    enableScissor(this.left, this.top, this.right, this.bottom);
    this.renderList(matrixStack, mouseX, mouseY, delta);
    this.renderScrollBar(matrixStack, mouseX, mouseY, delta);
    disableScissor();
  }

  protected void renderList(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
  }

  protected void renderScrollBar(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    int i = this.getMaxScroll();
    if (i > 0) {
      int j = (this.height) * (this.height) / this.entries.totalHeight;
      j = MathHelper.clamp(j, 32, this.bottom - this.top - 8);
      int k = (int) this.getScrollAmount() * (this.bottom - this.top - j) / i + this.top;
      if (k < this.top) {
        k = this.top;
      }

      fill(matrixStack, i, this.top, j, this.bottom, -16777216);
      fill(matrixStack, i, k, j, k + j, -8355712);
      fill(matrixStack, i, k, j - 1, k + j - 1, -4144960);
    }
  }

  @Override
  public void appendNarrations(NarrationMessageBuilder builder) {

  }

  @Override
  public E getFocused() {
    return (E) super.getFocused();
  }

  @Override
  public List<? extends Element> children() {
    return this.entries.copy();
  }

  @Override
  public SelectionType getType() {
    if (this.isFocused()) {
      return SelectionType.FOCUSED;
    } else {
      return this.hoveredEntry != null ? SelectionType.HOVERED : SelectionType.NONE;
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    this.updateScrollingState(mouseX, mouseY, button);
    if (!this.isMouseOver(mouseX, mouseY)) {
      return false;
    }

    E entry = this.getEntryAtPosition(mouseX, mouseY);
    if (entry != null) {
      if (entry.mouseClicked(mouseX, mouseY, button)) {
        E focused = this.getFocused();
        if (focused != entry && focused != null) {
          focused.setFocused(null);
        }

        this.setFocused(entry);
        this.setDragging(true);
        return true;
      }
    }

    return this.scrolling;
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    if (this.getFocused() != null) {
      this.getFocused().mouseReleased(mouseX, mouseY, button);
    }

    return false;
  }

  @Override
  public boolean mouseDragged(
      double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
      return true;
    } else if (button == 0 && this.scrolling) {
      if (mouseY < this.top) {
        this.setScrollAmount(0);
      } else if (mouseY > this.bottom) {
        this.setScrollAmount(this.getMaxScroll());
      } else {
        double d = Math.max(1, this.getMaxScroll());
        int i = this.height;
        int j = MathHelper.clamp((int) ((float) (i * i) / this.entries.totalHeight), 32, i - 8);
        double e = Math.max(1.0, d / (double) (i - j));
        this.setScrollAmount(this.getScrollAmount() + deltaY * e);
      }

      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    this.setScrollAmount(this.getScrollAmount() - amount * this.getScrollUnit());
    return true;
  }

  public void updateSize(int left, int top, int width, int height) {
    this.left = left;
    this.top = top;
    this.width = width;
    this.height = height;
  }

  protected E getEntryAtPosition(double x, double y) {
    if (x < this.getContentLeft() || x > this.getContentRight()) {
      return null;
    }

    if (y < this.top || y > this.bottom) {
      return null;
    }

    double adjustedY = y - this.top - this.getScrollAmount();
    return this.entries.getEntryAtPosition(adjustedY);
  }

  protected boolean shouldShowScrollbar() {
    return this.getContentHeight() > this.height;
  }

  protected int getContentHeight() {
    return this.entries.totalHeight + this.contentPadding * 2;
  }

  protected int getContentWidth() {
    return this.width - (this.shouldShowScrollbar() ? SCROLLBAR_WIDTH : 0) -
        this.contentPadding * 2;
  }

  protected int getContentLeft() {
    return this.left + this.contentPadding;
  }

  protected int getContentRight() {
    return this.right - this.contentPadding - (this.shouldShowScrollbar() ? SCROLLBAR_WIDTH : 0);
  }

  protected int getScrollbarPositionX() {
    return this.right - SCROLLBAR_WIDTH;
  }

  private void scroll(int amount) {
    this.setScrollAmount(this.getScrollAmount() + (double) amount);
  }

  public double getScrollAmount() {
    return this.scrollAmount;
  }

  public void setScrollAmount(double amount) {
    this.scrollAmount = MathHelper.clamp(amount, 0.0, this.getMaxScroll());
  }

  public int getMaxScroll() {
    return Math.max(0, this.entries.totalHeight - (this.height - 4));
  }

  protected void updateScrollingState(double mouseX, double mouseY, int button) {
    this.scrolling = button == 0 && mouseX >= (double) this.getScrollbarPositionX() &&
        mouseX < (this.getScrollbarPositionX() + SCROLLBAR_WIDTH);
  }

  protected double getScrollUnit() {
    return this.autoCalculateScrollUnit ? this.entries.averageItemHeight / 2f : this.scrollUnit;
  }

  public abstract static class Entry<E extends Entry<E>> extends AbstractParentElement {
    protected final MinecraftClient client;
    protected final VariableHeightListWidget<E> parent;
    protected final int height;

    protected int top;

    public Entry(MinecraftClient client, VariableHeightListWidget<E> parent, int top, int height) {
      this.client = client;
      this.parent = parent;
      this.top = top;
      this.height = height;
    }

    public void render(
        MatrixStack matrixStack, int left, int width, int mouseX, int mouseY, float delta) {

    }
  }

  protected static class CachingPositionalLinkedList<E extends VariableHeightListWidget.Entry<E>> {
    private final LinkedList<E> entries = new LinkedList<>();
    private int totalHeight;
    private double averageItemHeight;
    private E cachedAtY;
    private int cachedAtYIndex;

    public List<E> copy() {
      return List.copyOf(this.entries);
    }

    public void add(E entry) {
      E last = this.entries.peekLast();
      entry.top = last != null ? last.top + last.height : 0;

      this.entries.add(entry);
      this.totalHeight += entry.height;
      this.averageItemHeight = (double) this.totalHeight / (double) this.entries.size();
    }

    public void prepend(E entry) {
      entry.top = 0;

      this.entries.addFirst(entry);
      this.reflow();
    }

    public void remove(E entry) {
      this.entries.remove(entry);
      this.reflow();
    }

    public E get(int index) {
      return this.entries.get(index);
    }

    public int size() {
      return this.entries.size();
    }

    public void clear() {
      this.entries.clear();
      this.reflow();
    }

    public void reflow() {
      this.cachedAtY = null;
      this.cachedAtYIndex = 0;

      int top = 0;

      for (E entry : this.entries) {
        entry.top = top;
        top += entry.height;
      }

      this.totalHeight = top;
      this.averageItemHeight = (double) this.totalHeight / (double) this.entries.size();
    }

    public E getEntryAtPosition(double y) {
      if (this.cachedAtY != null) {
        if (y >= this.cachedAtY.top && y <= this.cachedAtY.top + this.cachedAtY.height) {
          return this.cachedAtY;
        }

        if (y < this.cachedAtY.top) {
          return this.getEntryAtPositionLookBackward(y, this.cachedAtYIndex);
        } else {
          return this.getEntryAtPositionLookForward(y, this.cachedAtYIndex);
        }
      }

      return this.getEntryAtPositionLookForward(y, 0);
    }

    private E getEntryAtPositionLookBackward(double y, int startingIndex) {
      ListIterator<E> iterator = this.entries.listIterator(startingIndex);

      while (iterator.hasPrevious()) {
        E previous = iterator.previous();

        if (y >= previous.top && y <= previous.top + previous.height) {
          this.cachedAtY = previous;
          this.cachedAtYIndex = iterator.nextIndex();

          return previous;
        }
      }

      return null;
    }

    private E getEntryAtPositionLookForward(double y, int startingIndex) {
      ListIterator<E> iterator = this.entries.listIterator(startingIndex);

      while (iterator.hasNext()) {
        E next = iterator.next();

        if (y >= next.top && y <= next.top + next.height) {
          this.cachedAtY = next;
          this.cachedAtYIndex = iterator.nextIndex();

          return next;
        }
      }

      return null;
    }
  }
}
