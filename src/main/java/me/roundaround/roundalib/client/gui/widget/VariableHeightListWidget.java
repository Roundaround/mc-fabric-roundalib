package me.roundaround.roundalib.client.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class VariableHeightListWidget<E extends VariableHeightListWidget.Entry<E>>
    extends AbstractParentElement implements Drawable, Selectable {
  protected static final int SCROLLBAR_WIDTH = 6;

  protected final MinecraftClient client;
  protected final int left;
  protected final int top;
  protected final int width;
  protected final int height;
  protected final CachingPositionalLinkedList<E> entries = new CachingPositionalLinkedList<>();

  protected E selectedEntry;
  protected E hoveredEntry;
  protected int contentPadding = 4;
  protected int contentHeight;

  private E cachedElementAtY;

  public VariableHeightListWidget(
      MinecraftClient client, int left, int top, int width, int height) {
    this.client = client;
    this.left = left;
    this.top = top;
    this.width = width;
    this.height = height;
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    enableScissor(this.left, this.top, this.left + this.width, this.top + this.height);

    this.renderList(matrixStack, mouseX, mouseY, delta);

    disableScissor();
  }

  protected void renderList(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
  }

  @Override
  public void appendNarrations(NarrationMessageBuilder builder) {

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

  public double getScrollAmount() {
    return 0;
  }

  protected E getEntryAtPosition(double x, double y) {
    if (x < this.getContentLeft() || x > this.getContentRight()) {
      return null;
    }

    if (x < this.top || x > this.top + this.height) {
      return null;
    }

    double adjustedY = y - this.top - this.getScrollAmount();
    return this.entries.getEntryAtPosition(adjustedY);
  }

  protected boolean shouldShowScrollbar() {
    return this.getContentHeight() > this.height;
  }

  protected int getContentHeight() {
    return this.contentHeight + this.contentPadding * 2;
  }

  protected int getContentWidth() {
    return this.width - (this.shouldShowScrollbar() ? SCROLLBAR_WIDTH : 0) -
        this.contentPadding * 2;
  }

  protected int getContentLeft() {
    return this.left + this.contentPadding;
  }

  protected int getContentRight() {
    return this.left + this.width - this.contentPadding -
        (this.shouldShowScrollbar() ? SCROLLBAR_WIDTH : 0);
  }

  public abstract static class Entry<E extends Entry<E>> extends AbstractParentElement
      implements Element {
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
        MatrixStack matrixStack,
        int top,
        int left,
        int width,
        int mouseX,
        int mouseY,
        float delta) {

    }
  }

  protected static class CachingPositionalLinkedList<E extends VariableHeightListWidget.Entry<E>> {
    private final LinkedList<E> entries = new LinkedList<>();
    private E cachedAtY;
    private int cachedAtYIndex;

    public List<E> copy() {
      return List.copyOf(this.entries);
    }

    public void add(E entry) {
      E last = this.entries.peekLast();
      entry.top = last != null ? last.top + last.height : 0;

      this.entries.add(entry);
    }

    public void remove(E entry) {
      this.entries.remove(entry);
    }

    public E get(int index) {
      return this.entries.get(index);
    }

    public int size() {
      return this.entries.size();
    }

    public void clear() {
      this.entries.clear();
    }

    public void reflow(int startingIndex) {
      ListIterator<E> iterator = this.entries.listIterator(startingIndex);

      if (iterator.hasPrevious()) {
        E previous = iterator.previous();
        iterator.next();

        iterator.next().top = previous.top + previous.height;
      } else {
        iterator.next().top = 0;
      }

      while (iterator.hasNext()) {
        E previous = iterator.previous();
        iterator.next();

        iterator.next().top = previous.top + previous.height;
      }
    }

    public E getEntryAtPosition(double y) {
      if (this.cachedAtY != null) {
        // Check if we're within the cached element first
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
