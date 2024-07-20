package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.linear.AutoLinearLayoutCellConfigurator;
import me.roundaround.roundalib.client.gui.layout.linear.LinearLayoutCellConfigurator;
import me.roundaround.roundalib.client.gui.util.Alignment;
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
public class LabelWidget extends DrawableWidget
    implements AutoLinearLayoutCellConfigurator<LabelWidget> {
  public static final Spacing DEFAULT_PADDING = Spacing.of(1, 2);

  private final TextRenderer textRenderer;
  private final PositionMode positionMode;

  private List<Text> lines;
  private int color;
  private Alignment alignmentX;
  private Alignment alignmentY;
  private Spacing padding;
  private OverflowBehavior overflowBehavior;
  private int scrollMargin;
  private int maxLines;
  private int lineSpacing;
  private boolean showBackground;
  private Spacing bgOverflow;
  private int bgColor;
  private boolean shadow;
  private Duration tooltipDelay;
  private IntRect textBounds = IntRect.zero();
  private IntRect bgBounds = IntRect.zero();
  private int referenceX;
  private int referenceY;
  private boolean inBatchUpdate = false;

  private LabelWidget(
      TextRenderer textRenderer,
      List<Text> lines,
      int color,
      PositionMode positionMode,
      int x,
      int y,
      int width,
      int height,
      Alignment alignmentX,
      Alignment alignmentY,
      Spacing padding,
      OverflowBehavior overflowBehavior,
      int scrollMargin,
      int maxLines,
      int lineSpacing,
      boolean showBackground,
      int bgColor,
      Spacing bgOverflow,
      boolean shadow,
      Tooltip tooltip,
      Duration tooltipDelay) {
    super(x, y, width, height, ScreenTexts.EMPTY);

    this.textRenderer = textRenderer;
    this.lines = new ArrayList<>(lines);
    this.color = color;
    this.positionMode = positionMode;
    this.alignmentX = alignmentX;
    this.alignmentY = alignmentY;
    this.padding = padding;
    this.overflowBehavior = overflowBehavior;
    this.scrollMargin = scrollMargin;
    this.maxLines = maxLines;
    this.lineSpacing = lineSpacing;
    this.showBackground = showBackground;
    this.bgColor = bgColor;
    this.bgOverflow = bgOverflow;
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

    this.referenceX = this.getReferenceX();
    this.referenceY = this.getReferenceY();
    super.setX(this.getAbsoluteX());
    super.setY(this.getAbsoluteY());

    if (this.lines.isEmpty()) {
      this.textBounds = IntRect.zero();
      this.bgBounds = IntRect.zero();
      return;
    }

    int lineCount = this.lines.size();
    int textWidth = this.lines.stream().mapToInt(this.textRenderer::getWidth).max().orElse(0);
    int textHeight = lineCount * this.textRenderer.fontHeight + (lineCount - 1) * this.lineSpacing;

    if (lineCount == 1 && this.overflowBehavior == OverflowBehavior.WRAP) {
      textHeight = GuiUtil.measureWrappedTextHeight(this.textRenderer,
          this.lines.getFirst(),
          this.getAvailableWidth(),
          this.maxLines,
          this.lineSpacing);
    }

    textWidth = Math.min(textWidth, this.getAvailableWidth());
    textHeight = Math.min(textHeight, this.getAvailableHeight());

    this.textBounds = IntRect.byDimensions(this.alignmentX.getPos(this.referenceX, textWidth),
        this.alignmentY.getPos(this.referenceY, textHeight),
        textWidth,
        textHeight);
    this.bgBounds = this.textBounds.expand(this.padding).expand(this.bgOverflow);
  }

  @Override
  public void onAddToLinearLayout(LinearLayoutCellConfigurator<LabelWidget> configurator) {
    configurator.margin(this.padding.expand(this.bgOverflow));
  }

  @Override
  public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    if (this.lines.isEmpty()) {
      return;
    }

    IntRect hoveredBounds = this.showBackground ? this.bgBounds : this.textBounds;
    this.hovered =
        context.scissorContains(mouseX, mouseY) && hoveredBounds.contains(mouseX, mouseY);

    if (this.showBackground) {
      context.fill(this.bgBounds.left(),
          this.bgBounds.top(),
          this.bgBounds.right(),
          this.bgBounds.bottom(),
          this.bgColor);
    }

    OverflowBehavior overflowBehavior = this.overflowBehavior;
    if (this.lines.size() > 1 && !overflowBehavior.supportsMultiline()) {
      overflowBehavior = OverflowBehavior.SHOW;
    }

    if (overflowBehavior == OverflowBehavior.CLIP) {
      context.enableScissor(this.textBounds.left(),
          this.textBounds.top(),
          this.textBounds.right(),
          this.textBounds.bottom());
    }

    int x = this.referenceX;
    int y = this.textBounds.top();
    int availableWidth = this.getAvailableWidth();

    for (Text line : this.lines) {
      this.renderLine(line, x, y, availableWidth, context, mouseX, mouseY, delta, overflowBehavior);
      y += this.textRenderer.fontHeight + this.lineSpacing;
    }

    if (overflowBehavior == OverflowBehavior.CLIP) {
      context.disableScissor();
    }
  }

  @Override
  public ScreenRect getNavigationFocus() {
    IntRect boundsRect = this.showBackground ? this.bgBounds : this.textBounds;
    return boundsRect.toScreenRect();
  }

  protected void renderLine(
      Text line,
      int x,
      int y,
      int availableWidth,
      DrawContext context,
      int mouseX,
      int mouseY,
      float delta,
      OverflowBehavior overflowBehavior) {
    switch (overflowBehavior) {
      case SHOW, CLIP -> GuiUtil.drawText(context,
          this.textRenderer,
          line,
          x,
          y,
          this.color,
          this.shadow,
          this.alignmentX);
      case TRUNCATE -> GuiUtil.drawTruncatedText(context,
          this.textRenderer,
          line,
          x,
          y,
          this.color,
          this.shadow,
          availableWidth,
          this.alignmentX);
      case WRAP -> GuiUtil.drawWrappedText(context,
          this.textRenderer,
          line,
          x,
          y,
          this.color,
          this.shadow,
          availableWidth,
          this.maxLines,
          this.lineSpacing,
          this.alignmentX);
      case SCROLL -> GuiUtil.drawScrollingText(context,
          this.textRenderer,
          line,
          x,
          y,
          this.color,
          this.shadow,
          availableWidth,
          this.scrollMargin,
          this.alignmentX);
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
        this.padding.getVertical();
  }

  public void batchUpdates(Runnable runnable) {
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

  public void setAlignmentX(Alignment alignmentX) {
    this.alignmentX = alignmentX;
    this.calculateBounds();
  }

  public void setAlignmentY(Alignment alignmentY) {
    this.alignmentY = alignmentY;
    this.calculateBounds();
  }

  public void setPadding(Spacing padding) {
    this.padding = padding;
    this.calculateBounds();
  }

  public void setOverflowBehavior(OverflowBehavior overflowBehavior) {
    this.overflowBehavior = overflowBehavior;
    this.calculateBounds();
  }

  public void setScrollMargin(int scrollMargin) {
    this.scrollMargin = scrollMargin;
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
  }

  public void setBgColor(int bgColor) {
    this.bgColor = bgColor;
  }

  public void setBgOverflow(Spacing bgOverflow) {
    this.bgOverflow = bgOverflow;
    this.calculateBounds();
  }

  public void setShadow(boolean shadow) {
    this.shadow = shadow;
  }

  @Override
  public void setX(int x) {
    switch (this.positionMode) {
      case ABSOLUTE -> super.setX(x);
      case REFERENCE -> this.referenceX = x;
    }
    this.calculateBounds();
  }

  @Override
  public void setY(int y) {
    switch (this.positionMode) {
      case ABSOLUTE -> super.setY(y);
      case REFERENCE -> this.referenceY = y;
    }
    this.calculateBounds();
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
    super.setDimensions(width, height);
    this.calculateBounds();
  }

  @Override
  public void setTooltipDelay(Duration tooltipDelay) {
    this.tooltipDelay = tooltipDelay;
    super.setTooltipDelay(tooltipDelay);
  }

  @Override
  public int getWidth() {
    if (this.width != 0) {
      return this.width;
    }
    return this.textBounds.getWidth();
  }

  @Override
  public int getHeight() {
    if (this.height != 0) {
      return this.height;
    }
    return this.textBounds.getHeight();
  }

  public IntRect getTextBounds() {
    return this.textBounds;
  }

  public IntRect getBgBounds() {
    return this.bgBounds;
  }

  protected int getAvailableWidth() {
    if (this.overflowBehavior == OverflowBehavior.SHOW) {
      return Integer.MAX_VALUE;
    }
    return this.getWidth() - this.padding.getHorizontal();
  }

  protected int getAvailableHeight() {
    if (this.overflowBehavior == OverflowBehavior.SHOW) {
      return Integer.MAX_VALUE;
    }
    return this.getHeight() - this.padding.getVertical();
  }

  protected int getRefX() {
    return switch (this.alignmentX) {
      case START -> this.getX();
      case CENTER -> this.getX() + this.getWidth() / 2;
      case END -> this.getX() + this.getWidth();
    };
  }

  protected int getRefY() {
    return switch (this.alignmentY) {
      case START -> this.getY();
      case CENTER -> this.getY() + this.getHeight() / 2;
      case END -> this.getY() + this.getHeight();
    };
  }

  protected int getReferenceX() {
    if (this.positionMode == PositionMode.REFERENCE) {
      return this.referenceX;
    }
    return switch (this.alignmentX) {
      case START -> this.getX();
      case CENTER -> this.getX() + this.getWidth() / 2;
      case END -> this.getX() + this.getWidth();
    };
  }

  protected int getReferenceY() {
    if (this.positionMode == PositionMode.REFERENCE) {
      return this.referenceY;
    }
    return switch (this.alignmentY) {
      case START -> this.getY();
      case CENTER -> this.getY() + this.getHeight() / 2;
      case END -> this.getY() + this.getHeight();
    };
  }

  protected int getAbsoluteX() {
    if (this.positionMode == PositionMode.ABSOLUTE) {
      return this.getX();
    }
    return switch (this.alignmentX) {
      case START -> this.referenceX;
      case CENTER -> this.referenceX - this.getWidth() / 2;
      case END -> this.referenceX - this.getWidth();
    };
  }

  protected int getAbsoluteY() {
    if (this.positionMode == PositionMode.ABSOLUTE) {
      return this.getY();
    }
    return switch (this.alignmentY) {
      case START -> this.referenceY;
      case CENTER -> this.referenceY - this.getHeight() / 2;
      case END -> this.referenceY - this.getHeight();
    };
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
    return getDefaultHeight(textRenderer, lines, DEFAULT_PADDING.getVertical(), 0);
  }

  public static int getDefaultHeight(
      TextRenderer textRenderer, int lines, int padding, int spacing) {
    return lines * textRenderer.fontHeight + (lines - 1) * spacing + padding;
  }

  @Environment(EnvType.CLIENT)
  public static class Builder {
    private final TextRenderer textRenderer;
    private final List<Text> lines;

    private PositionMode positionMode = PositionMode.ABSOLUTE;
    private int x;
    private int y;
    private int width;
    private int height;
    private int color = GuiUtil.LABEL_COLOR;
    private Alignment alignmentX = Alignment.START;
    private Alignment alignmentY = Alignment.CENTER;
    private Spacing padding = DEFAULT_PADDING;
    private OverflowBehavior overflowBehavior = OverflowBehavior.SHOW;
    private int scrollMargin = 2;
    private int maxLines = 0;
    private int lineSpacing = 0;
    private boolean background = true;
    private int bgColor = GuiUtil.BACKGROUND_COLOR;
    private Spacing bgOverflow = Spacing.of(1, 0, 0, 1);
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

    public Builder positionMode(PositionMode positionMode) {
      this.positionMode = positionMode;
      return this;
    }

    public Builder refPosition(int refX, int refY) {
      this.positionMode = PositionMode.REFERENCE;
      this.x = refX;
      this.y = refY;
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

    public Builder scrollMargin(int scrollMargin) {
      this.scrollMargin = scrollMargin;
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

    public Builder alignX(Alignment alignmentX) {
      this.alignmentX = alignmentX;
      return this;
    }

    public Builder alignLeft() {
      return this.alignX(Alignment.START);
    }

    public Builder alignCenterX() {
      return this.alignX(Alignment.CENTER);
    }

    public Builder alignRight() {
      return this.alignX(Alignment.END);
    }

    public Builder alignY(Alignment alignmentY) {
      this.alignmentY = alignmentY;
      return this;
    }

    public Builder alignTop() {
      return this.alignY(Alignment.START);
    }

    public Builder alignCenterY() {
      return this.alignY(Alignment.CENTER);
    }

    public Builder alignBottom() {
      return this.alignY(Alignment.END);
    }

    public Builder padding(int space) {
      this.padding = Spacing.of(space);
      return this;
    }

    public Builder padding(int vertical, int horizontal) {
      this.padding = Spacing.of(vertical, horizontal);
      return this;
    }

    public Builder padding(Spacing padding) {
      this.padding = padding;
      return this;
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

    public Builder bgOverflow(int space) {
      this.bgOverflow = Spacing.of(space);
      return this;
    }

    public Builder bgOverflow(int vertical, int horizontal) {
      this.bgOverflow = Spacing.of(vertical, horizontal);
      return this;
    }

    public Builder bgOverflow(Spacing bgOverflow) {
      this.bgOverflow = bgOverflow;
      return this;
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
          this.positionMode,
          this.x,
          this.y,
          this.width,
          this.height,
          this.alignmentX,
          this.alignmentY,
          this.padding,
          this.overflowBehavior,
          this.scrollMargin,
          this.maxLines,
          this.lineSpacing,
          this.background,
          this.bgColor,
          this.bgOverflow,
          this.shadow,
          this.tooltip,
          this.tooltipDelay);
    }
  }

  @Environment(EnvType.CLIENT)
  public enum PositionMode {
    REFERENCE,
    ABSOLUTE
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
