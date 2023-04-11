package me.roundaround.roundalib.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.config.ConfigListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public abstract class VariableHeightListWidget<E extends VariableHeightListWidget.Entry<E>>
    extends AbstractParentElement implements Drawable, Selectable {
  protected final MinecraftClient client;
  protected final CachingPositionalLinkedList<E> entries =
      new CachingPositionalLinkedList<>(this.rowPadding);

  protected int left;
  protected int top;
  protected int right;
  protected int bottom;
  protected int width;
  protected int height;
  protected E hoveredEntry;
  protected int contentPadding = GuiUtil.PADDING;
  protected int rowPadding = GuiUtil.PADDING; // TODO: make changeable
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

  public <T extends E> T addEntry(T entry) {
    this.entries.add(entry);
    return entry;
  }

  public <T extends E> T prependEntry(T entry) {
    this.entries.prepend(entry);
    return entry;
  }

  public <T extends E> T insertEntry(int index, T entry) {
    this.entries.insert(index, entry);
    return entry;
  }

  public void removeEntry(E entry) {
    this.entries.remove(entry);
  }

  public void clearEntries() {
    this.entries.clear();
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    this.hoveredEntry =
        this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;

    this.renderBackground(matrixStack, delta);

    enableScissor(this.left, this.top, this.right, this.bottom);
    this.renderList(matrixStack, mouseX, mouseY, delta);
    this.renderScrollBar(matrixStack, mouseX, mouseY, delta);
    disableScissor();
  }

  protected void renderList(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    for (int i = 0; i < this.entries.size(); i++) {
      this.renderEntry(matrixStack, i, mouseX, mouseY, delta);
    }
  }

  protected void renderEntry(
      MatrixStack matrixStack, int index, int mouseX, int mouseY, float delta) {
    E entry = this.entries.get(index);
    double scrolledTop = this.top + entry.getTop() - this.getScrollAmount();
    double scrolledBottom = this.top + entry.getTop() + entry.getHeight() - this.getScrollAmount();
    if (scrolledBottom < this.top || scrolledTop > this.bottom) {
      return;
    }

    matrixStack.push();
    matrixStack.translate(0, this.top - getScrollAmount(), 0);
    entry.render(matrixStack, index, mouseX, mouseY, delta);
    matrixStack.pop();
  }

  protected void renderScrollBar(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    if (!this.shouldShowScrollbar()) {
      return;
    }

    int scrollbarLeft = this.getScrollbarPositionX();
    int scrollbarRight = scrollbarLeft + GuiUtil.SCROLLBAR_WIDTH;

    RenderSystem.setShader(GameRenderer::getPositionColorProgram);

    int handleHeight = (int) ((float) this.height * this.height / this.getMaxScroll());
    handleHeight = MathHelper.clamp(handleHeight, 32, this.height - 8);

    int handleTop =
        (int) Math.round(this.scrollAmount) * (this.height - handleHeight) / this.getMaxScroll() +
            this.top;
    if (handleTop < this.top) {
      handleTop = this.top;
    }

    fill(matrixStack,
        scrollbarLeft,
        this.top,
        scrollbarRight,
        this.bottom,
        GuiUtil.genColorInt(0, 0, 0));
    fill(matrixStack,
        scrollbarLeft,
        handleTop,
        scrollbarRight,
        handleTop + handleHeight,
        GuiUtil.genColorInt(0.5f, 0.5f, 0.5f));
    fill(matrixStack,
        scrollbarLeft,
        handleTop,
        scrollbarRight - 1,
        handleTop + handleHeight - 2,
        GuiUtil.genColorInt(0.75f, 0.75f, 0.75f));
  }

  protected void renderBackground(MatrixStack matrixStack, float delta) {
    Screen parent = this.client.currentScreen;
    int screenWidth = parent != null ? parent.width : this.width;

    GuiUtil.renderBackgroundInRegion(32,
        this.top,
        this.bottom,
        0,
        screenWidth,
        0,
        this.scrollAmount);

    this.renderHorizontalShadows(matrixStack, delta);
  }

  protected void renderHorizontalShadows(MatrixStack matrixStack, float delta) {
    Screen parent = this.client.currentScreen;
    int screenWidth = parent != null ? parent.width : this.width;

    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();

    RenderSystem.depthFunc(515);
    RenderSystem.disableDepthTest();
    RenderSystem.enableBlend();
    RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA,
        GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
        GlStateManager.SrcFactor.ZERO,
        GlStateManager.DstFactor.ONE);
    RenderSystem.setShader(GameRenderer::getPositionColorProgram);

    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
    bufferBuilder.vertex(0, this.top + this.contentPadding, 0).color(0, 0, 0, 0).next();
    bufferBuilder.vertex(screenWidth, this.top + this.contentPadding, 0).color(0, 0, 0, 0).next();
    bufferBuilder.vertex(screenWidth, this.top, 0).color(0, 0, 0, 255).next();
    bufferBuilder.vertex(0, this.top, 0).color(0, 0, 0, 255).next();
    bufferBuilder.vertex(0, this.bottom, 0).color(0, 0, 0, 255).next();
    bufferBuilder.vertex(screenWidth, this.bottom, 0).color(0, 0, 0, 255).next();
    bufferBuilder.vertex(screenWidth, this.bottom - this.contentPadding, 0)
        .color(0, 0, 0, 0)
        .next();
    bufferBuilder.vertex(0, this.bottom - this.contentPadding, 0).color(0, 0, 0, 0).next();
    tessellator.draw();

    RenderSystem.disableBlend();
  }

  @Override
  public void appendNarrations(NarrationMessageBuilder builder) {

  }

  @Override
  @SuppressWarnings("unchecked")
  public E getFocused() {
    // Suppress warning because we know that the focused element will only ever be an entry
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

  protected void ensureVisible(E entry) {
    int scrolledTop = entry.getTop() - (int) this.scrollAmount;
    if (scrolledTop < this.contentPadding) {
      this.scroll(scrolledTop - this.contentPadding);
      return;
    }

    int scrolledBottom = entry.getTop() + entry.getHeight() - (int) this.scrollAmount;
    if (scrolledBottom > this.height - this.contentPadding) {
      this.scroll(scrolledBottom - this.height + this.contentPadding);
    }
  }

  protected int getContentHeight() {
    return this.entries.totalHeight + this.contentPadding * 2;
  }

  protected int getContentWidth() {
    return this.width - (this.shouldShowScrollbar() ? GuiUtil.SCROLLBAR_WIDTH : 0) -
        this.contentPadding * 2;
  }

  protected int getContentLeft() {
    return this.left + this.contentPadding;
  }

  protected int getContentRight() {
    return this.right - this.contentPadding -
        (this.shouldShowScrollbar() ? GuiUtil.SCROLLBAR_WIDTH : 0);
  }

  protected int getScrollbarPositionX() {
    return this.right - GuiUtil.SCROLLBAR_WIDTH;
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
    return Math.max(0, this.getContentHeight() - this.height);
  }

  protected void updateScrollingState(double mouseX, double mouseY, int button) {
    this.scrolling = button == 0 && mouseX >= (double) this.getScrollbarPositionX() &&
        mouseX < (this.getScrollbarPositionX() + GuiUtil.SCROLLBAR_WIDTH);
  }

  protected double getScrollUnit() {
    return this.autoCalculateScrollUnit ? this.entries.averageItemHeight / 2f : this.scrollUnit;
  }

  protected boolean shouldShowScrollbar() {
    return this.getMaxScroll() > 0;
  }

  public abstract static class Entry<E extends Entry<E>> extends AbstractParentElement {
    protected static final int ROW_SHADE_STRENGTH = 85;
    protected static final int ROW_SHADE_FADE_WIDTH = 10;
    protected static final int ROW_SHADE_FADE_OVERFLOW = 10;

    protected final MinecraftClient client;
    protected final VariableHeightListWidget<E> parent;

    private final int left;
    private final int width;
    private final int height;

    private int top;

    public Entry(MinecraftClient client, VariableHeightListWidget<E> parent, int height) {
      this.client = client;
      this.parent = parent;
      this.left = parent.getContentLeft();
      this.width = parent.getContentWidth();
      this.top = parent.entries.totalHeight + parent.rowPadding; // TODO: Make a getter
      this.height = height;
    }

    public ConfigListWidget getParent() {
      return (ConfigListWidget) this.parent;
    }

    public int getTop() {
      return this.top;
    }

    public int getBottom() {
      return this.top + this.height;
    }

    public int getLeft() {
      return this.left;
    }

    public int getRight() {
      return this.left + this.width;
    }

    public int getHeight() {
      return this.height;
    }

    public int getWidth() {
      return this.width;
    }

    public final void setTop(int top) {
      this.top = top;
    }

    protected void onTopChanged() {
    }

    public void render(
        MatrixStack matrixStack, int index, int mouseX, int mouseY, float delta) {
      this.renderBackground(matrixStack, index, mouseX, mouseY, delta);
      this.renderContent(matrixStack, index, mouseX, mouseY, delta);
      this.renderDecorations(matrixStack, index, mouseX, mouseY, delta);
    }

    public void renderBackground(
        MatrixStack matrixStack, int index, int mouseX, int mouseY, float delta) {
      if (index % 2 == 0) {

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

        int bgLeft = this.left - ROW_SHADE_FADE_OVERFLOW;
        int bgRight = this.left + this.width + ROW_SHADE_FADE_OVERFLOW;
        int bgTop = this.top;
        int bgBottom = this.top + this.height;

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix4f, bgLeft - 1 + ROW_SHADE_FADE_WIDTH, bgTop - 1, 0)
            .color(0, 0, 0, ROW_SHADE_STRENGTH)
            .next();
        bufferBuilder.vertex(matrix4f, bgLeft - 1, bgTop - 1, 0).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(matrix4f, bgLeft - 1, bgBottom + 2, 0).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(matrix4f, bgLeft - 1 + ROW_SHADE_FADE_WIDTH, bgBottom + 2, 0)
            .color(0, 0, 0, ROW_SHADE_STRENGTH)
            .next();

        bufferBuilder.vertex(matrix4f, bgRight + 2 - ROW_SHADE_FADE_WIDTH, bgTop - 1, 0)
            .color(0, 0, 0, ROW_SHADE_STRENGTH)
            .next();
        bufferBuilder.vertex(matrix4f, bgLeft - 1 + ROW_SHADE_FADE_WIDTH, bgTop - 1, 0)
            .color(0, 0, 0, ROW_SHADE_STRENGTH)
            .next();
        bufferBuilder.vertex(matrix4f, bgLeft - 1 + ROW_SHADE_FADE_WIDTH, bgBottom + 2, 0)
            .color(0, 0, 0, ROW_SHADE_STRENGTH)
            .next();
        bufferBuilder.vertex(matrix4f, bgRight + 2 - ROW_SHADE_FADE_WIDTH, bgBottom + 2, 0)
            .color(0, 0, 0, ROW_SHADE_STRENGTH)
            .next();

        bufferBuilder.vertex(matrix4f, bgRight + 2, bgTop - 1, 0).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(matrix4f, bgRight + 2 - ROW_SHADE_FADE_WIDTH, bgTop - 1, 0)
            .color(0, 0, 0, ROW_SHADE_STRENGTH)
            .next();
        bufferBuilder.vertex(matrix4f, bgRight + 2 - ROW_SHADE_FADE_WIDTH, bgBottom + 2, 0)
            .color(0, 0, 0, ROW_SHADE_STRENGTH)
            .next();
        bufferBuilder.vertex(matrix4f, bgRight + 2, bgBottom + 2, 0).color(0, 0, 0, 0).next();
        tessellator.draw();

        RenderSystem.disableBlend();
      }
    }

    public void renderContent(
        MatrixStack matrixStack, int index, int mouseX, int mouseY, float delta) {

    }

    public void renderDecorations(
        MatrixStack matrixStack, int index, int mouseX, int mouseY, float delta) {

    }
  }

  protected static class CachingPositionalLinkedList<E extends VariableHeightListWidget.Entry<E>> {
    private final LinkedList<E> entries = new LinkedList<>();
    private final int rowPadding;
    private int totalHeight;
    private double averageItemHeight;
    private E cachedAtY;
    private int cachedAtYIndex;

    protected CachingPositionalLinkedList(int rowPadding) {
      this.rowPadding = rowPadding;
    }

    public List<E> copy() {
      return List.copyOf(this.entries);
    }

    public void add(E entry) {
      E last = this.entries.peekLast();
      entry.setTop(last != null ? last.getTop() + last.getHeight() + rowPadding : 0);

      this.entries.add(entry);
      this.totalHeight += entry.getHeight() + (last != null ? rowPadding : 0);
      this.averageItemHeight = (double) this.totalHeight / (double) this.entries.size();
    }

    public void prepend(E entry) {
      this.entries.addFirst(entry);
      this.reflow();
    }

    public void insert(int index, E entry) {
      if (index == 0) {
        this.prepend(entry);
        return;
      }

      this.entries.add(index, entry);
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
        entry.setTop(top);
        top += entry.getHeight() + rowPadding;
      }

      this.totalHeight = top - (this.entries.size() <= 1 ? 0 : rowPadding);
      this.averageItemHeight = (double) this.totalHeight / (double) this.entries.size();
    }

    public E getEntryAtPosition(double y) {
      if (this.cachedAtY != null) {
        if (y >= this.cachedAtY.getTop() &&
            y <= this.cachedAtY.getTop() + this.cachedAtY.getHeight()) {
          return this.cachedAtY;
        }

        if (y < this.cachedAtY.getTop()) {
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

        if (y >= previous.getTop() && y <= previous.getTop() + previous.getHeight()) {
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

        if (y >= next.getTop() && y <= next.getTop() + next.getHeight()) {
          this.cachedAtY = next;
          this.cachedAtYIndex = iterator.nextIndex();

          return next;
        }
      }

      return null;
    }
  }
}
