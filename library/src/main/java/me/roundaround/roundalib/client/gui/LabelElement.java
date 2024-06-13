package me.roundaround.roundalib.client.gui;

import me.roundaround.roundalib.client.gui.layout.IntRect;
import me.roundaround.roundalib.client.gui.layout.Spacing;
import me.roundaround.roundalib.client.gui.layout.TextAlignment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class LabelElement implements Drawable, Element {
  private final TextRenderer textRenderer;
  private final TextAlignment alignmentH;
  private final TextAlignment alignmentV;
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
  private int x;
  private int y;
  private int maxWidth;
  private int bgColor;
  private boolean layoutDirty = true;
  private IntRect textBounds = IntRect.zero();
  private IntRect interactionBounds = IntRect.zero();
  private IntRect bgBounds = IntRect.zero();

  private LabelElement(
      TextRenderer textRenderer,
      Text text,
      int color,
      int x,
      int y,
      TextAlignment alignmentH,
      TextAlignment alignmentV,
      Spacing padding,
      int maxWidth,
      OverflowBehavior overflowBehavior,
      int scrollMargin,
      int maxLines,
      int lineSpacing,
      boolean background,
      int bgColor,
      Spacing bgOverflow,
      boolean shadow
  ) {
    this.textRenderer = textRenderer;
    this.text = text;
    this.color = color;
    this.x = x;
    this.y = y;
    this.alignmentH = alignmentH;
    this.alignmentV = alignmentV;
    this.padding = padding;
    this.maxWidth = maxWidth;
    this.overflowBehavior = overflowBehavior;
    this.scrollMargin = scrollMargin;
    this.maxLines = maxLines;
    this.lineSpacing = lineSpacing;
    this.background = background;
    this.bgColor = bgColor;
    this.bgOverflow = bgOverflow;
    this.shadow = shadow;

    this.updateLayout();
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    this.updateLayout();

    if (this.background) {
      context.fill(this.bgBounds.left(), this.bgBounds.top(), this.bgBounds.right(), this.bgBounds.bottom(),
          this.bgColor
      );
    }

    int y = this.textBounds.top();

    switch (this.overflowBehavior) {
      case SHOW ->
          GuiUtil.drawText(context, this.textRenderer, this.text, this.x, y, this.color, this.shadow, this.alignmentH);
      case TRUNCATE ->
          GuiUtil.drawTruncatedText(context, this.textRenderer, this.text, this.x, y, this.color, this.shadow,
              this.maxWidth, this.alignmentH
          );
      case WRAP -> GuiUtil.drawWrappedText(context, this.textRenderer, this.text, this.x, y, this.color, this.shadow,
          this.maxWidth, this.maxLines, this.lineSpacing, this.alignmentH
      );
      case CLIP -> {
        GuiUtil.enableScissor(context, this.textBounds);
        GuiUtil.drawText(context, this.textRenderer, this.text, this.x, y, this.color, this.shadow, this.alignmentH);
        GuiUtil.disableScissor(context);
      }
      case SCROLL ->
          GuiUtil.drawScrollingText(context, this.textRenderer, this.text, this.x, y, this.color, this.shadow,
              this.maxWidth, this.scrollMargin, this.alignmentH
          );
    }
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    this.updateLayout();
    return this.interactionBounds.contains(mouseX, mouseY);
  }

  @Override
  public boolean isFocused() {
    return false;
  }

  @Override
  public void setFocused(boolean focused) {
  }

  public void setText(Text text) {
    if (this.text != null && text.getString().equals(this.text.getString())) {
      return;
    }
    this.text = text;
    this.layoutDirty = true;
  }

  public Text getText() {
    return this.text.copy();
  }

  public void setX(int x) {
    this.x = x;
    this.layoutDirty = true;
  }

  public void setY(int y) {
    this.y = y;
    this.layoutDirty = true;
  }

  public void setPosition(int x, int y) {
    this.setX(x);
    this.setY(y);
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

  public void setMaxWidth(int maxWidth) {
    this.maxWidth = maxWidth;
    this.layoutDirty = true;
  }

  public IntRect getTextBounds() {
    return this.textBounds;
  }

  public IntRect getInteractionBounds() {
    return this.interactionBounds;
  }

  public IntRect getBgBounds() {
    return this.bgBounds;
  }

  private void updateLayout() {
    if (!this.layoutDirty) {
      return;
    }

    int textWidth = this.textRenderer.getWidth(this.text);
    int textHeight = this.textRenderer.fontHeight;

    if (this.maxWidth > 0) {
      int available = this.maxWidth - this.padding.getHorizontal();
      textWidth = Math.min(textWidth, available);
    }

    if (this.overflowBehavior == OverflowBehavior.WRAP) {
      textHeight = GuiUtil.measureWrappedTextHeight(
          this.textRenderer, this.text, this.maxWidth, this.maxLines, this.lineSpacing);
    }

    if (!this.shadow) {
      textHeight -= 1;
      textWidth -= 1;
    }

    int paddedX = switch (this.alignmentH) {
      case START -> this.x + this.padding.left();
      case END -> this.x - this.padding.right();
      case CENTER -> this.x;
    };
    int textLeft = this.alignmentH.getLeft(paddedX, textWidth);

    int textTop = switch (this.alignmentV) {
      case START -> this.y + this.padding.top();
      case END -> this.y - this.padding.bottom() - textHeight;
      case CENTER -> this.y - textHeight / 2;
    };

    this.textBounds = IntRect.byDimensions(textLeft, textTop, textWidth, textHeight);
    this.interactionBounds = this.textBounds.expand(this.padding);
    this.bgBounds = this.interactionBounds.expand(this.bgOverflow);

    this.layoutDirty = false;
  }

  public static Builder builder(TextRenderer textRenderer, Text text, int posX, int posY) {
    return new Builder(textRenderer, text, posX, posY);
  }

  public static LabelElement screenTitle(TextRenderer textRenderer, Text text, Screen screen) {
    return screenTitle(textRenderer, text, screen, GuiUtil.DEFAULT_HEADER_HEIGHT);
  }

  public static LabelElement screenTitle(TextRenderer textRenderer, Text text, Screen screen, int headerHeight) {
    return new Builder(textRenderer, text, (int) (screen.width * 0.5f), (int) (headerHeight * 0.5f)).alignedMiddle()
        .justifiedCenter()
        .hideBackground()
        .showShadow()
        .build();
  }

  @SuppressWarnings("unused")
  public static class Builder {
    private final TextRenderer textRenderer;
    private final Text text;
    private final int x;
    private final int y;
    private final Spacing padding = Spacing.of(1, 2);
    private final Spacing bgOverflow = Spacing.zero();

    private int color = GuiUtil.LABEL_COLOR;
    private TextAlignment alignmentH = TextAlignment.START;
    private TextAlignment alignmentV = TextAlignment.CENTER;
    private int maxWidth = 0;
    private OverflowBehavior overflowBehavior = OverflowBehavior.SHOW;
    private int scrollMargin = 2;
    private int maxLines = 0;
    private int lineSpacing = 0;
    private boolean background = true;
    private int bgColor = GuiUtil.BACKGROUND_COLOR;
    private boolean shadow = false;

    public Builder(TextRenderer textRenderer, Text text, int x, int y) {
      this.textRenderer = textRenderer;
      this.text = text;
      this.x = x;
      this.y = y;
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

    public Builder maxWidth(int maxWidth) {
      this.maxWidth = maxWidth;
      return this;
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
      this.padding.set(space);
      return this;
    }

    public Builder padding(int vertical, int horizontal) {
      this.padding.set(vertical, horizontal);
      return this;
    }

    public Builder padding(int top, int horizontal, int bottom) {
      this.padding.set(top, horizontal, bottom);
      return this;
    }

    public Builder padding(int top, int right, int bottom, int left) {
      this.padding.set(top, right, bottom, left);
      return this;
    }

    public Builder padding(Spacing padding) {
      this.padding.set(padding);
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
      this.bgOverflow.set(space);
      return this;
    }

    public Builder bgOverflow(int vertical, int horizontal) {
      this.bgOverflow.set(vertical, horizontal);
      return this;
    }

    public Builder bgOverflow(int top, int horizontal, int bottom) {
      this.bgOverflow.set(top, horizontal, bottom);
      return this;
    }

    public Builder bgOverflow(int top, int right, int bottom, int left) {
      this.bgOverflow.set(top, right, bottom, left);
      return this;
    }

    public Builder bgOverflow(Spacing bgOverflow) {
      this.bgOverflow.set(bgOverflow);
      return this;
    }

    public Builder showShadow() {
      return this.shadow(true);
    }

    public Builder shadow(boolean shadow) {
      this.shadow = shadow;
      return this;
    }

    public LabelElement build() {
      return new LabelElement(this.textRenderer, this.text, this.color, this.x, this.y, this.alignmentH,
          this.alignmentV, this.padding.copy(), this.maxWidth, this.overflowBehavior, this.scrollMargin, this.maxLines,
          this.lineSpacing, this.background, this.bgColor, this.bgOverflow.copy(), this.shadow
      );
    }
  }

  public enum OverflowBehavior {
    SHOW, TRUNCATE, WRAP, CLIP, SCROLL
  }
}
