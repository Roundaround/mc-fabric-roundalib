package me.roundaround.roundalib.client.gui.widget;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.roundalib.RoundaLib;
import me.roundaround.roundalib.client.gui.GuiUtil;
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

public abstract class VariableHeightListWidget<E extends VariableHeightListWidget.Entry> extends ContainerWidget implements
    LayoutWidget {
  private static final Identifier SCROLLER_TEXTURE = new Identifier("widget/scroller");
  private static final Identifier SCROLLER_BACKGROUND_TEXTURE = new Identifier("widget/scroller_background");
  private static final Identifier MENU_LIST_BACKGROUND_TEXTURE = new Identifier(
      "textures/gui/menu_list_background.png");
  private static final Identifier INWORLD_MENU_LIST_BACKGROUND_TEXTURE = new Identifier(
      "textures/gui/inworld_menu_list_background.png");

  protected final MinecraftClient client;

  protected E hoveredEntry;
  protected Double scrollUnit;

  private final LinkedList<E> entries = new LinkedList<>();

  private int contentHeight = 0;
  private double scrollAmount = 0;
  private boolean scrolling = false;

  protected VariableHeightListWidget(MinecraftClient client, int x, int y, int width, int height) {
    super(x, y, width, height, ScreenTexts.EMPTY);

    this.client = client;
  }

  @SuppressWarnings("UnusedReturnValue")
  public <T extends E> T addEntry(EntryFactory<T> factory) {
    T entry = factory.create(this.entries.size(), this.getX(), this.getContentHeight(), this.getContentWidth());
    if (entry == null) {
      return null;
    }

    boolean wasScrollbarVisible = this.isScrollbarVisible();

    this.entries.add(entry);
    this.contentHeight += entry.getHeight();

    if (!wasScrollbarVisible && this.isScrollbarVisible()) {
      this.refreshPositions();
    }

    return entry;
  }

  public void clearEntries() {
    this.entries.clear();
    this.contentHeight = 0;
  }

  public void forEachEntry(Consumer<E> consumer) {
    this.entries.forEach(consumer);
  }

  public int getContentWidth() {
    return this.getWidth() - (this.isScrollbarVisible() ? GuiUtil.SCROLLBAR_WIDTH : 0);
  }

  public int getContentHeight() {
    return this.contentHeight;
  }

  @Override
  public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    this.hoveredEntry = this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;

    this.renderListBackground(context);
    this.renderList(context, mouseX, mouseY, delta);
    this.renderScrollBar(context);
    this.renderListBorders(context);
  }

  protected void renderListBackground(DrawContext context) {
    RenderSystem.enableBlend();
    Identifier identifier =
        this.client.world == null ? MENU_LIST_BACKGROUND_TEXTURE : INWORLD_MENU_LIST_BACKGROUND_TEXTURE;
    context.drawTexture(identifier, this.getX(), this.getY(), (float) this.getRight(),
        this.getBottom() + (float) this.getScrollAmount(), this.getWidth(), this.getHeight(), 32, 32
    );
    RenderSystem.disableBlend();
  }

  protected void renderList(DrawContext context, int mouseX, int mouseY, float delta) {
    int scrollAmount = (int) this.getScrollAmount();

    // Annoyingly ClickableWidget hover state is determined inside of render, which has no clean way to amend
    // or hook into. Additionally, the hover state is bounded on the DrawContext's currently registered scissor
    // so when I translate and adjust the mouseY accordingly, the ClickableWidget thinks we're outside the
    // scissor. To get around this, we need to "set the scissor" on the DrawContext using the shifted region to
    // track mouse hovering and tooltips, then overwrite that by bypassing DrawContext entirely and enabling
    // the real scissor directly on the RenderSystem.
    context.enableScissor(this.getX(), this.getY() + scrollAmount, this.getRight(), this.getBottom() + scrollAmount);
    GuiUtil.enableScissorBypassContext(this.getX(), this.getY(), this.getRight(), this.getBottom());

    context.getMatrices().push();
    context.getMatrices().translate(0, -scrollAmount, 0);

    this.entries.stream().filter(this::isEntryVisible).forEach((entry) -> {
      entry.render(context, mouseX, mouseY + scrollAmount, delta);
    });

    context.getMatrices().pop();
    context.disableScissor();
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

    int top = this.getY();
    int height = this.getHeight();
    int contentHeight = this.getContentHeight();
    double scrollAmount = this.getScrollAmount();
    int maxScroll = this.getMaxScroll();
    int scrollbarLeft = this.getScrollbarPositionX();

    int handleHeight = MathHelper.clamp(Math.round((float) height * height / contentHeight), 32, height - 8);
    int handleTop = top + (int) Math.round(scrollAmount * (height - handleHeight) / maxScroll);

    RenderSystem.enableBlend();
    context.drawGuiTexture(SCROLLER_BACKGROUND_TEXTURE, scrollbarLeft, top, GuiUtil.SCROLLBAR_WIDTH, height);
    context.drawGuiTexture(SCROLLER_TEXTURE, scrollbarLeft, handleTop, GuiUtil.SCROLLBAR_WIDTH, handleHeight);
    RenderSystem.disableBlend();
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
      NavigationDirection direction, Predicate<E> predicate, E focused
  ) {
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
    return List.copyOf(this.entries);
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
  public void appendClickableNarrations(NarrationMessageBuilder builder) {
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
    return mouseX >= this.getX() && mouseX <= this.getRight() && mouseY >= this.getY() && mouseY <= this.getBottom();
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    this.updateScrollingState(mouseX, mouseY, button);

    if (!this.isMouseOver(mouseX, mouseY)) {
      return false;
    }

    E entry = this.getEntryAtPosition(mouseX, mouseY);
    if (entry != null) {
      if (entry.mouseClicked(mouseX, mouseY + this.getScrollAmount(), button)) {
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
      this.getFocused().mouseReleased(mouseX, mouseY + this.getScrollAmount(), button);
    }

    return false;
  }

  @Override
  public boolean mouseDragged(
      double mouseX, double mouseY, int button, double deltaX, double deltaY
  ) {
    if (super.mouseDragged(mouseX, mouseY + this.getScrollAmount(), button, deltaX, deltaY)) {
      return true;
    }

    // TODO: Clean up
    if (button == 0 && this.scrolling) {
      if (mouseY < this.getY()) {
        this.setScrollAmount(0);
      } else if (mouseY > this.getBottom()) {
        this.setScrollAmount(this.getMaxScroll());
      } else {
        double d = Math.max(1, this.getMaxScroll());
        int i = this.height;
        int j = MathHelper.clamp((int) ((float) (i * i) / this.getContentHeight()), 32, i - 8);
        double e = Math.max(1.0, d / (double) (i - j));
        this.setScrollAmount(this.getScrollAmount() + deltaY * e);
      }

      return true;
    }

    return false;
  }

  @Override
  public boolean mouseScrolled(
      double mouseX, double mouseY, double horizontalAmount, double verticalAmount
  ) {
    Entry entry = this.getEntryAtPosition(mouseX, mouseY);
    if (entry != null &&
        entry.mouseScrolled(mouseX, mouseY + this.getScrollAmount(), horizontalAmount, verticalAmount)) {
      return true;
    }

    this.setScrollAmount(this.getScrollAmount() - verticalAmount * this.getScrollUnit());
    return true;
  }

  protected E getEntryAtPosition(double x, double y) {
    if (x < this.getX() || x > this.getRight()) {
      return null;
    }

    if (y < this.getY() || y > this.getBottom()) {
      return null;
    }

    double scrolledY = y + this.getScrollAmount();
    for (E entry : this.entries) {
      if (scrolledY >= entry.getTop() && scrolledY <= entry.getBottom()) {
        return entry;
      }
    }

    return null;
  }

  protected boolean isEntryVisible(E entry) {
    int scrollAmount = (int) this.getScrollAmount();
    int scrolledTop = entry.getTop() - scrollAmount;
    int scrolledBottom = entry.getBottom() - scrollAmount;

    return scrolledTop <= this.getBottom() && scrolledBottom >= this.getY();
  }

  protected void ensureVisible(E entry) {
    int scrollAmount = (int) this.getScrollAmount();

    int scrolledTop = entry.getTop() - scrollAmount;
    if (scrolledTop < this.getY()) {
      this.scroll(scrolledTop - this.getY());
      return;
    }

    int scrolledBottom = entry.getBottom() - scrollAmount;
    if (scrolledBottom > this.getBottom()) {
      this.scroll(scrolledBottom - this.getBottom());
    }
  }

  protected boolean isScrollbarVisible() {
    return this.getMaxScroll() > 0;
  }

  protected int getScrollbarPositionX() {
    return this.getRight() - GuiUtil.SCROLLBAR_WIDTH;
  }

  private void scroll(int amount) {
    this.setScrollAmount(this.getScrollAmount() + (double) amount);
  }

  public double getScrollAmount() {
    return this.scrollAmount;
  }

  public void setScrollAmount(double amount) {
    this.scrollAmount = MathHelper.clamp(amount, 0, this.getMaxScroll());
  }

  public int getMaxScroll() {
    return Math.max(0, this.getContentHeight() - this.getHeight());
  }

  protected void updateScrollingState(double mouseX, double mouseY, int button) {
    this.scrolling = button == 0 && mouseX >= (double) this.getScrollbarPositionX() &&
        mouseX < (this.getScrollbarPositionX() + GuiUtil.SCROLLBAR_WIDTH);
  }

  protected double getScrollUnit() {
    if (this.scrollUnit != null) {
      return this.scrollUnit;
    }
    double averageHeight = (double) this.getContentHeight() / this.entries.size();
    return averageHeight / 2;
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
    this.entries.forEach(consumer);
  }

  @Override
  public void refreshPositions() {
    int entryY = this.getY();
    for (E entry : this.entries) {
      entry.setPosition(this.getX(), entryY);
      entry.setWidth(this.getContentWidth());
      entry.refreshPositions();

      entryY += entry.getHeight();
    }

    this.setScrollAmount(this.getScrollAmount());

    LayoutWidget.super.refreshPositions();
  }

  @FunctionalInterface
  public interface EntryFactory<E extends Entry> {
    E create(int index, int left, int top, int width);
  }

  public abstract static class Entry extends PositionalWidget implements ParentElement {
    protected static final int ROW_SHADE_STRENGTH = 50;
    protected static final int DEFAULT_FADE_WIDTH = 10;
    protected static final int DEFAULT_MARGIN_HORIZONTAL = 10;
    protected static final int DEFAULT_MARGIN_VERTICAL = GuiUtil.PADDING / 2;

    private final ArrayList<Element> children = new ArrayList<>();
    private final ArrayList<Selectable> selectableChildren = new ArrayList<>();
    private final int index;
    private final int contentHeight;

    private int marginLeft = DEFAULT_MARGIN_HORIZONTAL;
    private int marginRight = DEFAULT_MARGIN_HORIZONTAL;
    private int marginTop = DEFAULT_MARGIN_VERTICAL;
    private int marginBottom = DEFAULT_MARGIN_VERTICAL;
    private int bgFadeWidth = DEFAULT_FADE_WIDTH;
    private Element focused;
    private Selectable focusedSelectable;
    private boolean dragging;

    protected Entry(int index, int left, int top, int width, int contentHeight) {
      super(left, top, width, 0);
      this.index = index;
      this.contentHeight = contentHeight;
    }

    protected final void addChild(Element child) {
      this.children.add(child);
    }

    protected final void addSelectableChild(Selectable selectable) {
      this.selectableChildren.add(selectable);
    }

    @Override
    public void renderPositional(DrawContext drawContext, int mouseX, int mouseY, float delta) {
      this.renderBackground(drawContext, mouseX, mouseY, delta);
      this.renderContent(drawContext, mouseX, mouseY, delta);
      this.renderDecorations(drawContext, mouseX, mouseY, delta);
    }

    public void renderBackground(DrawContext drawContext, int mouseX, int mouseY, float delta) {
      if (this.index % 2 != 0) {
        return;
      }

      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionColorProgram);

      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferBuilder = tessellator.getBuffer();
      Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();

      int left = this.getLeft();
      int right = this.getRight();
      int top = this.getTop();
      int bottom = this.getBottom();

      bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      bufferBuilder.vertex(matrix4f, left + this.bgFadeWidth, top, 0).color(0, 0, 0, ROW_SHADE_STRENGTH).next();
      bufferBuilder.vertex(matrix4f, left, top, 0).color(0, 0, 0, 0).next();
      bufferBuilder.vertex(matrix4f, left, bottom, 0).color(0, 0, 0, 0).next();
      bufferBuilder.vertex(matrix4f, left + this.bgFadeWidth, bottom, 0).color(0, 0, 0, ROW_SHADE_STRENGTH).next();

      bufferBuilder.vertex(matrix4f, right - this.bgFadeWidth, top, 0).color(0, 0, 0, ROW_SHADE_STRENGTH).next();
      bufferBuilder.vertex(matrix4f, left + this.bgFadeWidth, top, 0).color(0, 0, 0, ROW_SHADE_STRENGTH).next();
      bufferBuilder.vertex(matrix4f, left + this.bgFadeWidth, bottom, 0).color(0, 0, 0, ROW_SHADE_STRENGTH).next();
      bufferBuilder.vertex(matrix4f, right - this.bgFadeWidth, bottom, 0).color(0, 0, 0, ROW_SHADE_STRENGTH).next();

      bufferBuilder.vertex(matrix4f, right, top, 0).color(0, 0, 0, 0).next();
      bufferBuilder.vertex(matrix4f, right - this.bgFadeWidth, top, 0).color(0, 0, 0, ROW_SHADE_STRENGTH).next();
      bufferBuilder.vertex(matrix4f, right - this.bgFadeWidth, bottom, 0).color(0, 0, 0, ROW_SHADE_STRENGTH).next();
      bufferBuilder.vertex(matrix4f, right, bottom, 0).color(0, 0, 0, 0).next();
      tessellator.draw();

      RenderSystem.disableBlend();
    }

    public void renderContent(DrawContext drawContext, int mouseX, int mouseY, float delta) {
      this.children().forEach((child) -> {
        if (child instanceof Drawable drawable) {
          drawable.render(drawContext, mouseX, mouseY, delta);
        }
      });
    }

    public void renderDecorations(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    }

    @Override
    public List<? extends Element> children() {
      return ImmutableList.copyOf(this.children);
    }

    public List<? extends Selectable> selectableChildren() {
      return ImmutableList.copyOf(this.selectableChildren);
    }

    @Override
    public void forEachElement(Consumer<Widget> consumer) {
      this.children().forEach((child) -> {
        if (child instanceof Widget widget) {
          consumer.accept(widget);
        }
      });
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
    public void setDragging(boolean dragging) {
      this.dragging = dragging;
    }

    @Override
    public boolean isDragging() {
      return this.dragging;
    }

    protected void appendNarrations(NarrationMessageBuilder builder) {
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
      return new ScreenRect(this.getLeft(), this.getTop(), this.getWidth(), this.getHeight());
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

    public void setBgFadeWidth(int bgFadeWidth) {
      this.bgFadeWidth = bgFadeWidth;
    }

    @Override
    public int getHeight() {
      return this.contentHeight + this.getMarginTop() + this.getMarginBottom();
    }

    @Override
    public final void setHeight(int height) {
      RoundaLib.LOGGER.error(
          String.format("Cannot change height on %s. Reinitialize instead.", this.getClass().getCanonicalName()));
    }

    public void setMarginLeft(int margin) {
      this.marginLeft = margin;
    }

    public int getMarginLeft() {
      return this.marginLeft;
    }

    public void setMarginRight(int margin) {
      this.marginRight = margin;
    }

    public int getMarginRight() {
      return this.marginRight;
    }

    public void setMarginTop(int margin) {
      this.marginTop = margin;
    }

    public int getMarginTop() {
      return this.marginTop;
    }

    public void setMarginBottom(int margin) {
      this.marginBottom = margin;
    }

    public int getMarginBottom() {
      return this.marginBottom;
    }

    public void setMargin(int top, int right, int bottom, int left) {
      this.setMarginTop(top);
      this.setMarginRight(right);
      this.setMarginBottom(bottom);
      this.setMarginLeft(left);
    }

    public void setMargin(int top, int horizontal, int bottom) {
      this.setMargin(top, horizontal, bottom, horizontal);
    }

    public void setMargin(int vertical, int horizontal) {
      this.setMargin(vertical, horizontal, vertical, horizontal);
    }

    public void setMarginVertical(int margin) {
      this.setMarginTop(margin);
      this.setMarginBottom(margin);
    }

    public void setMarginHorizontal(int margin) {
      this.setMarginLeft(margin);
      this.setMarginRight(margin);
    }

    public int getContentLeft() {
      return this.getLeft() + this.getMarginLeft();
    }

    public int getContentRight() {
      return this.getRight() - this.getMarginRight();
    }

    public int getContentWidth() {
      return this.getWidth() - this.getMarginLeft() - this.getMarginRight();
    }

    public int getContentTop() {
      return this.getTop() + this.getMarginTop();
    }

    public int getContentBottom() {
      return this.getBottom() - this.getMarginBottom();
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
  }
}
