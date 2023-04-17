package me.roundaround.roundalib.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class IconButtonWidget extends ButtonWidget {
  protected static final int DEFAULT_SIZE = 20;
  protected static final int DEFAULT_TEXTURE_SIZE = 256;

  protected final int iconU;
  protected final int iconV;
  protected final int textureSize;
  protected final Identifier texture;

  protected IconButtonWidget(
      int x,
      int y,
      int width,
      int height,
      int iconU,
      int iconV,
      int textureSize,
      Identifier texture,
      PressAction onPress,
      Tooltip tooltip) {
    super(x, y, width, height, Text.empty(), onPress, DEFAULT_NARRATION_SUPPLIER);
    this.iconU = iconU;
    this.iconV = iconV;
    this.textureSize = textureSize;
    this.texture = texture;

    if (tooltip != null) {
      this.setTooltip(tooltip);
    }
  }

  @Override
  public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    super.renderButton(matrixStack, mouseX, mouseY, delta);

    float brightness = this.active ? 1f : 0.6f;

    RenderSystem.setShaderTexture(0, this.texture);
    RenderSystem.setShaderColor(brightness, brightness, brightness, 1f);
    drawTexture(matrixStack,
        getX(),
        getY(),
        this.iconU,
        this.iconV,
        this.width,
        this.height,
        this.textureSize,
        this.textureSize);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
  }

  public static Builder builder(Identifier texture, PressAction onPress) {
    return new Builder(texture, onPress);
  }

  public static class Builder {
    private final Identifier texture;
    private final PressAction onPress;

    private int size = DEFAULT_SIZE;
    private int x;
    private int y;
    private int iconU;
    private int iconV;
    private int textureSize = DEFAULT_TEXTURE_SIZE;
    private boolean autoCalculateUV = false;
    private int textureIndex;
    private int autoCalculateStartX = 0;
    private int autoCalculateStartY = 0;
    private Tooltip tooltip = null;

    public Builder(Identifier texture, PressAction onPress) {
      this.texture = texture;
      this.onPress = onPress;
    }

    public Builder size(int size) {
      this.size = size;
      return this;
    }

    public Builder position(int x, int y) {
      this.x = x;
      this.y = y;
      return this;
    }

    public Builder uv(int u, int v) {
      this.iconU = u;
      this.iconV = v;
      return this;
    }

    public Builder textureSize(int textureSize) {
      this.textureSize = textureSize;
      return this;
    }

    public Builder autoCalculateUV(int textureIndex) {
      this.autoCalculateUV = true;
      this.textureIndex = textureIndex;
      return this;
    }

    public Builder autoCalculateUV(int textureIndex, int startX, int startY) {
      this.autoCalculateStartX = startX;
      this.autoCalculateStartY = startY;
      return this.autoCalculateUV(textureIndex);
    }

    public Builder tooltip(Tooltip tooltip) {
      this.tooltip = tooltip;
      return this;
    }

    public Builder tooltip(Text tooltip) {
      return this.tooltip(Tooltip.of(tooltip));
    }

    public IconButtonWidget build() {
      int u = this.iconU;
      int v = this.iconV;

      if (this.autoCalculateUV) {
        int workableWidth = this.textureSize - this.autoCalculateStartX;
        int iconsPerRow = workableWidth / this.size;

        u = this.autoCalculateStartX + (this.textureIndex % iconsPerRow) * this.size;
        v = this.autoCalculateStartY + (this.textureIndex / iconsPerRow) * this.size;
      }

      return new IconButtonWidget(this.x,
          this.y,
          this.size,
          this.size,
          u,
          v,
          this.textureSize,
          this.texture,
          this.onPress,
          this.tooltip);
    }
  }
}
