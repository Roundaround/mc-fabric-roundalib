package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.client.gui.DrawableBuilder;
import me.roundaround.roundalib.client.gui.GuiUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class LabelWidget implements Drawable, Element {
  private int x;
  private int y;
  private final Alignment alignmentH;
  private final Alignment alignmentV;
  private final boolean showBackground;
  private final boolean showTextShadow;
  private final boolean shiftForPadding;
  private final TextRenderer textRenderer;

  private Text text;
  private int textWidth;
  private float left;
  private float right;
  private float top;
  private float bottom;
  private boolean layoutDirty = false;

  private LabelWidget(
      MinecraftClient client,
      Text text,
      int x,
      int y,
      Alignment alignmentH,
      Alignment alignmentV,
      boolean showBackground,
      boolean showTextShadow,
      boolean shiftForPadding
  ) {
    this.x = x;
    this.y = y;
    this.alignmentH = alignmentH;
    this.alignmentV = alignmentV;
    this.showBackground = showBackground;
    this.showTextShadow = showTextShadow;
    this.shiftForPadding = shiftForPadding;
    this.textRenderer = client.textRenderer;

    this.setText(text);
    this.updateLayout();
  }

  @Override
  public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
    if (this.showBackground) {
      drawContext.fill(MathHelper.floor(this.getLeft()) - 2, MathHelper.floor(this.getTop()) - 1,
          MathHelper.ceil(this.getRight()) + 2, MathHelper.ceil(this.getBottom()) + 1, GuiUtil.BACKGROUND_COLOR
      );
    }

    if (this.showTextShadow) {
      drawContext.drawTextWithShadow(
          this.textRenderer, this.text, Math.round(this.getLeft() + 0.5f), Math.round(this.getTop() + 1), 0xFFFFFFFF);
    } else {
      drawContext.drawText(this.textRenderer, this.text, Math.round(this.getLeft() + 0.5f),
          Math.round(this.getTop() + 1), GuiUtil.LABEL_COLOR, false
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
    this.textWidth = this.textRenderer.getWidth(text);
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
        this.left = this.x - this.textWidth / 2f;
        this.right = this.x + this.textWidth / 2f;
      }
      case END -> {
        this.left = this.x - this.textWidth;
        this.right = this.x;
      }
      default -> {
        this.left = this.x;
        this.right = this.x + this.textWidth;
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

  public static Builder centered(MinecraftClient client, Text text, int left, int top, int width, int height) {
    return new Builder(client, text, left + width / 2, top + height / 2).justifiedCenter().alignedMiddle();
  }

  public static class Builder implements DrawableBuilder<LabelWidget> {
    private final MinecraftClient client;
    private final Text text;
    private final int x;
    private final int y;
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

    @Override
    public LabelWidget build() {
      return new LabelWidget(this.client, this.text, this.x, this.y, this.alignmentH, this.alignmentV,
          this.showBackground, this.showTextShadow, this.shiftForPadding
      );
    }
  }

  public enum Alignment {
    START(1), CENTER(0), END(-1);

    private final int shiftOffset;

    Alignment(int shiftOffset) {
      this.shiftOffset = shiftOffset;
    }

    public int getShiftOffset() {
      return this.shiftOffset;
    }
  }
}
