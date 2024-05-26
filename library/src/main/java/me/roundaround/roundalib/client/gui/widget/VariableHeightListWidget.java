package me.roundaround.roundalib.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.PositionalEntry;
import me.roundaround.roundalib.client.gui.PositionalLinkedList;
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
import net.minecraft.client.gui.widget.ContainerWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.render.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class VariableHeightListWidget<E extends VariableHeightListWidget.Entry<E>> extends ContainerWidget {
  private static final Identifier SCROLLER_TEXTURE = new Identifier("widget/scroller");
  private static final Identifier SCROLLER_BACKGROUND_TEXTURE = new Identifier("widget/scroller_background");
  private static final Identifier MENU_LIST_BACKGROUND_TEXTURE = new Identifier("textures/gui/menu_list_background.png");
  private static final Identifier INWORLD_MENU_LIST_BACKGROUND_TEXTURE = new Identifier("textures/gui/inworld_menu_list_background.png");

  protected final MinecraftClient client;

  protected E hoveredEntry;
  protected double scrollUnit;
  protected boolean autoCalculateScrollUnit = true;

  private final PositionalLinkedList<E> entries = new PositionalLinkedList<>();

  private int rowPadding;
  private int contentWidth;
  private int contentHeight;
  private double scrollAmount;
  private boolean scrolling;

  protected VariableHeightListWidget(MinecraftClient client, int x, int y, int width, int height) {
    this(client, x, y, width, height, GuiUtil.PADDING);
  }

  protected VariableHeightListWidget(MinecraftClient client, int x, int y, int width, int height, int rowPadding) {
    super(x, y, width, height, ScreenTexts.EMPTY);

    this.client = client;

    this.rowPadding = rowPadding;
    this.contentWidth = width;

    this.contentHeight = 0;
  }

  public <T extends E> T addEntry(T entry) {
    this.entries.add(entry);
    this.recalculateContentHeight();
    return entry;
  }

  public void clearEntries() {
    this.entries.clear();
    this.contentHeight = 0;
  }

  public void forEachEntry(Consumer<? super E> consumer) {
    this.entries.forEach(consumer);
  }

  @Override
  public void setWidth(int width) {
    super.setWidth(width);
    this.setContentWidth(this.contentWidth);
  }

  public void updatePosition(int x, int y, int width, int height) {
    this.updatePosition(x, y, width, height, this.contentWidth);
  }

  public void updatePosition(int x, int y, int width, int height, int contentWidth) {
    this.setPosition(x, y);
    this.setDimensions(width, height);
    this.setContentWidth(contentWidth);
  }

  public void setRowPadding(int rowPadding) {
    this.rowPadding = rowPadding;
    this.recalculateContentHeight();
  }

  public void setContentWidth(int contentWidth) {
    this.contentWidth = Math.min(contentWidth, this.getWidth());
  }

  public int getContentWidth() {
    return this.contentWidth;
  }

  public int getContentLeft() {
    return this.getX() + (this.getWidth() - this.getContentWidth()) / 2;
  }

  public int getContentRight() {
    return this.getX() + (this.getWidth() + this.getContentWidth()) / 2;
  }

  private void recalculateContentHeight() {
    this.contentHeight = this.entries.getTotalHeight() + (this.entries.size() * this.rowPadding);
  }

  @Override
  public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    this.hoveredEntry = this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;

    this.renderListBackground(drawContext);

    drawContext.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
    this.renderList(drawContext, mouseX, mouseY, delta);
    this.renderScrollBar(drawContext, mouseX, mouseY, delta);
    drawContext.disableScissor();

    this.renderListBorders(drawContext);
  }

  protected void renderListBackground(DrawContext drawContext) {
    RenderSystem.enableBlend();
    Identifier identifier = this.client.world == null ? MENU_LIST_BACKGROUND_TEXTURE : INWORLD_MENU_LIST_BACKGROUND_TEXTURE;
    drawContext.drawTexture(identifier, this.getX(), this.getY(), (float) this.getRight(), (float) (this.getBottom() + (int) this.getScrollAmount()), this.getWidth(), this.getHeight(), 32, 32);
    RenderSystem.disableBlend();
  }

  protected void renderList(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    for (int i = 0; i < this.entries.size(); i++) {
      this.renderEntry(drawContext, i, mouseX, mouseY, delta);
    }
  }

  protected void renderEntry(DrawContext drawContext, int index, int mouseX, int mouseY, float delta) {
    E entry = this.entries.get(index);
    double scrollAmount = this.getScrollAmount();

    double scrolledTop = entry.getTop() - scrollAmount;
    double scrolledBottom = entry.getTop() + entry.getHeight() - scrollAmount;

    if (scrolledBottom < this.getY() || scrolledTop > this.getBottom()) {
      return;
    }

    entry.render(drawContext, index, scrollAmount, mouseX, mouseY, delta);
  }

  protected void renderScrollBar(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    if (!this.isScrollbarVisible()) {
      return;
    }

    int scrollbarLeft = this.getScrollbarPositionX();

    int handleHeight = (int) ((float) this.height * this.height / this.contentHeight);
    handleHeight = MathHelper.clamp(handleHeight, 32, this.height - 8);

    int handleTop = (int) this.scrollAmount * (this.height - handleHeight) / this.getMaxScroll() + this.getY();
    if (handleTop < this.getY()) {
      handleTop = this.getY();
    }

    RenderSystem.enableBlend();
    drawContext.drawGuiTexture(SCROLLER_BACKGROUND_TEXTURE, scrollbarLeft, this.getY(), GuiUtil.SCROLLBAR_WIDTH, this.height);
    drawContext.drawGuiTexture(SCROLLER_TEXTURE, scrollbarLeft, handleTop, GuiUtil.SCROLLBAR_WIDTH, handleHeight);
    RenderSystem.disableBlend();
  }

  protected void renderListBorders(DrawContext drawContext) {
    Identifier headerSepTex = this.client.world == null ? Screen.HEADER_SEPARATOR_TEXTURE : Screen.INWORLD_HEADER_SEPARATOR_TEXTURE;
    Identifier footerSepTex = this.client.world == null ? Screen.FOOTER_SEPARATOR_TEXTURE : Screen.INWORLD_FOOTER_SEPARATOR_TEXTURE;
    RenderSystem.enableBlend();
    drawContext.drawTexture(headerSepTex, this.getX(), this.getY() - 2, 0.0F, 0.0F, this.getWidth(), 2, 32, 2);
    drawContext.drawTexture(footerSepTex, this.getX(), this.getBottom(), 0.0F, 0.0F, this.getWidth(), 2, 32, 2);
    RenderSystem.disableBlend();
  }

  @Override
  public ScreenRect getNavigationFocus() {
    return new ScreenRect(this.getX(), this.getY(), this.width, this.height);
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
        builder.put(NarrationPart.POSITION, Text.translatable("narrator.position.list", i + 1, list.size()));
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
  public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
      return true;
    }

    if (button == 0 && this.scrolling) {
      if (mouseY < this.getY()) {
        this.setScrollAmount(0);
      } else if (mouseY > this.getBottom()) {
        this.setScrollAmount(this.getMaxScroll());
      } else {
        double d = Math.max(1, this.getMaxScroll());
        int i = this.height;
        int j = MathHelper.clamp((int) ((float) (i * i) / this.entries.getTotalHeight()), 32, i - 8);
        double e = Math.max(1.0, d / (double) (i - j));
        this.setScrollAmount(this.getScrollAmount() + deltaY * e);
      }

      return true;
    }

    return false;
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    this.setScrollAmount(this.getScrollAmount() - verticalAmount * this.getScrollUnit());
    return true;
  }

  protected E getEntryAtPosition(double x, double y) {
    if (x < this.getEntriesLeft() || x > this.getEntriesRight()) {
      return null;
    }

    if (y < this.getY() || y > this.getBottom()) {
      return null;
    }

    return this.entries.getEntryAtPosition(y + this.scrollAmount);
  }

  protected void ensureVisible(E entry) {
    int scrolledTop = entry.getTop() - (int) this.scrollAmount;
    if (scrolledTop < this.getEntriesTop()) {
      this.scroll(scrolledTop - this.getEntriesTop());
      return;
    }

    int scrolledBottom = entry.getTop() + entry.getHeight() - (int) this.scrollAmount;
    if (scrolledBottom > this.getBottom()) {
      this.scroll(scrolledBottom - this.getEntriesBottom());
    }
  }

  protected int getEntriesLeft() {
    return this.getX();
  }

  protected int getEntriesTop() {
    return this.getY();
  }

  protected int getEntriesRight() {
    return this.getContentRight() - (this.isScrollbarVisible() ? GuiUtil.SCROLLBAR_WIDTH : 0);
  }

  protected int getEntriesBottom() {
    return this.getBottom();
  }

  protected boolean isScrollbarVisible() {
    return this.getMaxScroll() > 0;
  }

  protected int getScrollbarPositionX() {
    return this.getContentRight() - GuiUtil.SCROLLBAR_WIDTH;
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
    return Math.max(0, this.contentHeight - this.getHeight());
  }

  protected void updateScrollingState(double mouseX, double mouseY, int button) {
    this.scrolling = button == 0 && mouseX >= (double) this.getScrollbarPositionX() && mouseX < (this.getScrollbarPositionX() + GuiUtil.SCROLLBAR_WIDTH);
  }

  protected double getScrollUnit() {
    return this.autoCalculateScrollUnit ? this.entries.getAverageItemHeight() / 2.0 : this.scrollUnit;
  }

  public abstract static class Entry<E extends Entry<E>> extends AbstractParentElement implements PositionalEntry {
    protected static final int ROW_SHADE_STRENGTH = 85;
    protected static final int ROW_SHADE_FADE_WIDTH = 10;
    protected static final int ROW_SHADE_FADE_OVERFLOW = 10;

    protected final MinecraftClient client;
    protected final VariableHeightListWidget<E> parent;

    private final int index;
    private final int height;
    private final int top;

    private Element focused;
    private Selectable focusedSelectable;

    public Entry(MinecraftClient client, VariableHeightListWidget<E> parent, int index, int height) {
      this.client = client;
      this.parent = parent;
      this.index = index;
      this.height = height;
      this.top = parent.getY() + parent.entries.getTotalHeight();
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

    @Override
    public int getTop() {
      return this.top + this.index * this.parent.rowPadding;
    }

    @Override
    public int getHeight() {
      return this.height;
    }

    public int getLeft() {
      return this.parent.getEntriesLeft();
    }

    public int getRight() {
      return this.parent.getEntriesRight();
    }

    public void render(DrawContext drawContext, int index, double scrollAmount, int mouseX, int mouseY, float delta) {
      this.renderBackground(drawContext, index, scrollAmount, mouseX, mouseY, delta);
      this.renderContent(drawContext, index, scrollAmount, mouseX, mouseY, delta);
      this.renderDecorations(drawContext, index, scrollAmount, mouseX, mouseY, delta);
    }

    public void renderBackground(DrawContext drawContext, int index, double scrollAmount, int mouseX, int mouseY, float delta) {
      if (index % 2 == 0) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();

        int left = this.getLeft() - ROW_SHADE_FADE_OVERFLOW;
        int right = this.getRight() + ROW_SHADE_FADE_OVERFLOW;
        int top = this.top - (int) scrollAmount - this.parent.rowPadding / 2;
        int bottom = this.top + this.height - (int) scrollAmount + this.parent.rowPadding / 2 + 1;

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix4f, left + ROW_SHADE_FADE_WIDTH, top, 0).color(0, 0, 0, ROW_SHADE_STRENGTH).next();
        bufferBuilder.vertex(matrix4f, left, top, 0).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(matrix4f, left, bottom, 0).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(matrix4f, left + ROW_SHADE_FADE_WIDTH, bottom, 0).color(0, 0, 0, ROW_SHADE_STRENGTH).next();

        bufferBuilder.vertex(matrix4f, right - ROW_SHADE_FADE_WIDTH, top, 0).color(0, 0, 0, ROW_SHADE_STRENGTH).next();
        bufferBuilder.vertex(matrix4f, left + ROW_SHADE_FADE_WIDTH, top, 0).color(0, 0, 0, ROW_SHADE_STRENGTH).next();
        bufferBuilder.vertex(matrix4f, left + ROW_SHADE_FADE_WIDTH, bottom, 0).color(0, 0, 0, ROW_SHADE_STRENGTH).next();
        bufferBuilder.vertex(matrix4f, right - ROW_SHADE_FADE_WIDTH, bottom, 0).color(0, 0, 0, ROW_SHADE_STRENGTH).next();

        bufferBuilder.vertex(matrix4f, right, top, 0).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(matrix4f, right - ROW_SHADE_FADE_WIDTH, top, 0).color(0, 0, 0, ROW_SHADE_STRENGTH).next();
        bufferBuilder.vertex(matrix4f, right - ROW_SHADE_FADE_WIDTH, bottom, 0).color(0, 0, 0, ROW_SHADE_STRENGTH).next();
        bufferBuilder.vertex(matrix4f, right, bottom, 0).color(0, 0, 0, 0).next();
        tessellator.draw();

        RenderSystem.disableBlend();
      }
    }

    public void renderContent(DrawContext drawContext, int index, double scrollAmount, int mouseX, int mouseY, float delta) {

    }

    public void renderDecorations(DrawContext drawContext, int index, double scrollAmount, int mouseX, int mouseY, float delta) {

    }

    public List<? extends Selectable> selectableChildren() {
      return this.children().stream().filter(Selectable.class::isInstance).map(Selectable.class::cast).toList();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
      if (!this.parent.isMouseOver(mouseX, mouseY)) {
        return false;
      }

      return mouseY >= this.getTop() && mouseY <= this.getBottom();
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

      int index = MathHelper.clamp(delta + this.children().indexOf(this.getFocused()), 0, this.children().size() - 1);

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
      Screen.SelectedElementNarrationData data = Screen.findSelectedElementData(list, this.focusedSelectable);

      if (data != null) {
        if (data.selectType.isFocused()) {
          this.focusedSelectable = data.selectable;
        }

        if (list.size() > 1) {
          builder.put(NarrationPart.POSITION, Text.translatable("narrator.position.object_list", data.index + 1, list.size()));
          if (data.selectType == Selectable.SelectionType.FOCUSED) {
            builder.put(NarrationPart.USAGE, Text.translatable("narration.component_list.usage"));
          }
        }

        data.selectable.appendNarrations(builder.nextMessage());
      }
    }
  }
}
