package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.IntRect;
import me.roundaround.roundalib.client.gui.layout.Spacing;
import me.roundaround.roundalib.client.gui.layout.TextAlignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class LabelWidget extends ClickableWidget {
  public static final Spacing DEFAULT_PADDING = Spacing.of(1, 2);

  private final TextRenderer textRenderer;
  private final PositionMode positionMode;
  private final TextAlignment alignmentX;
  private final TextAlignment alignmentY;
  private final Spacing padding;
  private final OverflowBehavior overflowBehavior;
  private final int scrollMargin;
  private final int maxLines;
  private final int lineSpacing;
  private final boolean background;
  private final Spacing bgOverflow;
  private final boolean shadow;

  private Text text;
  private int color;
  private int bgColor;
  private IntRect textBounds = IntRect.zero();
  private IntRect bgBounds = IntRect.zero();
  private int layoutX;
  private int layoutY;

  private LabelWidget(
      TextRenderer textRenderer,
      Text text,
      int color,
      PositionMode positionMode,
      int x,
      int y,
      int width,
      int height,
      TextAlignment alignmentX,
      TextAlignment alignmentY,
      Spacing padding,
      OverflowBehavior overflowBehavior,
      int scrollMargin,
      int maxLines,
      int lineSpacing,
      boolean background,
      int bgColor,
      Spacing bgOverflow,
      boolean shadow
  ) {
    super(x, y, width, height, text);

    this.textRenderer = textRenderer;
    this.text = text;
    this.color = color;
    this.positionMode = positionMode;
    this.alignmentX = alignmentX;
    this.alignmentY = alignmentY;
    this.padding = padding;
    this.overflowBehavior = overflowBehavior;
    this.scrollMargin = scrollMargin;
    this.maxLines = maxLines;
    this.lineSpacing = lineSpacing;
    this.background = background;
    this.bgColor = bgColor;
    this.bgOverflow = bgOverflow;
    this.shadow = shadow;

    this.setLayoutX(x);
    this.setLayoutY(y);
  }

  @Override
  protected void appendClickableNarrations(NarrationMessageBuilder builder) {
  }

  @Override
  public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    int x = this.getTextRefX();

    int textWidth = this.textRenderer.getWidth(this.text);
    int textHeight = this.textRenderer.fontHeight;

    if (this.overflowBehavior == OverflowBehavior.WRAP) {
      textHeight = GuiUtil.measureWrappedTextHeight(
          this.textRenderer, this.text, this.getAvailableWidth(), this.maxLines, this.lineSpacing);
    }

    textWidth = Math.min(textWidth, this.getAvailableWidth());
    textHeight = Math.min(textHeight, this.getAvailableHeight());

    IntRect textBounds = IntRect.byDimensions(this.alignmentX.getLeft(x, textWidth),
        this.alignmentY.getTop(this.getTextRefY(), textHeight), textWidth, textHeight
    );
    IntRect bgBounds = textBounds.expand(this.padding).expand(this.bgOverflow);

    if (this.background) {
      context.fill(bgBounds.left(), bgBounds.top(), bgBounds.right(), bgBounds.bottom(), this.bgColor);
    }

    int y = textBounds.top();
    int availableWidth = this.getAvailableWidth();

    switch (this.overflowBehavior) {
      case SHOW ->
          GuiUtil.drawText(context, this.textRenderer, this.text, x, y, this.color, this.shadow, this.alignmentX);
      case TRUNCATE -> GuiUtil.drawTruncatedText(context, this.textRenderer, this.text, x, y, this.color, this.shadow,
          availableWidth, this.alignmentX
      );
      case WRAP ->
          GuiUtil.drawWrappedText(context, this.textRenderer, this.text, x, y, this.color, this.shadow, availableWidth,
              this.maxLines, this.lineSpacing, this.alignmentX
          );
      case CLIP -> {
        context.enableScissor(textBounds.left(), textBounds.top(), textBounds.right(), textBounds.bottom());
        GuiUtil.drawText(context, this.textRenderer, this.text, x, y, this.color, this.shadow, this.alignmentX);
        context.disableScissor();
      }
      case SCROLL -> GuiUtil.drawScrollingText(context, this.textRenderer, this.text, x, y, this.color, this.shadow,
          availableWidth, this.scrollMargin, this.alignmentX
      );
    }

    this.textBounds = textBounds;
    this.bgBounds = bgBounds;
  }

  @Override
  public boolean isFocused() {
    return false;
  }

  @Override
  public void setFocused(boolean focused) {
  }

  public void setText(Text text) {
    this.text = text;
  }

  public Text getText() {
    return this.text;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public void setColor(int r, int g, int b) {
    this.setColor(GuiUtil.genColorInt(r, g, b));
  }

  public void setColor(int r, int g, int b, int a) {
    this.setColor(GuiUtil.genColorInt(r, g, b, a));
  }

  public void setBgColor(int bgColor) {
    this.bgColor = bgColor;
  }

  public void setBgColor(int r, int g, int b) {
    this.setBgColor(GuiUtil.genColorInt(r, g, b));
  }

  public void setBgColor(int r, int g, int b, int a) {
    this.setBgColor(GuiUtil.genColorInt(r, g, b, a));
  }

  public IntRect getTextBounds() {
    return this.textBounds;
  }

  public IntRect getBgBounds() {
    return this.bgBounds;
  }

  @Override
  public void setX(int x) {
    super.setX(x);
    this.setLayoutX(x);
  }

  @Override
  public void setY(int y) {
    super.setY(y);
    this.setLayoutY(y);
  }

  @Override
  public void setWidth(int width) {
    super.setWidth(width);
    this.setLayoutX(this.getX());
  }

  @Override
  public void setHeight(int height) {
    super.setHeight(height);
    this.setLayoutY(height);
  }

  @Override
  public int getX() {
    return this.layoutX;
  }

  @Override
  public int getY() {
    return this.layoutY;
  }

  protected void setLayoutX(int x) {
    this.layoutX = this.positionMode == PositionMode.ABSOLUTE ? x : this.alignmentX.getLeft(x, this.getWidth());
  }

  protected void setLayoutY(int y) {
    this.layoutY = this.positionMode == PositionMode.ABSOLUTE ? y : this.alignmentY.getTop(y, this.getHeight());
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

  protected int getTextRefX() {
    return switch (this.positionMode) {
      case ABSOLUTE -> this.getRefX();
      case REFERENCE -> this.getX();
    };
  }

  protected int getTextRefY() {
    return switch (this.positionMode) {
      case ABSOLUTE -> this.getRefY();
      case REFERENCE -> this.getY();
    };
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

  public Builder toBuilder() {
    return new Builder(this.textRenderer, this.text).position(this.getX(), this.getY())
        .positionMode(this.positionMode)
        .dimensions(this.getWidth(), this.getHeight())
        .color(this.color)
        .overflowBehavior(this.overflowBehavior)
        .scrollMargin(this.scrollMargin)
        .maxLines(this.maxLines)
        .lineSpacing(this.lineSpacing)
        .justifiedHorizontally(this.alignmentX)
        .alignedVertically(this.alignmentY)
        .padding(this.padding)
        .background(this.background)
        .bgColor(this.bgColor)
        .bgOverflow(this.bgOverflow)
        .shadow(this.shadow);
  }

  public static Builder builder(TextRenderer textRenderer, Text text) {
    return new Builder(textRenderer, text);
  }

  public static LabelWidget screenTitle(TextRenderer textRenderer, Text text, Screen screen) {
    return screenTitle(textRenderer, text, screen, GuiUtil.DEFAULT_HEADER_FOOTER_HEIGHT);
  }

  public static LabelWidget screenTitle(TextRenderer textRenderer, Text text, Screen screen, int headerHeight) {
    return new Builder(textRenderer, text).refPosition(screen.width / 2, headerHeight / 2)
        .dimensions(screen.width, headerHeight / 2)
        .alignedMiddle()
        .justifiedCenter()
        .hideBackground()
        .showShadow()
        .build();
  }

  @SuppressWarnings("unused")
  @Environment(EnvType.CLIENT)
  public static class Builder {
    private final TextRenderer textRenderer;
    private final Text text;

    private PositionMode positionMode = PositionMode.ABSOLUTE;
    private int x;
    private int y;
    private int width;
    private int height;
    private int color = GuiUtil.LABEL_COLOR;
    private TextAlignment alignmentH = TextAlignment.START;
    private TextAlignment alignmentV = TextAlignment.CENTER;
    private Spacing padding = DEFAULT_PADDING;
    private OverflowBehavior overflowBehavior = OverflowBehavior.SHOW;
    private int scrollMargin = 2;
    private int maxLines = 0;
    private int lineSpacing = 0;
    private boolean background = true;
    private int bgColor = GuiUtil.BACKGROUND_COLOR;
    private Spacing bgOverflow = Spacing.of(1, 0, 0, 1);
    private boolean shadow = false;

    public Builder(TextRenderer textRenderer, Text text) {
      this.textRenderer = textRenderer;
      this.text = text;
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

    public Builder justifiedLeft() {
      return this.justifiedHorizontally(TextAlignment.START);
    }

    public Builder justifiedCenter() {
      return this.justifiedHorizontally(TextAlignment.CENTER);
    }

    public Builder justifiedRight() {
      return this.justifiedHorizontally(TextAlignment.END);
    }

    public Builder justifiedHorizontally(TextAlignment alignmentH) {
      this.alignmentH = alignmentH;
      return this;
    }

    public Builder alignedTop() {
      return this.alignedVertically(TextAlignment.START);
    }

    public Builder alignedMiddle() {
      return this.alignedVertically(TextAlignment.CENTER);
    }

    public Builder alignedBottom() {
      return this.alignedVertically(TextAlignment.END);
    }

    public Builder alignedVertically(TextAlignment alignmentV) {
      this.alignmentV = alignmentV;
      return this;
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

    public LabelWidget build() {
      return new LabelWidget(this.textRenderer, this.text, this.color, this.positionMode, this.x, this.y, this.width,
          this.height, this.alignmentH, this.alignmentV, this.padding.copy(), this.overflowBehavior, this.scrollMargin,
          this.maxLines, this.lineSpacing, this.background, this.bgColor, this.bgOverflow.copy(), this.shadow
      );
    }
  }

  @Environment(EnvType.CLIENT)
  public enum PositionMode {
    REFERENCE, ABSOLUTE
  }

  @Environment(EnvType.CLIENT)
  public enum OverflowBehavior {
    SHOW("show"), TRUNCATE("truncate"), WRAP("wrap"), CLIP("clip"), SCROLL("scroll");

    private final String id;

    OverflowBehavior(String id) {
      this.id = id;
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
  }
}
