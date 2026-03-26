package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.client.gui.icon.Icon;
import me.roundaround.roundalib.client.gui.util.GuiUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class IconButtonWidget extends Button {
  public static final int SIZE_V = 20;
  public static final int SIZE_L = 18;
  public static final int SIZE_M = 13;
  public static final int SIZE_S = 9;

  protected static final OnPress NOOP = (button) -> {
  };

  protected final int iconSize;
  protected final boolean dimIconWhenDisabled;
  protected final boolean hideMessage;
  protected final boolean hideBackground;

  protected Identifier texture;
  protected Identifier highlightedTexture;

  protected IconButtonWidget(
      int x,
      int y,
      int width,
      int height,
      Identifier texture,
      Identifier highlightedTexture,
      int iconSize,
      boolean dimIconWhenDisabled,
      net.minecraft.network.chat.Component message,
      boolean hideMessage,
      boolean hideBackground,
      OnPress onPress,
      Tooltip tooltip,
      CreateNarration narrationSupplier
  ) {
    super(x, y, width, height, message, onPress, narrationSupplier);

    this.texture = texture;
    this.highlightedTexture = highlightedTexture;
    this.iconSize = iconSize;
    this.dimIconWhenDisabled = dimIconWhenDisabled;
    this.hideMessage = hideMessage;
    this.hideBackground = hideBackground;

    if (tooltip != null) {
      this.setTooltip(tooltip);
    }
  }

  @Override
  public void renderContents(GuiGraphics context, int mouseX, int mouseY, float delta) {
    if (!this.hideBackground) {
      this.renderDefaultSprite(context);
    }

    Identifier texture = this.isHoveredOrFocused() && this.highlightedTexture != null ? this.highlightedTexture : this.texture;
    float brightness = this.active || !this.dimIconWhenDisabled ? 1f : 0.6f;
    int x = this.getX() + (this.getWidth() - this.iconSize) / 2;
    int y = this.getY() + (this.getHeight() - this.iconSize) / 2;

    context.blitSprite(
        RenderPipelines.GUI_TEXTURED,
        texture,
        x,
        y,
        this.iconSize,
        this.iconSize,
        GuiUtil.genColorInt(brightness, brightness, brightness)
    );
  }

  @Override
  protected void renderDefaultLabel(ActiveTextCollector drawer) {
    if (this.hideMessage) {
      return;
    }
    super.renderDefaultLabel(drawer);
  }

  public void setTexture(Icon icon, String modId) {
    this.texture = icon.getTexture(modId);
  }

  public void setTexture(Identifier texture) {
    this.texture = texture;
  }

  public void setHighlightedTexture(Icon icon, String modId) {
    this.highlightedTexture = icon.getTexture(modId);
  }

  public void setHighlightedTexture(Identifier texture) {
    this.highlightedTexture = texture;
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
    private Identifier highlightedTexture = null;
    private boolean dimIconWhenDisabled = true;
    private net.minecraft.network.chat.Component message = net.minecraft.network.chat.Component.empty();
    private boolean hideMessage = true;
    private boolean hideBackground = false;
    private OnPress onPress = NOOP;
    private Tooltip tooltip = null;
    private CreateNarration narrationSupplier = DEFAULT_NARRATION;

    private Builder(Icon icon, String modId) {
      this.texture = icon.getTexture(modId);
      this.iconSize = icon.getSize();
    }

    private Builder(Identifier texture, int iconSize) {
      this.texture = texture;
      this.iconSize = iconSize;
    }

    public Builder highlightedTexture(Icon icon, String modId) {
      this.highlightedTexture = icon.getTexture(modId);
      return this;
    }

    public Builder highlightedTexture(Identifier texture) {
      this.highlightedTexture = texture;
      return this;
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

    public Builder message(net.minecraft.network.chat.Component message) {
      this.message = message;
      return this;
    }

    public Builder showMessage() {
      this.hideMessage = false;
      return this;
    }

    public Builder hideBackground() {
      this.hideBackground = true;
      return this;
    }

    public Builder onPress(OnPress onPress) {
      this.onPress = onPress;
      return this;
    }

    public Builder tooltip(Tooltip tooltip) {
      this.tooltip = tooltip;
      return this;
    }

    public Builder tooltip(net.minecraft.network.chat.Component tooltip) {
      this.tooltip = Tooltip.create(tooltip);
      return this;
    }

    public Builder messageAndTooltip(net.minecraft.network.chat.Component text) {
      this.message = text;
      this.tooltip = Tooltip.create(text);
      return this;
    }

    public Builder narration(CreateNarration narrationSupplier) {
      this.narrationSupplier = narrationSupplier;
      return this;
    }

    public IconButtonWidget build() {
      return new IconButtonWidget(
          this.x,
          this.y,
          this.width,
          this.height,
          this.texture,
          this.highlightedTexture,
          this.iconSize,
          this.dimIconWhenDisabled,
          this.message,
          this.hideMessage,
          this.hideBackground,
          this.onPress,
          this.tooltip,
          this.narrationSupplier
      );
    }
  }
}
