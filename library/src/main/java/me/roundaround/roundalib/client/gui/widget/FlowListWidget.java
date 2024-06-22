package me.roundaround.roundalib.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.Spacing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.navigation.NavigationAxis;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.render.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public abstract class FlowListWidget<E extends FlowListWidget.Entry> extends ContainerWidget implements LayoutWidget {
  protected static final Identifier SCROLLER_TEXTURE = new Identifier("widget/scroller");
  protected static final Identifier SCROLLER_BACKGROUND_TEXTURE = new Identifier("widget/scroller_background");
  protected static final Identifier MENU_LIST_BACKGROUND_TEXTURE = new Identifier(
      "textures/gui/menu_list_background.png");
  protected static final Identifier INWORLD_MENU_LIST_BACKGROUND_TEXTURE = new Identifier(
      "textures/gui/inworld_menu_list_background.png");
  protected static final int DEFAULT_SHADE_STRENGTH = 50;
  protected static final int DEFAULT_FADE_WIDTH = 10;
  protected static final int VANILLA_LIST_WIDTH_S = 220;
  protected static final int VANILLA_LIST_WIDTH_M = 280;
  protected static final int VANILLA_LIST_WIDTH_L = 340;

  protected final MinecraftClient client;
  protected final ThreePartsLayoutWidget parentLayout;

  protected E hoveredEntry;
  protected E selectedEntry;
  protected Double scrollUnit;

  private final LinkedList<E> entries = new LinkedList<>();

  private int contentHeight = 0;
  private Spacing contentPadding = Spacing.zero();
  private int rowSpacing = 0;
  private double scrollAmount = 0;
  private boolean scrolling = false;
  private boolean alternatingRowShading = false;
  private int shadeFadeWidth = DEFAULT_FADE_WIDTH;
  private int shadeStrength = DEFAULT_SHADE_STRENGTH;

  protected FlowListWidget(MinecraftClient client, ThreePartsLayoutWidget layout) {
    super(0, layout.getHeaderHeight(), layout.getWidth(), layout.getContentHeight(), ScreenTexts.EMPTY);

    this.client = client;
    this.parentLayout = layout;
  }

  protected FlowListWidget(MinecraftClient client, int x, int y, int width, int height) {
    super(x, y, width, height, ScreenTexts.EMPTY);

    this.client = client;
    this.parentLayout = null;
  }

  public <T extends E> T addEntry(EntryFactory<T> factory) {
    int lastBottom = this.getLastEntryBottom();
    T entry = factory.create(this.entries.size(), this.getContentLeft(), lastBottom + this.rowSpacing,
        this.getContentWidth()
    );

    if (entry == null) {
      return null;
    }

    this.entries.add(entry);
    this.contentHeight += entry.getHeight();

    if (this.entries.size() > 1) {
      this.contentHeight += this.rowSpacing;
    }

    if (this.selectedEntry != null) {
      this.ensureVisible(this.selectedEntry);
    }

    return entry;
  }

  public void clearEntries() {
    this.entries.clear();
    this.contentHeight = this.contentPadding.getVertical();
    this.selectedEntry = null;
  }

  @Override
  public List<? extends Element> children() {
    return this.getEntries();
  }

  public List<E> getEntries() {
    return List.copyOf(this.entries);
  }

  public E getEntry(int index) {
    return this.entries.get(index);
  }

  public int getEntryCount() {
    return this.entries.size();
  }

  public void forEachEntry(Consumer<E> consumer) {
    this.entries.forEach(consumer);
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
    this.entries.forEach(consumer);
  }

  @Override
  public void refreshPositions() {
    if (this.parentLayout != null) {
      this.setDimensionsAndPosition(
          this.parentLayout.getWidth(), this.parentLayout.getContentHeight(), 0, this.parentLayout.getHeaderHeight());
    }

    this.contentHeight = this.contentPadding.getVertical();

    int entryY = this.getContentTop();
    for (E entry : this.entries) {
      entry.setPosition(this.getContentLeft(), entryY);
      entry.setWidth(this.getContentWidth());

      entryY += entry.getHeight() + this.rowSpacing;
      this.contentHeight += entry.getHeight() + this.rowSpacing;
    }

    this.contentHeight -= this.rowSpacing;

    this.setScrollAmount(this.getScrollAmount());

    LayoutWidget.super.refreshPositions();
  }

  @Override
  public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    this.hoveredEntry = this.getEntryAtPosition(mouseX, mouseY);

    this.renderListBackground(context);
    this.renderList(context, mouseX, mouseY, delta);
    this.renderListBorders(context);
  }

  protected void renderListBackground(DrawContext context) {
    RenderSystem.enableBlend();
    Identifier identifier =
        this.client.world == null ? MENU_LIST_BACKGROUND_TEXTURE : INWORLD_MENU_LIST_BACKGROUND_TEXTURE;
    context.drawTexture(identifier, this.getX(), this.getY(), this.getRight(),
        this.getBottom() + (int) this.getScrollAmount(), this.getWidth(), this.getHeight(), 32, 32
    );
    RenderSystem.disableBlend();
  }

  protected void renderList(DrawContext context, int mouseX, int mouseY, float delta) {
    context.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
    this.renderEntries(context, mouseX, mouseY, delta);
    this.renderScrollBar(context);
    context.disableScissor();
  }

  protected void renderEntries(DrawContext context, int mouseX, int mouseY, float delta) {
    this.entries.forEach((entry) -> {
      if (!this.isEntryVisible(entry)) {
        return;
      }
      this.renderEntry(context, mouseX, mouseY, delta, entry);
    });
  }

  protected void renderEntry(DrawContext context, int mouseX, int mouseY, float delta, E entry) {
    if (this.alternatingRowShading && entry.getIndex() % 2 == 0) {
      this.renderRowShade(context, entry);
    }

    entry.render(context, mouseX, mouseY, delta);
  }

  protected void renderRowShade(DrawContext context, E entry) {
    if (entry.getIndex() % 2 != 0) {
      return;
    }

    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.setShader(GameRenderer::getPositionColorProgram);

    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();

    int left = entry.getX();
    int right = entry.getRight();
    int top = entry.getY();
    int bottom = entry.getBottom();

    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
    bufferBuilder.vertex(matrix4f, left + this.shadeFadeWidth, top, 0).color(0, 0, 0, this.shadeStrength).next();
    bufferBuilder.vertex(matrix4f, left, top, 0).color(0, 0, 0, 0).next();
    bufferBuilder.vertex(matrix4f, left, bottom, 0).color(0, 0, 0, 0).next();
    bufferBuilder.vertex(matrix4f, left + this.shadeFadeWidth, bottom, 0).color(0, 0, 0, this.shadeStrength).next();

    bufferBuilder.vertex(matrix4f, right - this.shadeFadeWidth, top, 0).color(0, 0, 0, this.shadeStrength).next();
    bufferBuilder.vertex(matrix4f, left + this.shadeFadeWidth, top, 0).color(0, 0, 0, this.shadeStrength).next();
    bufferBuilder.vertex(matrix4f, left + this.shadeFadeWidth, bottom, 0).color(0, 0, 0, this.shadeStrength).next();
    bufferBuilder.vertex(matrix4f, right - this.shadeFadeWidth, bottom, 0).color(0, 0, 0, this.shadeStrength).next();

    bufferBuilder.vertex(matrix4f, right, top, 0).color(0, 0, 0, 0).next();
    bufferBuilder.vertex(matrix4f, right - this.shadeFadeWidth, top, 0).color(0, 0, 0, this.shadeStrength).next();
    bufferBuilder.vertex(matrix4f, right - this.shadeFadeWidth, bottom, 0).color(0, 0, 0, this.shadeStrength).next();
    bufferBuilder.vertex(matrix4f, right, bottom, 0).color(0, 0, 0, 0).next();
    tessellator.draw();

    RenderSystem.disableBlend();
  }

  protected void renderListBorders(DrawContext context) {
    Identifier headerSepTex =
        this.client.world == null ? Screen.HEADER_SEPARATOR_TEXTURE : Screen.INWORLD_HEADER_SEPARATOR_TEXTURE;
    Identifier footerSepTex =
        this.client.world == null ? Screen.FOOTER_SEPARATOR_TEXTURE : Screen.INWORLD_FOOTER_SEPARATOR_TEXTURE;
    RenderSystem.enableBlend();
    context.drawTexture(headerSepTex, this.getX(), this.getY() - 2, 0, 0, this.getWidth(), 2, 32, 2);
    context.drawTexture(footerSepTex, this.getX(), this.getBottom(), 0, 0, this.getWidth(), 2, 32, 2);
    RenderSystem.disableBlend();
  }

  protected void renderScrollBar(DrawContext context) {
    if (!this.isScrollbarVisible()) {
      return;
    }

    int x = this.getScrollbarPositionX();
    int handleHeight = this.getScrollbarHandleHeight();
    double yPercent = this.getScrollAmount() / this.getMaxScroll();
    int movableSpace = this.getHeight() - handleHeight;
    int handleY = this.getY() + (int) Math.round(yPercent * movableSpace);

    RenderSystem.enableBlend();
    context.drawGuiTexture(SCROLLER_BACKGROUND_TEXTURE, x, this.getY(), GuiUtil.SCROLLBAR_WIDTH, this.getHeight());
    context.drawGuiTexture(SCROLLER_TEXTURE, x, handleY, GuiUtil.SCROLLBAR_WIDTH, handleHeight);
    RenderSystem.disableBlend();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setFocused(Element focused) {
    super.setFocused(focused);

    Element element = this.getFocused();
    if (!(element instanceof Entry entry)) {
      return;
    }

    this.setSelected((E) entry);
    if (this.client.getNavigationType().isKeyboard()) {
      this.ensureVisible((E) entry);
    }
  }

  public E getSelected() {
    return this.selectedEntry;
  }

  public void setSelected(E entry) {
    this.selectedEntry = entry;
  }

  public void selectFirst() {
    if (this.getEntryCount() > 0) {
      this.setSelected(this.getEntry(0));
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
    if (this.getEntryCount() == 0) {
      return null;
    }

    if (!(navigation instanceof GuiNavigation.Arrow arrow)) {
      return super.getNavigationPath(navigation);
    }

    Element element = this.getFocused();
    if (!(element == null || element instanceof Entry)) {
      return super.getNavigationPath(navigation);
    }

    E entry = (E) element;

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
      entry = this.getNeighboringEntry(direction, (e) -> !e.children().isEmpty());
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
    return this.getNeighboringEntry(direction, predicate, this.getSelected());
  }

  protected E getNeighboringEntry(NavigationDirection direction, Predicate<E> predicate, E focused) {
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
  public SelectionType getType() {
    if (this.isFocused()) {
      return SelectionType.FOCUSED;
    } else {
      return this.getHoveredEntry() != null ? SelectionType.HOVERED : SelectionType.NONE;
    }
  }

  protected E getHoveredEntry() {
    return this.hoveredEntry;
  }

  protected void appendNarrations(NarrationMessageBuilder builder, E entry) {
    if (this.entries.size() > 1) {
      int i = this.entries.indexOf(entry);
      if (i != -1) {
        builder.put(NarrationPart.POSITION, Text.translatable("narrator.position.list", i + 1, this.entries.size()));
      }
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void appendClickableNarrations(NarrationMessageBuilder builder) {
    E hovered = this.getHoveredEntry();
    if (hovered != null) {
      hovered.appendNarrations(builder.nextMessage());
      this.appendNarrations(builder, hovered);
    } else {
      Element focusedElement = this.getFocused();
      if (focusedElement instanceof Entry focused) {
        focused.appendNarrations(builder.nextMessage());
        this.appendNarrations(builder, (E) focused);
      }
    }

    builder.put(NarrationPart.USAGE, Text.translatable("narration.component_list.usage"));
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    return mouseX >= this.getX() && mouseX <= this.getRight() && mouseY >= this.getY() && mouseY <= this.getBottom();
  }

  protected boolean isSelectButton(int button) {
    return button == 0;
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (!this.isSelectButton(button)) {
      return false;
    }

    this.updateScrollingState(mouseX, mouseY, button);

    if (!this.isMouseOver(mouseX, mouseY)) {
      return false;
    }

    E entry = this.getEntryAtPosition(mouseX, mouseY);
    if (entry != null) {
      if (entry.mouseClicked(mouseX, mouseY, button)) {
        Element focused = this.getFocused();
        if (focused != entry && focused instanceof ParentElement parent) {
          parent.setFocused(null);
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
  public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
      return true;
    }

    if (!this.isSelectButton(button) || !this.scrolling) {
      return false;
    }

    if (mouseY < this.getY()) {
      this.setScrollAmount(0);
    } else if (mouseY > this.getBottom()) {
      this.setScrollAmount(this.getMaxScroll());
    } else {
      double max = Math.max(1, this.getMaxScroll());
      int movableSpace = this.getHeight() - this.getScrollbarHandleHeight();
      double scale = Math.max(1, max / movableSpace);
      this.setScrollAmount(this.getScrollAmount() + deltaY * scale);
    }

    return true;
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    if (this.allowScrollPassThrough()) {
      E entry = this.getEntryAtPosition(mouseX, mouseY);
      if (entry != null && entry.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
        return true;
      }
    }

    this.setScrollAmount(this.getScrollAmount() - verticalAmount * this.getScrollUnit());
    return true;
  }

  protected boolean allowScrollPassThrough() {
    return false;
  }

  protected E getEntryAtPosition(double x, double y) {
    if (!this.isMouseOver(x, y)) {
      return null;
    }

    for (E entry : this.entries) {
      if (entry.isMouseOver(x, y)) {
        return entry;
      }
    }

    return null;
  }

  protected boolean isEntryVisible(E entry) {
    return entry.getY() <= this.getContentBottom() && entry.getBottom() >= this.getContentTop();
  }

  protected void ensureVisible(E entry) {
    if (entry.getY() < this.getContentTop()) {
      this.scroll(entry.getY() - this.getContentTop());
      return;
    }

    if (entry.getBottom() > this.getContentBottom()) {
      this.scroll(entry.getBottom() - this.getContentBottom());
    }
  }

  protected boolean isScrollbarVisible() {
    return this.getMaxScroll() > 0;
  }

  public void setContentPaddingX(int paddingX) {
    this.contentPadding = this.contentPadding.setHorizontal(paddingX);
  }

  public void setContentPaddingY(int paddingX) {
    this.contentPadding = this.contentPadding.setVertical(paddingX);
  }

  public void setContentPadding(int padding) {
    this.contentPadding = Spacing.of(padding);
  }

  public void setContentPadding(Spacing padding) {
    this.contentPadding = padding;
  }

  public void setRowSpacing(int rowSpacing) {
    this.rowSpacing = rowSpacing;
  }

  public int getContentWidth() {
    return Math.min(this.getPreferredContentWidth(), this.getMaxContentWidth());
  }

  public int getScrollPaneHeight() {
    return this.contentHeight;
  }

  public int getContentTop() {
    return this.getY() + this.contentPadding.top();
  }

  public int getContentBottom() {
    return this.getBottom() - this.contentPadding.bottom();
  }

  public int getContentLeft() {
    return this.getX() + (this.getWidth() - this.getContentWidth()) / 2;
  }

  public int getContentRight() {
    return this.getX() + (this.getWidth() + this.getContentWidth()) / 2;
  }

  protected int getLastEntryBottom() {
    if (this.entries.isEmpty()) {
      return this.getContentTop() - this.rowSpacing;
    }
    return this.entries.getLast().getBottom();
  }

  protected int getPreferredContentWidth() {
    if (this.parentLayout == null || this.getWidth() <= VANILLA_LIST_WIDTH_S) {
      return this.getWidth();
    }
    if (this.getWidth() < VANILLA_LIST_WIDTH_L) {
      return VANILLA_LIST_WIDTH_M;
    }
    return VANILLA_LIST_WIDTH_L;
  }

  protected int getMaxContentWidth() {
    return this.getMaxContentRight() - this.getMinContentLeft();
  }

  protected int getMinContentLeft() {
    return this.getX() + 2 * this.getScrollbarPadding() + GuiUtil.SCROLLBAR_WIDTH + this.contentPadding.left();
  }

  protected int getMaxContentRight() {
    return this.getRight() - 2 * this.getScrollbarPadding() - GuiUtil.SCROLLBAR_WIDTH - this.contentPadding.right();
  }

  protected int getScrollbarPositionX() {
    return this.getContentRight() +
        (this.getWidth() < VANILLA_LIST_WIDTH_L ? 2 * this.getScrollbarPadding() : this.getScrollbarPadding());
  }

  protected int getScrollbarPadding() {
    return GuiUtil.PADDING;
  }

  protected int getScrollbarHandleHeight() {
    int height = this.getHeight();
    return MathHelper.clamp(height * height / this.getScrollPaneHeight(), 32, height - 8);
  }

  private void scroll(int amount) {
    this.setScrollAmount(this.getScrollAmount() + (double) amount);
  }

  public double getScrollAmount() {
    return this.scrollAmount;
  }

  public void setScrollAmount(double amount) {
    this.scrollAmount = MathHelper.clamp(amount, 0, this.getMaxScroll());
    this.entries.forEach((entry) -> entry.setScrollAmount(this.getScrollAmount()));
  }

  public int getMaxScroll() {
    return Math.max(0, this.getScrollPaneHeight() - this.getHeight());
  }

  protected void updateScrollingState(double mouseX, double mouseY, int button) {
    this.scrolling = button == 0 && mouseX >= (double) this.getScrollbarPositionX() &&
        mouseX < (this.getScrollbarPositionX() + GuiUtil.SCROLLBAR_WIDTH);
  }

  protected double getScrollUnit() {
    if (this.scrollUnit != null) {
      return this.scrollUnit;
    }
    double averageHeight = (double) this.contentHeight / this.entries.size();
    return averageHeight / 2;
  }

  protected void alternatingRowShading(boolean alternatingRowShading) {
    this.alternatingRowShading = alternatingRowShading;
  }

  protected void rowShadeFadeWidth(int width) {
    this.shadeFadeWidth = Math.max(width, 0);
  }

  protected void rowShadeStrength(int strength) {
    this.shadeStrength = Math.clamp(strength, 0, 255);
  }

  protected void rowShadeStrength(float strength) {
    this.rowShadeStrength((int) (strength * 255));
  }

  @FunctionalInterface
  public interface EntryFactory<E extends Entry> {
    E create(int index, int left, int top, int width);
  }

  @Environment(EnvType.CLIENT)
  public abstract static class Entry implements Drawable, LayoutWidget, ParentElement, Narratable {
    protected static final Spacing DEFAULT_MARGIN = Spacing.of(GuiUtil.PADDING / 2);

    private final ArrayList<WrappedLayoutWidget<? extends LayoutWidget>> layouts = new ArrayList<>();
    private final ArrayList<Element> children = new ArrayList<>();
    private final ArrayList<Selectable> selectables = new ArrayList<>();
    private final ArrayList<Drawable> drawables = new ArrayList<>();
    private final int index;
    private final int contentHeight;

    private int x;
    private int y;
    private int width;
    private Element focused;
    private Selectable focusedSelectable;
    private boolean dragging;
    private double scrollAmount;
    private Spacing margin = DEFAULT_MARGIN;

    protected Entry(int index, int x, int y, int width, int contentHeight) {
      this.index = index;
      this.x = x;
      this.y = y;
      this.width = width;
      this.contentHeight = contentHeight;
    }

    protected <T extends LayoutWidget> T addLayout(T layout) {
      return this.addLayout(layout, LayoutHook.noop());
    }

    protected <T extends LayoutWidget> T addLayout(T layout, LayoutHook<T> layoutHook) {
      this.layouts.add(new WrappedLayoutWidget<>(layout, layoutHook));
      return layout;
    }

    protected <T extends Element> T addChild(T child) {
      this.children.add(child);
      if (child instanceof Drawable drawable) {
        this.drawables.add(drawable);
      }
      if (child instanceof Selectable selectable) {
        this.selectables.add(selectable);
      }
      return child;
    }

    protected <T extends Element & Drawable & Selectable> T addDrawableChild(T child) {
      this.drawables.add(child);
      return this.addSelectableChild(child);
    }

    protected <T extends Drawable> T addDrawable(T drawable) {
      this.drawables.add(drawable);
      return drawable;
    }

    protected <T extends Element & Selectable> T addSelectableChild(T child) {
      this.children.add(child);
      this.selectables.add(child);
      return child;
    }

    protected void clearChildren() {
      this.layouts.clear();
      this.children.clear();
      this.drawables.clear();
      this.selectables.clear();
    }

    @Override
    public List<? extends Element> children() {
      return this.children;
    }

    public List<? extends Selectable> selectableChildren() {
      return this.selectables;
    }

    @Override
    public void forEachElement(Consumer<Widget> consumer) {
      this.layouts.forEach((wrapped) -> wrapped.apply(consumer));
      this.children().forEach((child) -> {
        if (child instanceof Widget widget) {
          consumer.accept(widget);
        }
      });
    }

    @Override
    public void refreshPositions() {
      this.layouts.forEach(WrappedLayoutWidget::refreshPositions);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      this.renderBackground(context, mouseX, mouseY, delta);
      this.renderContent(context, mouseX, mouseY, delta);
      this.renderDecorations(context, mouseX, mouseY, delta);
    }

    protected void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    protected void renderContent(DrawContext context, int mouseX, int mouseY, float delta) {
      this.drawables.forEach((drawable) -> drawable.render(context, mouseX, mouseY, delta));
    }

    protected void renderDecorations(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    protected void setScrollAmount(double scrollAmount) {
      this.scrollAmount = scrollAmount;
      this.refreshPositions();
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

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
      return mouseX >= this.getX() && mouseX <= this.getRight() && mouseY >= this.getY() && mouseY <= this.getBottom();
    }

    @Override
    public void setDragging(boolean dragging) {
      this.dragging = dragging;
    }

    @Override
    public boolean isDragging() {
      return this.dragging;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
      List<? extends Selectable> list = this.selectableChildren();
      Screen.SelectedElementNarrationData data = Screen.findSelectedElementData(list, this.focusedSelectable);

      if (data != null) {
        if (data.selectType.isFocused()) {
          this.focusedSelectable = data.selectable;
        }

        if (list.size() > 1) {
          builder.put(NarrationPart.POSITION,
              Text.translatable("narrator.position.object_list", data.index + 1, list.size())
          );
          if (data.selectType == Selectable.SelectionType.FOCUSED) {
            builder.put(NarrationPart.USAGE, Text.translatable("narration.component_list.usage"));
          }
        }

        data.selectable.appendNarrations(builder.nextMessage());
      }
    }

    @Override
    public ScreenRect getNavigationFocus() {
      return new ScreenRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
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
        return ParentElement.super.getNavigationPath(navigation);
      }

      int delta = switch (arrow.direction()) {
        case LEFT -> -1;
        case RIGHT -> 1;
        default -> 0;
      };

      if (delta == 0) {
        return null;
      }

      int index = MathHelper.clamp(delta + this.children().indexOf(this.getFocused()), 0, this.children().size() - 1);

      for (int i = index; i >= 0 && i < this.children().size(); i += delta) {
        GuiNavigationPath path = this.children().get(i).getNavigationPath(navigation);
        if (path != null) {
          return GuiNavigationPath.of(this, path);
        }
      }

      return ParentElement.super.getNavigationPath(navigation);
    }

    public int getIndex() {
      return this.index;
    }

    @Override
    public int getWidth() {
      return this.width;
    }

    public void setWidth(int width) {
      this.width = width;
    }

    @Override
    public int getHeight() {
      return this.getContentHeight() + this.margin.getVertical();
    }

    @Override
    public int getX() {
      return this.x;
    }

    @Override
    public void setX(int x) {
      this.x = x;
    }

    @Override
    public int getY() {
      return this.y - (int) this.scrollAmount;
    }

    @Override
    public void setY(int y) {
      this.y = y;
    }

    public int getRight() {
      return this.getX() + this.getWidth();
    }

    public int getBottom() {
      return this.getY() + this.getHeight();
    }

    public void setMarginY(int margin) {
      this.margin = this.margin.setVertical(margin);
    }

    public void setMarginX(int margin) {
      this.margin = this.margin.setHorizontal(margin);
    }

    public void setMargin(int margin) {
      this.margin = Spacing.of(margin);
    }

    public void setMargin(Spacing margin) {
      this.margin = margin;
    }

    public int getContentLeft() {
      return this.getX() + this.margin.left();
    }

    public int getContentRight() {
      return this.getRight() - this.margin.right();
    }

    public int getContentWidth() {
      return this.getWidth() - this.margin.getHorizontal();
    }

    public int getContentTop() {
      return this.getY() + this.margin.top();
    }

    public int getContentBottom() {
      return this.getBottom() - this.margin.bottom();
    }

    public int getContentHeight() {
      return this.contentHeight;
    }

    public int getContentCenterX() {
      return this.getContentLeft() + this.getContentWidth() / 2;
    }

    public int getContentCenterY() {
      return this.getContentTop() + this.getContentHeight() / 2;
    }

    @Environment(EnvType.CLIENT)
    public static class WrappedLayoutWidget<T extends LayoutWidget> {
      private final T wrapped;
      private final LayoutHook<T> hook;

      public WrappedLayoutWidget(T wrapped, LayoutHook<T> hook) {
        this.wrapped = wrapped;
        this.hook = hook;
      }

      public void refreshPositions() {
        this.hook.run(this.wrapped);
        this.wrapped.refreshPositions();
      }

      public void apply(Consumer<Widget> consumer) {
        consumer.accept(this.wrapped);
      }
    }
  }
}
