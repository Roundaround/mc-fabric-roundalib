package me.roundaround.roundalib.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.roundalib.asset.icon.Icon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class IconButtonWidget extends ButtonWidget {
  public static final int SIZE_V = 20;
  public static final int SIZE_L = 18;
  public static final int SIZE_M = 13;
  public static final int SIZE_S = 9;

  protected static final PressAction NOOP = (button) -> {
  };

  protected final Identifier texture;
  protected final int iconSize;
  protected final boolean dimIconWhenDisabled;
  protected final boolean hideMessage;

  protected IconButtonWidget(
      int x,
      int y,
      int width,
      int height,
      Identifier texture,
      int iconSize,
      boolean dimIconWhenDisabled,
      Text message,
      boolean hideMessage,
      PressAction onPress,
      Tooltip tooltip,
      NarrationSupplier narrationSupplier
  ) {
    super(x, y, width, height, message, onPress, narrationSupplier);

    this.texture = texture;
    this.iconSize = iconSize;
    this.dimIconWhenDisabled = dimIconWhenDisabled;
    this.hideMessage = hideMessage;

    if (tooltip != null) {
      this.setTooltip(tooltip);
    }
  }

  @Override
  public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    super.renderWidget(context, mouseX, mouseY, delta);

    float brightness = this.active || !this.dimIconWhenDisabled ? 1f : 0.6f;

    int x = this.getX() + (this.getWidth() - this.iconSize) / 2;
    int y = this.getY() + (this.getHeight() - this.iconSize) / 2;

    RenderSystem.setShaderColor(brightness, brightness, brightness, 1f);
    context.drawGuiTexture(this.texture, x, y, this.iconSize, this.iconSize);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
  }

  @Override
  public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
    if (this.hideMessage) {
      return;
    }
    super.drawMessage(context, textRenderer, color);
  }

  public static Builder builder(Identifier texture, int iconSize) {
    return new Builder(texture, iconSize);
  }

  public static Builder builder(Icon icon, String modId) {
    return new Builder(icon, modId);
  }

  public static class Builder {
    private final Identifier texture;
    private final int iconSize;
    private int x = 0;
    private int y = 0;
    private int width = SIZE_L;
    private int height = SIZE_L;
    private boolean dimIconWhenDisabled = true;
    private Text message = Text.empty();
    private boolean hideMessage = true;
    private PressAction onPress = NOOP;
    private Tooltip tooltip = null;
    private NarrationSupplier narrationSupplier = DEFAULT_NARRATION_SUPPLIER;

    private Builder(Icon icon, String modId) {
      this.texture = icon.getTexture(modId);
      this.iconSize = icon.getSize();
    }

    private Builder(Identifier texture, int iconSize) {
      this.texture = texture;
      this.iconSize = iconSize;
    }

    public Builder dimensions(int size) {
      this.width = size;
      this.height = size;
      return this;
    }

    public Builder dimensions(int width, int height) {
      this.width = width;
      this.height = height;
      return this;
    }

    public Builder vanillaSize() {
      this.width = SIZE_V;
      this.height = SIZE_V;
      return this;
    }

    public Builder large() {
      this.width = SIZE_L;
      this.height = SIZE_L;
      return this;
    }

    public Builder medium() {
      this.width = SIZE_M;
      this.height = SIZE_M;
      return this;
    }

    public Builder small() {
      this.width = SIZE_S;
      this.height = SIZE_S;
      return this;
    }

    public Builder position(int x, int y) {
      this.x = x;
      this.y = y;
      return this;
    }

    public Builder disableIconDim() {
      this.dimIconWhenDisabled = false;
      return this;
    }

    public Builder message(Text message) {
      this.message = message;
      return this;
    }

    public Builder showMessage() {
      this.hideMessage = false;
      return this;
    }

    public Builder onPress(PressAction onPress) {
      this.onPress = onPress;
      return this;
    }

    public Builder tooltip(Tooltip tooltip) {
      this.tooltip = tooltip;
      return this;
    }

    public Builder tooltip(Text tooltip) {
      this.tooltip = Tooltip.of(tooltip);
      return this;
    }

    public Builder messageAndTooltip(Text text) {
      this.message = text;
      this.tooltip = Tooltip.of(text);
      return this;
    }

    public Builder narration(NarrationSupplier narrationSupplier) {
      this.narrationSupplier = narrationSupplier;
      return this;
    }

    public IconButtonWidget build() {
      return new IconButtonWidget(this.x, this.y, this.width, this.height, this.texture, this.iconSize,
          this.dimIconWhenDisabled, this.message, this.hideMessage, this.onPress, this.tooltip, this.narrationSupplier
      );
    }
  }
}
