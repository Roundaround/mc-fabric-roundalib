package me.roundaround.roundalib.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.config.ConfigListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.navigation.NavigationAxis;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.render.*;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public abstract class VariableHeightListWidget<E extends VariableHeightListWidget.Entry<E>>
    extends AbstractParentElement implements Drawable, Selectable {
  protected final MinecraftClient client;
  protected final PositionalLinkedList<E> entries;
  protected final int contentPadding;
  protected final int rowPadding;

  protected int left;
  protected int top;
  protected int right;
  protected int bottom;
  protected int width;
  protected int height;
  protected E hoveredEntry;
  protected double scrollUnit;
  protected boolean autoCalculateScrollUnit = true;

  private double scrollAmount;
  private boolean scrolling;

  public VariableHeightListWidget(
      MinecraftClient client, int left, int top, int width, int height) {
    this(client, left, top, width, height, GuiUtil.PADDING, GuiUtil.PADDING);
  }

  public VariableHeightListWidget(
      MinecraftClient client,
      int left,
      int top,
      int width,
      int height,
      int contentPadding,
      int rowPadding) {
    this.client = client;
    this.left = left;
    this.right = left + width;
    this.top = top;
    this.bottom = top + height;
    this.width = width;
    this.height = height;
    this.contentPadding = contentPadding;
    this.rowPadding = rowPadding;
    this.entries = new PositionalLinkedList<>(this.rowPadding);
  }

  public <T extends E> T addEntry(T entry) {
    this.entries.add(entry);
    return entry;
  }

  public void clearEntries() {
    this.entries.clear();
  }

  @Override
  public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    this.hoveredEntry =
        this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;

    this.renderBackground(drawContext, delta);

    drawContext.enableScissor(this.left, this.top, this.right, this.bottom);
    this.renderList(drawContext, mouseX, mouseY, delta);
    this.renderScrollBar(drawContext, mouseX, mouseY, delta);
    drawContext.disableScissor();
  }

  protected void renderList(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    for (int i = 0; i < this.entries.size(); i++) {
      this.renderEntry(drawContext, i, mouseX, mouseY, delta);
    }
  }

  protected void renderEntry(
      DrawContext drawContext, int index, int mouseX, int mouseY, float delta) {
    E entry = this.entries.get(index);
    double scrollAmount = this.getScrollAmount();

    double scrolledTop = entry.getTop() - scrollAmount;
    double scrolledBottom = entry.getTop() + entry.getHeight() - scrollAmount;

    if (scrolledBottom < this.top || scrolledTop > this.bottom) {
      return;
    }

    entry.render(drawContext, index, scrollAmount, mouseX, mouseY, delta);
  }

  protected void renderScrollBar(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    int maxScroll = this.getMaxScroll();
    if (maxScroll <= 0) {
      return;
    }

    int scrollbarLeft = this.getScrollbarPositionX();
    int scrollbarRight = scrollbarLeft + GuiUtil.SCROLLBAR_WIDTH;

    RenderSystem.setShader(GameRenderer::getPositionColorProgram);


    int handleHeight = (int) ((float) this.height * this.height / this.getContentHeight());
    handleHeight = MathHelper.clamp(handleHeight, 32, this.height - 8);

    int handleTop = (int) this.scrollAmount * (this.height - handleHeight) / maxScroll + this.top;
    if (handleTop < this.top) {
      handleTop = this.top;
    }

    drawContext.fill(scrollbarLeft,
        this.top,
        scrollbarRight,
        this.bottom,
        GuiUtil.genColorInt(0, 0, 0));
    drawContext.fill(scrollbarLeft,
        handleTop,
        scrollbarRight,
        handleTop + handleHeight,
        GuiUtil.genColorInt(0.5f, 0.5f, 0.5f));
    drawContext.fill(scrollbarLeft,
        handleTop,
        scrollbarRight - 1,
        handleTop + handleHeight - 2,
        GuiUtil.genColorInt(0.75f, 0.75f, 0.75f));
  }

  protected void renderBackground(DrawContext drawContext, float delta) {
    Screen parent = this.client.currentScreen;
    int screenWidth = parent != null ? parent.width : this.width;

    GuiUtil.renderBackgroundInRegion(32,
        this.top,
        this.bottom,
        0,
        screenWidth,
        0,
        (int) this.scrollAmount);

    this.renderHorizontalShadows(drawContext, delta);
  }

  protected void renderHorizontalShadows(DrawContext drawContext, float delta) {
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
  public ScreenRect getNavigationFocus() {
    return new ScreenRect(this.left, this.top, this.width, this.height);
  }

  @Override
  @SuppressWarnings("unchecked")
  public E getFocused() {
    // Suppress warning because we know that the focused element will only ever be an entry
    return (E) super.getFocused();
  }

  @Override
  public void setFocused(Element focused) {
    super.setFocused(focused);

    E entry = this.getFocused();
    if (entry == null) {
      return;
    }

    if (this.client.getNavigationType().isKeyboard()) {
      this.ensureVisible(entry);
    }
  }

  @Override
  public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
    if (this.entries.isEmpty()) {
      return null;
    }

    if (!(navigation instanceof GuiNavigation.Arrow arrow)) {
      return super.getNavigationPath(navigation);
    }

    E entry = this.getFocused();

    if (arrow.direction().getAxis() == NavigationAxis.HORIZONTAL && entry != null) {
      return GuiNavigationPath.of(this, entry.getNavigationPath(navigation));
    }

    int index = -1;
    NavigationDirection direction = arrow.direction();

    if (entry != null) {
      index = entry.children().indexOf(entry.getFocused());
    }

    if (index == -1) {
      switch (direction) {
        case LEFT -> {
          index = Integer.MAX_VALUE;
          direction = NavigationDirection.DOWN;
        }
        case RIGHT -> {
          index = 0;
          direction = NavigationDirection.DOWN;
        }
        default -> index = 0;
      }
    }

    GuiNavigationPath path;
    do {
      entry = this.getNeighboringEntry(direction, (element) -> !element.children().isEmpty());
      if (entry == null) {
        return null;
      }
      path = entry.getNavigationPath(arrow, index);
    } while (path == null);

    return GuiNavigationPath.of(this, path);
  }

  protected E getNeighboringEntry(NavigationDirection direction) {
    return this.getNeighboringEntry(direction, (entry) -> true);
  }

  protected E getNeighboringEntry(NavigationDirection direction, Predicate<E> predicate) {
    return this.getNeighboringEntry(direction, predicate, this.getFocused());
  }

  protected E getNeighboringEntry(
      NavigationDirection direction, Predicate<E> predicate, E focused) {
    if (this.entries.isEmpty()) {
      return null;
    }

    int delta = switch (direction) {
      case UP -> -1;
      case DOWN -> 1;
      default -> 0;
    };

    if (delta == 0) {
      return null;
    }

    int index;
    if (focused == null) {
      index = delta > 0 ? 0 : this.entries.size() - 1;
    } else {
      index = this.entries.indexOf(focused) + delta;
    }

    for (int i = index; i >= 0 && i < this.entries.size(); i += delta) {
      E entry = this.entries.get(i);
      if (predicate.test(entry)) {
        return entry;
      }
    }

    return null;
  }

  @Override
  public List<? extends Element> children() {
    return this.entries.asList();
  }

  @Override
  public SelectionType getType() {
    if (this.isFocused()) {
      return SelectionType.FOCUSED;
    } else {
      return this.hoveredEntry != null ? SelectionType.HOVERED : SelectionType.NONE;
    }
  }

  protected E getHoveredEntry() {
    return this.hoveredEntry;
  }

  protected void appendNarrations(NarrationMessageBuilder builder, E entry) {
    List<E> list = this.entries.asList();
    if (list.size() > 1) {
      int i = list.indexOf(entry);
      if (i != -1) {
        builder.put(NarrationPart.POSITION,
            Text.translatable("narrator.position.list", i + 1, list.size()));
      }
    }
  }

  @Override
  public void appendNarrations(NarrationMessageBuilder builder) {
    E hovered = this.getHoveredEntry();
    if (hovered != null) {
      hovered.appendNarrations(builder.nextMessage());
      this.appendNarrations(builder, hovered);
    } else {
      E focused = this.getFocused();
      if (focused != null) {
        focused.appendNarrations(builder.nextMessage());
        this.appendNarrations(builder, focused);
      }
    }

    builder.put(NarrationPart.USAGE, Text.translatable("narration.component_list.usage"));
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    return mouseX >= this.left && mouseX <= this.right && mouseY >= this.top &&
        mouseY <= this.bottom;
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
    }

    if (button == 0 && this.scrolling) {
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
    }

    return false;
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

    return this.entries.getEntryAtPosition(y + this.scrollAmount);
  }

  protected void ensureVisible(E entry) {
    int scrolledTop = entry.getTop() - (int) this.scrollAmount;
    if (scrolledTop < this.top + this.contentPadding) {
      this.scroll(scrolledTop - this.top - this.contentPadding);
      return;
    }

    int scrolledBottom = entry.getTop() + entry.getHeight() - (int) this.scrollAmount;
    if (scrolledBottom > this.bottom - this.contentPadding) {
      this.scroll(scrolledBottom - this.bottom + this.contentPadding);
    }
  }

  protected int getContentHeight() {
    return this.entries.totalHeight + this.contentPadding * 2;
  }

  protected int getContentWidth() {
    return this.width - (GuiUtil.SCROLLBAR_WIDTH + this.contentPadding) * 2;
  }

  protected int getContentLeft() {
    return this.left + GuiUtil.SCROLLBAR_WIDTH + this.contentPadding;
  }

  protected int getContentRight() {
    return this.right - GuiUtil.SCROLLBAR_WIDTH - this.contentPadding;
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

  public int nextTop() {
    return this.top + this.contentPadding + this.entries.nextTop();
  }

  public abstract static class Entry<E extends Entry<E>> extends AbstractParentElement {
    protected static final int ROW_SHADE_STRENGTH = 85;
    protected static final int ROW_SHADE_FADE_WIDTH = 10;
    protected static final int ROW_SHADE_FADE_OVERFLOW = 10;

    protected final MinecraftClient client;
    protected final VariableHeightListWidget<E> parent;

    private final int left;
    private final int top;
    private final int width;
    private final int height;

    private Element focused;
    private Selectable focusedSelectable;

    public Entry(MinecraftClient client, VariableHeightListWidget<E> parent, int height) {
      this.client = client;
      this.parent = parent;
      this.left = parent.getContentLeft();
      this.width = parent.getContentWidth();
      this.top = parent.nextTop();
      this.height = height;
    }

    public ConfigListWidget getParent() {
      return (ConfigListWidget) this.parent;
    }

    public MinecraftClient getClient() {
      return this.client;
    }

    public TextRenderer getTextRenderer() {
      return this.client.textRenderer;
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

    public void render(
        DrawContext drawContext,
        int index,
        double scrollAmount,
        int mouseX,
        int mouseY,
        float delta) {
      this.renderBackground(drawContext, index, scrollAmount, mouseX, mouseY, delta);
      this.renderContent(drawContext, index, scrollAmount, mouseX, mouseY, delta);
      this.renderDecorations(drawContext, index, scrollAmount, mouseX, mouseY, delta);
    }

    public void renderBackground(
        DrawContext drawContext,
        int index,
        double scrollAmount,
        int mouseX,
        int mouseY,
        float delta) {
      if (index % 2 == 0) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();

        int left = this.left - ROW_SHADE_FADE_OVERFLOW - this.parent.contentPadding / 2;
        int right =
            this.left + this.width + ROW_SHADE_FADE_OVERFLOW + this.parent.contentPadding / 2 + 1;
        int top = this.top - (int) scrollAmount - this.parent.rowPadding / 2;
        int bottom = this.top + this.height - (int) scrollAmount + this.parent.rowPadding / 2 + 1;

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix4f, left + ROW_SHADE_FADE_WIDTH, top, 0)
            .color(0, 0, 0, ROW_SHADE_STRENGTH)
            .next();
        bufferBuilder.vertex(matrix4f, left, top, 0).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(matrix4f, left, bottom, 0).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(matrix4f, left + ROW_SHADE_FADE_WIDTH, bottom, 0)
            .color(0, 0, 0, ROW_SHADE_STRENGTH)
            .next();

        bufferBuilder.vertex(matrix4f, right - ROW_SHADE_FADE_WIDTH, top, 0)
            .color(0, 0, 0, ROW_SHADE_STRENGTH)
            .next();
        bufferBuilder.vertex(matrix4f, left + ROW_SHADE_FADE_WIDTH, top, 0)
            .color(0, 0, 0, ROW_SHADE_STRENGTH)
            .next();
        bufferBuilder.vertex(matrix4f, left + ROW_SHADE_FADE_WIDTH, bottom, 0)
            .color(0, 0, 0, ROW_SHADE_STRENGTH)
            .next();
        bufferBuilder.vertex(matrix4f, right - ROW_SHADE_FADE_WIDTH, bottom, 0)
            .color(0, 0, 0, ROW_SHADE_STRENGTH)
            .next();

        bufferBuilder.vertex(matrix4f, right, top, 0).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(matrix4f, right - ROW_SHADE_FADE_WIDTH, top, 0)
            .color(0, 0, 0, ROW_SHADE_STRENGTH)
            .next();
        bufferBuilder.vertex(matrix4f, right - ROW_SHADE_FADE_WIDTH, bottom, 0)
            .color(0, 0, 0, ROW_SHADE_STRENGTH)
            .next();
        bufferBuilder.vertex(matrix4f, right, bottom, 0).color(0, 0, 0, 0).next();
        tessellator.draw();

        RenderSystem.disableBlend();
      }
    }

    public void renderContent(
        DrawContext drawContext,
        int index,
        double scrollAmount,
        int mouseX,
        int mouseY,
        float delta) {

    }

    public void renderDecorations(
        DrawContext drawContext,
        int index,
        double scrollAmount,
        int mouseX,
        int mouseY,
        float delta) {

    }

    public List<? extends Selectable> selectableChildren() {
      return this.children()
          .stream()
          .filter(Selectable.class::isInstance)
          .map(Selectable.class::cast)
          .toList();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
      if (!this.parent.isMouseOver(mouseX, mouseY)) {
        return false;
      }

      return mouseX >= this.getLeft() && mouseX <= this.getRight() && mouseY >= this.getTop() &&
          mouseY <= this.getBottom();
    }

    @Override
    public void setFocused(Element focused) {
      if (this.focused != null) {
        this.focused.setFocused(false);
      }

      if (focused != null) {
        focused.setFocused(true);
      }

      this.focused = focused;
    }

    @Override
    public Element getFocused() {
      return this.focused;
    }

    public GuiNavigationPath getNavigationPath(GuiNavigation navigation, int index) {
      if (this.children().isEmpty()) {
        return null;
      }

      Element child = this.children().get(Math.min(index, this.children().size() - 1));
      GuiNavigationPath path = child.getNavigationPath(navigation);
      return GuiNavigationPath.of(this, path);
    }

    @Override
    public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
      if (!(navigation instanceof GuiNavigation.Arrow arrow)) {
        return super.getNavigationPath(navigation);
      }

      int delta = switch (arrow.direction()) {
        case LEFT -> -1;
        case RIGHT -> 1;
        default -> 0;
      };

      if (delta == 0) {
        return null;
      }

      int index = MathHelper.clamp(delta + this.children().indexOf(this.getFocused()),
          0,
          this.children().size() - 1);

      for (int i = index; i >= 0 && i < this.children().size(); i += delta) {
        GuiNavigationPath path = this.children().get(i).getNavigationPath(navigation);
        if (path != null) {
          return GuiNavigationPath.of(this, path);
        }
      }

      return super.getNavigationPath(navigation);
    }

    void appendNarrations(NarrationMessageBuilder builder) {
      List<? extends Selectable> list = this.selectableChildren();
      Screen.SelectedElementNarrationData data =
          Screen.findSelectedElementData(list, this.focusedSelectable);

      if (data != null) {
        if (data.selectType.isFocused()) {
          this.focusedSelectable = data.selectable;
        }

        if (list.size() > 1) {
          builder.put(NarrationPart.POSITION,
              Text.translatable("narrator.position.object_list", data.index + 1, list.size()));
          if (data.selectType == Selectable.SelectionType.FOCUSED) {
            builder.put(NarrationPart.USAGE, Text.translatable("narration.component_list.usage"));
          }
        }

        data.selectable.appendNarrations(builder.nextMessage());
      }
    }
  }

  protected static class PositionalLinkedList<E extends VariableHeightListWidget.Entry<E>>
      implements Iterable<E> {
    private final LinkedList<E> entries = new LinkedList<>();
    private final int rowPadding;
    private int totalHeight;
    private double averageItemHeight;

    protected PositionalLinkedList(int rowPadding) {
      this.rowPadding = rowPadding;
    }

    public List<E> asList() {
      return List.copyOf(this.entries);
    }

    public void add(E entry) {
      boolean first = this.entries.isEmpty();

      this.entries.add(entry);
      this.totalHeight += entry.getHeight() + (first ? 0 : rowPadding);
      this.averageItemHeight = (double) this.totalHeight / (double) this.entries.size();
    }

    public E get(int index) {
      return this.entries.get(index);
    }

    public int size() {
      return this.entries.size();
    }

    public boolean isEmpty() {
      return this.entries.isEmpty();
    }

    public void clear() {
      this.entries.clear();

      this.totalHeight = 0;
      this.averageItemHeight = 0;
    }

    public int nextTop() {
      if (this.entries.isEmpty()) {
        return 0;
      }

      return this.totalHeight + this.rowPadding;
    }

    public E getEntryAtPosition(double y) {
      for (E entry : this.entries) {
        if (y >= entry.getTop() && y <= entry.getBottom()) {
          return entry;
        }
      }

      return null;
    }

    public int indexOf(E entry) {
      return this.entries.indexOf(entry);
    }

    @Override
    public Iterator<E> iterator() {
      return this.entries.iterator();
    }
  }
}
