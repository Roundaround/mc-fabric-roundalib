package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.RoundaLib;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.util.Alignment;
import me.roundaround.roundalib.client.gui.util.Dimensions;
import me.roundaround.roundalib.client.gui.util.IntRect;
import me.roundaround.roundalib.client.gui.util.Spacing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
public class LabelWidget extends DrawableWidget {
  public static final Spacing PADDING = Spacing.of(2, 2, 1, 3);

  private final TextRenderer textRenderer;

  private List<Text> lines;
  private int color;
  private Alignment alignSelfX;
  private Alignment alignSelfY;
  private Alignment alignTextX;
  private Alignment alignTextY;
  private OverflowBehavior overflowBehavior;
  private int maxLines;
  private int lineSpacing;
  private boolean showBackground;
  private int bgColor;
  private boolean shadow;
  private IntRect textBounds = IntRect.zero();
  private IntRect bgBounds = IntRect.zero();
  private boolean inBatchUpdate = false;

  private LabelWidget(
      TextRenderer textRenderer,
      List<Text> lines,
      int color,
      int x,
      int y,
      int width,
      int height,
      Alignment alignSelfX,
      Alignment alignSelfY,
      Alignment alignTextX,
      Alignment alignTextY,
      OverflowBehavior overflowBehavior,
      int maxLines,
      int lineSpacing,
      boolean showBackground,
      int bgColor,
      boolean shadow,
      Tooltip tooltip,
      Duration tooltipDelay) {
    super(x, y, width, height, ScreenTexts.EMPTY);

    this.textRenderer = textRenderer;
    this.lines = new ArrayList<>(lines);
    this.color = color;
    this.alignSelfX = alignSelfX;
    this.alignSelfY = alignSelfY;
    this.alignTextX = alignTextX;
    this.alignTextY = alignTextY;
    this.overflowBehavior = overflowBehavior;
    this.maxLines = maxLines;
    this.lineSpacing = lineSpacing;
    this.showBackground = showBackground;
    this.bgColor = bgColor;
    this.shadow = shadow;

    this.setTooltip(tooltip);
    if (tooltipDelay != null) {
      this.setTooltipDelay(tooltipDelay);
    }

    this.calculateBounds();
  }

  public void calculateBounds() {
    if (this.inBatchUpdate) {
      return;
    }

    int padX = this.showBackground ? PADDING.left() : 0;
    int padY = this.showBackground ? PADDING.top() : 0;

    int x = this.getX() + padX;
    int y = this.getY() + padY;

    if (this.lines.isEmpty()) {
      this.textBounds = IntRect.byDimensions(x, y, 0, 0);
      this.bgBounds = this.textBounds.expand(PADDING);
      return;
    }

    Dimensions dimensions = this.lines.size() == 1 && this.overflowBehavior == OverflowBehavior.WRAP
        ? this.getWrappedTextDimensions()
        : this.getFullTextDimensions();

    int textWidth = Math.min(dimensions.width(), this.getAvailableWidth());
    int textHeight = Math.min(dimensions.height(), this.getAvailableHeight());

    if (this.width != 0) {
      int padding = this.showBackground ? PADDING.getHorizontal() : 0;
      x = this.alignTextX.getPosInContainer(x, this.width - padding, textWidth);
    }
    if (this.height != 0) {
      int padding = this.showBackground ? PADDING.getVertical() : 0;
      y = this.alignTextY.getPosInContainer(y, this.height - padding, textHeight);
    }

    this.textBounds = IntRect.byDimensions(x, y, textWidth, textHeight);
    this.bgBounds = this.textBounds.expand(PADDING);
  }

  private Dimensions getFullTextDimensions() {
    return Dimensions.of(this.lines.stream().mapToInt(this.textRenderer::getWidth).max().orElse(0),
        this.lines.size() * this.textRenderer.fontHeight +
            (this.lines.size() - 1) * this.lineSpacing);
  }

  private Dimensions getWrappedTextDimensions() {
    return GuiUtil.measureWrappedText(this.textRenderer,
        this.lines.getFirst(),
        this.getAvailableWidth(),
        this.maxLines,
        this.lineSpacing);
  }

  @Override
  public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    if (this.lines.isEmpty()) {
      return;
    }

    this.hovered =
        context.scissorContains(mouseX, mouseY) && this.getBounds().contains(mouseX, mouseY);

    if (this.showBackground) {
      GuiUtil.fill(context, this.bgBounds, this.bgColor);
    }

    OverflowBehavior overflowBehavior = this.overflowBehavior;
    if (this.lines.size() > 1 && !overflowBehavior.supportsMultiline()) {
      overflowBehavior = OverflowBehavior.CLIP;
    }

    if (overflowBehavior == OverflowBehavior.CLIP) {
      GuiUtil.enableScissor(context, this.textBounds);
    }

    for (int index = 0; index < this.lines.size(); index++) {
      this.renderLine(index, overflowBehavior, context, mouseX, mouseY, delta);
    }

    if (overflowBehavior == OverflowBehavior.CLIP) {
      GuiUtil.disableScissor(context);
    }
  }

  protected void renderLine(
      int index,
      OverflowBehavior overflowBehavior,
      DrawContext context,
      int mouseX,
      int mouseY,
      float delta) {
    Text line = this.lines.get(index);
    int x = this.textBounds.left();
    int y =
        this.textBounds.top() + GuiUtil.getLineYOffset(this.textRenderer, index, this.lineSpacing);
    int viewportWidth = this.textBounds.getWidth();

    switch (overflowBehavior) {
      case SHOW, CLIP -> GuiUtil.drawText(context,
          this.textRenderer,
          line,
          x,
          y,
          this.color,
          this.shadow,
          viewportWidth,
          this.alignTextX);
      case TRUNCATE -> GuiUtil.drawTruncatedText(context,
          this.textRenderer,
          line,
          x,
          y,
          this.color,
          this.shadow,
          viewportWidth,
          this.alignTextX);
      case WRAP -> GuiUtil.drawWrappedText(context,
          this.textRenderer,
          line,
          x,
          y,
          this.color,
          this.shadow,
          viewportWidth,
          this.maxLines,
          this.lineSpacing,
          this.alignTextX);
      case SCROLL -> GuiUtil.drawScrollingText(context,
          this.textRenderer,
          line,
          x,
          y,
          this.color,
          this.shadow,
          viewportWidth,
          this.alignTextX);
    }
  }

  public Text getText() {
    if (this.lines.isEmpty()) {
      return Text.empty();
    }

    Iterator<Text> iter = this.lines.iterator();
    MutableText builder = iter.next().copy();
    while (iter.hasNext()) {
      builder.append(ScreenTexts.LINE_BREAK);
      builder.append(iter.next());
    }

    return builder;
  }

  public List<Text> getLines() {
    return this.lines;
  }

  public int getLineCount() {
    return this.lines.size();
  }

  public int getDefaultHeight() {
    int lines = Math.max(1, this.getLineCount());
    return lines * this.textRenderer.fontHeight + (lines - 1) * this.lineSpacing +
        PADDING.getVertical();
  }

  public void batchUpdates(Runnable runnable) {
    if (this.inBatchUpdate) {
      runnable.run();
      return;
    }

    this.inBatchUpdate = true;
    try {
      runnable.run();
    } finally {
      this.inBatchUpdate = false;
      this.calculateBounds();
    }
  }

  public void setText(Text text) {
    this.lines = List.of(text);
    this.calculateBounds();
  }

  public void setText(List<Text> lines) {
    this.lines = new ArrayList<>(lines);
    this.calculateBounds();
  }

  public void appendLine(Text text) {
    this.lines.add(text);
    this.calculateBounds();
  }

  public void setColor(int color) {
    this.color = color;
  }

  public void setAlignSelfX(Alignment alignSelfX) {
    if (this.lines.size() == 3) {
      RoundaLib.LOGGER.info("Setting align self X to {}", alignSelfX.name());
    }
    this.alignSelfX = alignSelfX;
    this.calculateBounds();
  }

  public void setAlignSelfY(Alignment alignSelfY) {
    this.alignSelfY = alignSelfY;
    this.calculateBounds();
  }

  public void setAlignTextX(Alignment alignTextX) {
    this.alignTextX = alignTextX;
    this.calculateBounds();
  }

  public void setAlignTextY(Alignment alignTextY) {
    this.alignTextY = alignTextY;
    this.calculateBounds();
  }

  public void setOverflowBehavior(OverflowBehavior overflowBehavior) {
    this.overflowBehavior = overflowBehavior;
    this.calculateBounds();
  }

  public void setMaxLines(int maxLines) {
    this.maxLines = maxLines;
    this.calculateBounds();
  }

  public void setLineSpacing(int lineSpacing) {
    this.lineSpacing = lineSpacing;
    this.calculateBounds();
  }

  public void setShowBackground(boolean showBackground) {
    this.showBackground = showBackground;
    this.calculateBounds();
  }

  public void setBgColor(int bgColor) {
    this.bgColor = bgColor;
  }

  public void setShadow(boolean shadow) {
    this.shadow = shadow;
  }

  @Override
  public void setX(int x) {
    super.setX(x);
    this.calculateBounds();
  }

  @Override
  public void setY(int y) {
    super.setY(y);
    this.calculateBounds();
  }

  @Override
  public void setPosition(int x, int y) {
    this.batchUpdates(() -> {
      super.setPosition(x, y);
    });
  }

  @Override
  public void setWidth(int width) {
    super.setWidth(width);
    this.calculateBounds();
  }

  @Override
  public void setHeight(int height) {
    super.setHeight(height);
    this.calculateBounds();
  }

  @Override
  public void setDimensions(int width, int height) {
    this.batchUpdates(() -> {
      super.setDimensions(width, height);
    });
  }

  @Override
  public void setDimensionsAndPosition(int width, int height, int x, int y) {
    this.batchUpdates(() -> {
      super.setDimensionsAndPosition(width, height, x, y);
    });
  }

  @Override
  public int getX() {
    return this.alignSelfX.getPos(super.getX(), this.getWidth());
  }

  @Override
  public int getY() {
    return this.alignSelfY.getPos(super.getY(), this.getHeight());
  }

  @Override
  public int getWidth() {
    return this.width != 0 ? this.width : this.getBounds().getWidth();
  }

  @Override
  public int getHeight() {
    return this.height != 0 ? this.height : this.getBounds().getHeight();
  }

  @Override
  public ScreenRect getNavigationFocus() {
    return this.getBounds().toScreenRect();
  }

  public IntRect getBounds() {
    return this.showBackground ? this.bgBounds : this.textBounds;
  }

  public IntRect getWidgetBounds() {
    return IntRect.byDimensions(this.getX(), this.getY(), this.getWidth(), this.getHeight());
  }

  public int getTextWidth() {
    return this.getBounds().getWidth();
  }

  public int getTextHeight() {
    return this.getBounds().getHeight();
  }

  protected int getAvailableWidth() {
    if (this.width == 0 || this.overflowBehavior == OverflowBehavior.SHOW) {
      return Integer.MAX_VALUE;
    }
    return Math.max(0, this.showBackground ? this.width - PADDING.getHorizontal() : this.width);
  }

  protected int getAvailableHeight() {
    if (this.height == 0 || this.overflowBehavior == OverflowBehavior.SHOW) {
      return Integer.MAX_VALUE;
    }
    return Math.max(0, this.showBackground ? this.height - PADDING.getVertical() : this.height);
  }

  public static Builder builder(TextRenderer textRenderer, Text text) {
    return new Builder(textRenderer, text);
  }

  public static Builder builder(TextRenderer textRenderer, List<Text> lines) {
    return new Builder(textRenderer, lines);
  }

  public static int getDefaultHeight(TextRenderer textRenderer) {
    return getDefaultHeight(textRenderer, 1);
  }

  public static int getDefaultHeight(TextRenderer textRenderer, int lines) {
    return getDefaultHeight(textRenderer, lines, 0);
  }

  public static int getDefaultHeight(
      TextRenderer textRenderer, int lines, int spacing) {
    return lines * textRenderer.fontHeight + (lines - 1) * spacing + PADDING.getVertical();
  }

  @Environment(EnvType.CLIENT)
  public static class Builder {
    private final TextRenderer textRenderer;
    private final List<Text> lines;

    private int x;
    private int y;
    private int width;
    private int height;
    private int color = GuiUtil.LABEL_COLOR;
    private Alignment alignSelfX = Alignment.START;
    private Alignment alignSelfY = Alignment.START;
    private Alignment alignTextX = Alignment.START;
    private Alignment alignTextY = Alignment.CENTER;
    private OverflowBehavior overflowBehavior = OverflowBehavior.SHOW;
    private int maxLines = 0;
    private int lineSpacing = 0;
    private boolean background = true;
    private int bgColor = GuiUtil.BACKGROUND_COLOR;
    private boolean shadow = false;
    private Tooltip tooltip = null;
    private Duration tooltipDelay = null;

    public Builder(TextRenderer textRenderer) {
      this(textRenderer, List.of());
    }

    public Builder(TextRenderer textRenderer, Text text) {
      this(textRenderer, List.of(text));
    }

    public Builder(TextRenderer textRenderer, List<Text> lines) {
      this.textRenderer = textRenderer;
      this.lines = new ArrayList<>(lines);
    }

    public Builder position(int x, int y) {
      this.x = x;
      this.y = y;
      return this;
    }

    public Builder width(int width) {
      this.width = width;
      return this;
    }

    public Builder height(int height) {
      this.height = height;
      return this;
    }

    public Builder dimensions(int width, int height) {
      this.width = width;
      this.height = height;
      return this;
    }

    public Builder color(int color) {
      this.color = color;
      return this;
    }

    public Builder color(int r, int g, int b) {
      return this.color(GuiUtil.genColorInt(r, g, b));
    }

    public Builder color(int r, int g, int b, int a) {
      return this.color(GuiUtil.genColorInt(r, g, b, a));
    }

    public Builder overflowBehavior(OverflowBehavior overflowBehavior) {
      this.overflowBehavior = overflowBehavior;
      return this;
    }

    public Builder maxLines(int maxLines) {
      this.maxLines = maxLines;
      return this;
    }

    public Builder lineSpacing(int lineSpacing) {
      this.lineSpacing = lineSpacing;
      return this;
    }

    public Builder alignSelfX(Alignment alignmentX) {
      this.alignSelfX = alignmentX;
      return this;
    }

    public Builder alignSelfLeft() {
      return this.alignSelfX(Alignment.START);
    }

    public Builder alignSelfCenterX() {
      return this.alignSelfX(Alignment.CENTER);
    }

    public Builder alignSelfRight() {
      return this.alignSelfX(Alignment.END);
    }

    public Builder alignSelfY(Alignment alignmentY) {
      this.alignSelfY = alignmentY;
      return this;
    }

    public Builder alignSelfTop() {
      return this.alignSelfY(Alignment.START);
    }

    public Builder alignSelfCenterY() {
      return this.alignSelfY(Alignment.CENTER);
    }

    public Builder alignSelfBottom() {
      return this.alignSelfY(Alignment.END);
    }

    public Builder alignTextX(Alignment alignmentX) {
      this.alignTextX = alignmentX;
      return this;
    }

    public Builder alignTextLeft() {
      return this.alignTextX(Alignment.START);
    }

    public Builder alignTextCenterX() {
      return this.alignTextX(Alignment.CENTER);
    }

    public Builder alignTextRight() {
      return this.alignTextX(Alignment.END);
    }

    public Builder alignTextY(Alignment alignmentY) {
      this.alignTextY = alignmentY;
      return this;
    }

    public Builder alignTextTop() {
      return this.alignTextY(Alignment.START);
    }

    public Builder alignTextCenterY() {
      return this.alignTextY(Alignment.CENTER);
    }

    public Builder alignTextBottom() {
      return this.alignTextY(Alignment.END);
    }

    public Builder hideBackground() {
      return this.background(false);
    }

    public Builder background(boolean background) {
      this.background = background;
      return this;
    }

    public Builder bgColor(int bgColor) {
      this.bgColor = bgColor;
      return this;
    }

    public Builder bgColor(int r, int g, int b) {
      return this.bgColor(GuiUtil.genColorInt(r, g, b));
    }

    public Builder bgColor(int r, int g, int b, int a) {
      return this.bgColor(GuiUtil.genColorInt(r, g, b, a));
    }

    public Builder showShadow() {
      return this.shadow(true);
    }

    public Builder shadow(boolean shadow) {
      this.shadow = shadow;
      return this;
    }

    public Builder tooltip(Text tooltip) {
      return this.tooltip(Tooltip.of(tooltip));
    }

    public Builder tooltip(Tooltip tooltip) {
      this.tooltip = tooltip;
      return this;
    }

    public Builder tooltipDelay(Duration tooltipDelay) {
      this.tooltipDelay = tooltipDelay;
      return this;
    }

    public LabelWidget build() {
      return new LabelWidget(this.textRenderer,
          this.lines,
          this.color,
          this.x,
          this.y,
          this.width,
          this.height,
          this.alignSelfX,
          this.alignSelfY,
          this.alignTextX,
          this.alignTextY,
          this.overflowBehavior,
          this.maxLines,
          this.lineSpacing,
          this.background,
          this.bgColor,
          this.shadow,
          this.tooltip,
          this.tooltipDelay);
    }
  }

  @Environment(EnvType.CLIENT)
  public enum OverflowBehavior {
    SHOW("show", true),
    TRUNCATE("truncate", true),
    WRAP("wrap", false),
    CLIP("clip", true),
    SCROLL("scroll", true);

    private final String id;
    private final boolean supportsMultiline;

    OverflowBehavior(String id, boolean supportsMultiline) {
      this.id = id;
      this.supportsMultiline = supportsMultiline;
    }

    public String getI18nKey(String modId) {
      return String.format("%s.roundalib.overflow_behavior.%s", modId, this.id);
    }

    public Text getDisplayText(String modId) {
      return Text.translatable(this.getI18nKey(modId));
    }

    public String getDisplayString(String modId) {
      return I18n.translate(this.getI18nKey(modId));
    }

    public boolean supportsMultiline() {
      return this.supportsMultiline;
    }
  }
}
