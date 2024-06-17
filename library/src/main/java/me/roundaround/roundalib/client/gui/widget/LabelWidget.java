package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.IntRect;
import me.roundaround.roundalib.client.gui.layout.Spacing;
import me.roundaround.roundalib.client.gui.layout.TextAlignment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class LabelWidget implements Drawable, Element, LayoutWidget {
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
  private IntRect textBounds = IntRect.zero();
  private IntRect interactionBounds = IntRect.zero();
  private IntRect bgBounds = IntRect.zero();

  private LabelWidget(
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

    this.refreshPositions();
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
  }

  @Override
  public void refreshPositions() {
    int textWidth = Math.min(this.textRenderer.getWidth(this.text), this.getAvailableWidth());
    int textHeight = this.textRenderer.fontHeight;

    if (this.overflowBehavior == OverflowBehavior.WRAP) {
      textHeight = GuiUtil.measureWrappedTextHeight(
          this.textRenderer, this.text, this.maxWidth, this.maxLines, this.lineSpacing);
    }

    int textLeft = this.alignmentH.getLeft(this.getPaddedX(), textWidth);
    int textTop = this.alignmentV.getTop(this.getPaddedY(), textHeight);

    this.textBounds = IntRect.byDimensions(textLeft, textTop, textWidth, textHeight);
    this.interactionBounds = this.textBounds.expand(this.padding);
    this.bgBounds = this.interactionBounds.expand(this.bgOverflow);
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    this.refreshPositions();

    if (this.background) {
      context.fill(this.bgBounds.left(), this.bgBounds.top(), this.bgBounds.right(), this.bgBounds.bottom(),
          this.bgColor
      );
    }

    int x = this.getPaddedX();
    int y = this.textBounds.top();
    int availableWidth = this.getAvailableWidth();

    switch (this.overflowBehavior) {
      case SHOW ->
          GuiUtil.drawText(context, this.textRenderer, this.text, x, y, this.color, this.shadow, this.alignmentH);
      case TRUNCATE -> GuiUtil.drawTruncatedText(context, this.textRenderer, this.text, x, y, this.color, this.shadow,
          availableWidth, this.alignmentH
      );
      case WRAP ->
          GuiUtil.drawWrappedText(context, this.textRenderer, this.text, x, y, this.color, this.shadow, availableWidth,
              this.maxLines, this.lineSpacing, this.alignmentH
          );
      case CLIP -> {
        context.enableScissor(this.textBounds.left(), this.textBounds.top(), this.textBounds.right(),
            this.textBounds.bottom()
        );
        GuiUtil.drawText(context, this.textRenderer, this.text, x, y, this.color, this.shadow, this.alignmentH);
        context.disableScissor();
      }
      case SCROLL -> GuiUtil.drawScrollingText(context, this.textRenderer, this.text, x, y, this.color, this.shadow,
          availableWidth, this.scrollMargin, this.alignmentH
      );
    }
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    this.refreshPositions();
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
  }

  public Text getText() {
    return this.text.copy();
  }

  @Override
  public void setX(int x) {
    this.x = x;
  }

  @Override
  public void setY(int y) {
    this.y = y;
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

  @Override
  public int getX() {
    return this.bgBounds.left();
  }

  @Override
  public int getY() {
    return this.bgBounds.top();
  }

  @Override
  public int getWidth() {
    return this.bgBounds.getWidth();
  }

  @Override
  public int getHeight() {
    return this.bgBounds.getHeight();
  }

  @Override
  public ScreenRect getNavigationFocus() {
    return new ScreenRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
  }

  private int getAvailableWidth() {
    if (this.maxWidth <= 0) {
      return Integer.MAX_VALUE;
    }
    return this.maxWidth - this.padding.getHorizontal();
  }

  private int getPaddedX() {
    return switch (this.alignmentH) {
      case START -> this.x + this.padding.left();
      case END -> this.x - this.padding.right();
      case CENTER -> this.x;
    };
  }

  private int getPaddedY() {
    return switch (this.alignmentV) {
      case START -> this.y + this.padding.top();
      case END -> this.y - this.padding.bottom();
      case CENTER -> this.y;
    };
  }

  public Selectable createSelectable() {
    return new Selectable() {
      public Selectable.SelectionType getType() {
        return Selectable.SelectionType.HOVERED;
      }

      public void appendNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, LabelWidget.this.getText());
      }
    };
  }

  public Builder toBuilder() {
    return new Builder(this.textRenderer, this.text, this.x, this.y).color(this.color)
        .maxWidth(this.maxWidth)
        .overflowBehavior(this.overflowBehavior)
        .scrollMargin(this.scrollMargin)
        .maxLines(this.maxLines)
        .lineSpacing(this.lineSpacing)
        .justifiedHorizontally(this.alignmentH)
        .alignedVertically(this.alignmentV)
        .padding(this.padding)
        .background(this.background)
        .bgColor(this.bgColor)
        .bgOverflow(this.bgOverflow)
        .shadow(this.shadow);
  }

  public static Builder builder(TextRenderer textRenderer, Text text, int posX, int posY) {
    return new Builder(textRenderer, text, posX, posY);
  }

  public static LabelWidget screenTitle(TextRenderer textRenderer, Text text, Screen screen) {
    return screenTitle(textRenderer, text, screen, GuiUtil.DEFAULT_HEADER_FOOTER_HEIGHT);
  }

  public static LabelWidget screenTitle(TextRenderer textRenderer, Text text, Screen screen, int headerHeight) {
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

    private int color = GuiUtil.LABEL_COLOR;
    private TextAlignment alignmentH = TextAlignment.START;
    private TextAlignment alignmentV = TextAlignment.CENTER;
    private Spacing padding = Spacing.of(1, 2);
    private int maxWidth = 0;
    private OverflowBehavior overflowBehavior = OverflowBehavior.SHOW;
    private int scrollMargin = 2;
    private int maxLines = 0;
    private int lineSpacing = 0;
    private boolean background = true;
    private int bgColor = GuiUtil.BACKGROUND_COLOR;
    private Spacing bgOverflow = Spacing.of(1, 0, 0, 1);
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
      return new LabelWidget(this.textRenderer, this.text, this.color, this.x, this.y, this.alignmentH,
          this.alignmentV, this.padding.copy(), this.maxWidth, this.overflowBehavior, this.scrollMargin, this.maxLines,
          this.lineSpacing, this.background, this.bgColor, this.bgOverflow.copy(), this.shadow
      );
    }
  }

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
