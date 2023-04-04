package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.client.gui.DrawableBuilder;
import me.roundaround.roundalib.client.gui.GuiUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class LabelWidget extends DrawableHelper implements Drawable {
  public static final int HEIGHT_WITH_PADDING = 13;
  public static final int PADDING = 2;

  private final int posX;
  private final int posY;
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

  private LabelWidget(
      MinecraftClient client,
      Text text,
      int posX,
      int posY,
      Alignment alignmentH,
      Alignment alignmentV,
      boolean showBackground,
      boolean showTextShadow,
      boolean shiftForPadding) {
    this.posX = posX;
    this.posY = posY;
    this.alignmentH = alignmentH;
    this.alignmentV = alignmentV;
    this.showBackground = showBackground;
    this.showTextShadow = showTextShadow;
    this.shiftForPadding = shiftForPadding;
    this.textRenderer = client.textRenderer;

    setText(text);
  }

  @Override
  public void render(
      MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    if (showBackground) {
      fill(matrixStack,
          MathHelper.floor(left) - 2,
          MathHelper.floor(top) - 1,
          MathHelper.ceil(right) + 2,
          MathHelper.ceil(bottom) + 1,
          GuiUtil.BACKGROUND_COLOR);
    }

    if (showTextShadow) {
      textRenderer.drawWithShadow(matrixStack,
          text,
          left + 0.5f,
          top + 1,
          0xFFFFFFFF);
    } else {
      textRenderer.draw(matrixStack,
          text,
          left + 0.5f,
          top + 1,
          GuiUtil.LABEL_COLOR);
    }
  }

  public boolean isMouseOver(double mouseX, double mouseY) {
    int pixelLeft = MathHelper.floor(left) - (showBackground ? 2 : 0);
    int pixelRight = MathHelper.ceil(right) + (showBackground ? 2 : 0);
    int pixelTop = MathHelper.floor(top) - (showBackground ? 1 : 0);
    int pixelBottom = MathHelper.ceil(bottom) + (showBackground ? 1 : 0);
    return mouseX >= pixelLeft && mouseY >= pixelTop && mouseX < pixelRight &&
        mouseY < pixelBottom;
  }

  public void setText(Text text) {
    if (this.text != null && text.getString().equals(this.text.getString())) {
      return;
    }
    this.text = text;
    textWidth = textRenderer.getWidth(text);
    calculateBounds();
  }

  public int getLeft() {
    return MathHelper.floor(left);
  }

  public int getRight() {
    return MathHelper.ceil(right);
  }

  public int getTop() {
    return MathHelper.floor(top);
  }

  public int getBottom() {
    return MathHelper.ceil(bottom);
  }

  private void calculateBounds() {
    switch (alignmentH) {
      case CENTER:
        left = posX - textWidth / 2f;
        right = posX + textWidth / 2f;
        break;
      case END:
        left = posX - textWidth;
        right = posX;
        break;
      case START:
      default:
        left = posX;
        right = posX + textWidth;
    }

    switch (alignmentV) {
      case START:
        top = posY;
        bottom = posY + 10;
        break;
      case END:
        top = posY - 10;
        bottom = posY;
        break;
      case CENTER:
      default:
        top = posY - 10 / 2f;
        bottom = posY + 10 / 2f;
    }

    if (shiftForPadding) {
      left += alignmentH.getShiftOffset() * 2;
      right += alignmentH.getShiftOffset() * 2;
      top += alignmentV.getShiftOffset();
      bottom += alignmentV.getShiftOffset();
    }
  }

  public static Builder builder(
      MinecraftClient client, Text text, int posX, int posY) {
    return new Builder(client, text, posX, posY);
  }

  public static class Builder implements DrawableBuilder<LabelWidget> {
    private final MinecraftClient client;
    private final Text text;
    private final int posX;
    private final int posY;
    private Alignment alignmentH = Alignment.START;
    private Alignment alignmentV = Alignment.CENTER;
    private boolean showBackground = true;
    private boolean showTextShadow = false;
    private boolean shiftForPadding = false;

    public Builder(MinecraftClient client, Text text, int posX, int posY) {
      this.client = client;
      this.text = text;
      this.posX = posX;
      this.posY = posY;
    }

    public Builder justifiedLeft() {
      alignmentH = Alignment.START;
      return this;
    }

    public Builder justifiedCenter() {
      alignmentH = Alignment.CENTER;
      return this;
    }

    public Builder justifiedRight() {
      alignmentH = Alignment.END;
      return this;
    }

    public Builder alignedTop() {
      alignmentV = Alignment.START;
      return this;
    }

    public Builder alignedMiddle() {
      alignmentV = Alignment.CENTER;
      return this;
    }

    public Builder alignedBottom() {
      alignmentV = Alignment.END;
      return this;
    }

    public Builder hideBackground() {
      showBackground = false;
      return this;
    }

    public Builder showTextShadow() {
      showTextShadow = true;
      return this;
    }

    public Builder shiftForPadding() {
      shiftForPadding = true;
      return this;
    }

    @Override
    public LabelWidget build() {
      return new LabelWidget(client,
          text,
          posX,
          posY,
          alignmentH,
          alignmentV,
          showBackground,
          showTextShadow,
          shiftForPadding);
    }
  }

  public enum Alignment {
    START(1),
    CENTER(0),
    END(-1);

    private final int shiftOffset;

    Alignment(int shiftOffset) {
      this.shiftOffset = shiftOffset;
    }

    public int getShiftOffset() {
      return shiftOffset;
    }
  }
}
