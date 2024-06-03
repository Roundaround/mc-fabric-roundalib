package me.roundaround.roundalib.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;

public class LabelElement implements Drawable, Element {
  private int x;
  private int y;
  private int color;
  private int bgColor;
  private int maxWidth;
  private final int maxLines;
  private final OverflowBehavior overflowBehavior;
  private final Alignment alignmentH;
  private final Alignment alignmentV;
  private final boolean showBackground;
  private final boolean showTextShadow;
  private final boolean shiftForPadding;
  private final TextRenderer textRenderer;

  private Text text;
  private int width;
  private float left;
  private float right;
  private float top;
  private float bottom;
  private boolean layoutDirty = false;

  private LabelElement(
      MinecraftClient client,
      Text text,
      int x,
      int y,
      int color,
      int bgColor,
      int maxWidth,
      int maxLines,
      OverflowBehavior overflowBehavior,
      Alignment alignmentH,
      Alignment alignmentV,
      boolean showBackground,
      boolean showTextShadow,
      boolean shiftForPadding
  ) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.bgColor = bgColor;
    this.maxWidth = maxWidth;
    this.maxLines = maxLines;
    this.overflowBehavior = overflowBehavior;
    this.alignmentH = alignmentH;
    this.alignmentV = alignmentV;
    this.showBackground = showBackground;
    this.showTextShadow = showTextShadow;
    this.shiftForPadding = shiftForPadding;
    this.textRenderer = client.textRenderer;
    this.text = text;
    this.width = this.getWidth();

    this.updateLayout();
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    if (this.showBackground) {
      context.fill(MathHelper.floor(this.getLeft()) - 2, MathHelper.floor(this.getTop()) - 1,
          MathHelper.ceil(this.getRight()) + 2, MathHelper.ceil(this.getBottom()) + 1, this.bgColor
      );
    }

    switch (this.overflowBehavior) {
      case SHOW:
        GuiUtil.drawText(context, this.textRenderer, this.text, this.x, Math.round(this.getTop() + 1), this.color,
            this.showTextShadow, this.alignmentH.asTextAlignment()
        );
      case TRUNCATE:
        GuiUtil.drawTruncatedText(context, this.textRenderer, this.text, this.x, Math.round(this.getTop() + 1),
            this.color, this.showTextShadow, this.maxWidth, this.alignmentH.asTextAlignment()
        );
      case WRAP:
        GuiUtil.drawWrappedText(context, this.textRenderer, this.text, this.x, Math.round(this.getTop() + 1),
            this.color, this.showTextShadow, this.maxWidth, this.maxLines, this.alignmentH.asTextAlignment()
        );
    }
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    int pixelLeft = MathHelper.floor(this.getLeft()) - (this.showBackground ? 2 : 0);
    int pixelRight = MathHelper.ceil(this.getRight()) + (this.showBackground ? 2 : 0);
    int pixelTop = MathHelper.floor(this.getTop()) - (this.showBackground ? 1 : 0);
    int pixelBottom = MathHelper.ceil(this.getBottom()) + (this.showBackground ? 1 : 0);
    return mouseX >= pixelLeft && mouseY >= pixelTop && mouseX < pixelRight && mouseY < pixelBottom;
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
    this.width = this.getWidth();
    this.layoutDirty = true;
  }

  public Text getText() {
    return this.text.copy();
  }

  public int getWidth() {
    int textWidth = this.textRenderer.getWidth(this.text);
    if (this.maxWidth <= 0) {
      return textWidth;
    }
    return Math.min(textWidth, this.maxWidth);
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

  public float getLeft() {
    if (this.layoutDirty) {
      this.updateLayout();
    }
    return this.left;
  }

  public float getRight() {
    if (this.layoutDirty) {
      this.updateLayout();
    }
    return this.right;
  }

  public float getTop() {
    if (this.layoutDirty) {
      this.updateLayout();
    }
    return this.top;
  }

  public float getBottom() {
    if (this.layoutDirty) {
      this.updateLayout();
    }
    return this.bottom;
  }

  private void updateLayout() {
    if (!this.layoutDirty) {
      return;
    }

    switch (this.alignmentH) {
      case CENTER -> {
        this.left = this.x - this.width / 2f;
        this.right = this.x + this.width / 2f;
      }
      case END -> {
        this.left = this.x - this.width;
        this.right = this.x;
      }
      default -> {
        this.left = this.x;
        this.right = this.x + this.width;
      }
    }

    switch (this.alignmentV) {
      case START -> {
        this.top = this.y;
        this.bottom = this.y + 10;
      }
      case END -> {
        this.top = this.y - 10;
        this.bottom = this.y;
      }
      default -> {
        this.top = this.y - 10 / 2f;
        this.bottom = this.y + 10 / 2f;
      }
    }

    if (this.shiftForPadding) {
      this.left += this.alignmentH.getShiftOffset() * 2;
      this.right += this.alignmentH.getShiftOffset() * 2;
      this.top += this.alignmentV.getShiftOffset();
      this.bottom += this.alignmentV.getShiftOffset();
    }

    this.layoutDirty = false;
  }

  public static Builder builder(MinecraftClient client, Text text, int posX, int posY) {
    return new Builder(client, text, posX, posY);
  }

  @SuppressWarnings("unused")
  public static class Builder {
    private final MinecraftClient client;
    private final Text text;
    private final int x;
    private final int y;
    private int color = GuiUtil.LABEL_COLOR;
    private int bgColor = GuiUtil.BACKGROUND_COLOR;
    private int maxWidth = 0;
    private int maxLines = 0;
    private OverflowBehavior overflowBehavior = OverflowBehavior.SHOW;
    private Alignment alignmentH = Alignment.START;
    private Alignment alignmentV = Alignment.CENTER;
    private boolean showBackground = true;
    private boolean showTextShadow = false;
    private boolean shiftForPadding = false;

    public Builder(MinecraftClient client, Text text, int x, int y) {
      this.client = client;
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

    public Builder maxWidth(int maxWidth) {
      this.maxWidth = maxWidth;
      return this;
    }

    public Builder maxLines(int maxLines) {
      this.maxLines = maxLines;
      return this;
    }

    public Builder overflowBehavior(OverflowBehavior overflowBehavior) {
      this.overflowBehavior = overflowBehavior;
      return this;
    }

    public Builder justifiedLeft() {
      this.alignmentH = Alignment.START;
      return this;
    }

    public Builder justifiedCenter() {
      this.alignmentH = Alignment.CENTER;
      return this;
    }

    public Builder justifiedRight() {
      this.alignmentH = Alignment.END;
      return this;
    }

    public Builder alignedTop() {
      this.alignmentV = Alignment.START;
      return this;
    }

    public Builder alignedMiddle() {
      this.alignmentV = Alignment.CENTER;
      return this;
    }

    public Builder alignedBottom() {
      this.alignmentV = Alignment.END;
      return this;
    }

    public Builder hideBackground() {
      this.showBackground = false;
      return this;
    }

    public Builder showTextShadow() {
      this.showTextShadow = true;
      return this;
    }

    public Builder shiftForPadding() {
      this.shiftForPadding = true;
      return this;
    }

    public LabelElement build() {
      return new LabelElement(this.client, this.text, this.x, this.y, this.color, this.bgColor, this.maxWidth,
          this.maxLines, this.overflowBehavior, this.alignmentH, this.alignmentV, this.showBackground,
          this.showTextShadow, this.shiftForPadding
      );
    }
  }

  public enum Alignment {
    START(1), CENTER(0), END(-1);

    private final int shiftOffset;

    Alignment(int shiftOffset) {
      this.shiftOffset = shiftOffset;
    }

    public static Alignment from(GuiUtil.TextAlignment textAlignment) {
      return switch (textAlignment) {
        case CENTER -> CENTER;
        case RIGHT -> END;
        default -> START;
      };
    }

    public int getShiftOffset() {
      return this.shiftOffset;
    }

    public GuiUtil.TextAlignment asTextAlignment() {
      return switch (this) {
        case CENTER -> GuiUtil.TextAlignment.CENTER;
        case END -> GuiUtil.TextAlignment.RIGHT;
        default -> GuiUtil.TextAlignment.LEFT;
      };
    }
  }

  public enum OverflowBehavior {
    SHOW, TRUNCATE, WRAP;
  }
}
