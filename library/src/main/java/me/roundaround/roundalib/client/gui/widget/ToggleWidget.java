package me.roundaround.roundalib.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.linear.LinearLayoutWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class ToggleWidget extends PressableWidget implements LayoutWidget {
  public static final int DEFAULT_CONTROL_WIDTH = 16;
  public static final int DEFAULT_CONTROL_HEIGHT = 12;
  public static final int DEFAULT_BAR_WIDTH = 10;

  private static final Identifier TEXTURE = new Identifier("widget/slider");
  private static final Identifier HANDLE_TEXTURE = new Identifier("widget/slider_handle");
  private static final Identifier HANDLE_HIGHLIGHTED_TEXTURE = new Identifier("widget/slider_handle_highlighted");

  private final Consumer<ToggleWidget> pressAction;
  private final TextRenderer textRenderer;
  private final Consumer<Boolean> valueChanged;
  private final ValueToTextMapper labelTextMapper;
  private final ValueToTextMapper valueTextMapper;
  private final LinearLayoutWidget layout;
  private final LabelWidget displayLabel;
  private final LabelWidget valueLabel;

  private boolean value;
  private int controlWidth;
  private int controlHeight;
  private int barWidth;
  private ValueToTextMapper tooltipTextMapper;

  protected ToggleWidget(
      int x,
      int y,
      int width,
      int height,
      Consumer<ToggleWidget> pressAction,
      TextRenderer textRenderer,
      ValueToTextMapper labelTextMapper,
      int controlWidth,
      int controlHeight,
      int barWidth,
      Consumer<Boolean> valueChanged,
      ValueToTextMapper valueTextMapper,
      ValueToTextMapper tooltipTextMapper,
      boolean initialValue
  ) {
    super(x, y, width, height, labelTextMapper.apply(initialValue));

    this.pressAction = pressAction;
    this.textRenderer = textRenderer;
    this.labelTextMapper = labelTextMapper;

    this.controlWidth = controlWidth;
    this.controlHeight = controlHeight;
    this.barWidth = barWidth;

    this.valueChanged = valueChanged;
    this.valueTextMapper = valueTextMapper;
    this.tooltipTextMapper = tooltipTextMapper;

    this.value = initialValue;

    this.layout = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING / 2).defaultOffAxisContentAlignCenter();
    this.displayLabel = this.layout.add(
        LabelWidget.builder(this.textRenderer, this.labelTextMapper.apply(this.value)).build());

    this.layout.add(new DrawableWidget() {
      @Override
      public int getWidth() {
        return ToggleWidget.this.controlWidth;
      }

      @Override
      public int getHeight() {
        return ToggleWidget.this.controlHeight;
      }

      @Override
      protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        boolean value = ToggleWidget.this.value;
        int barWidth = ToggleWidget.this.barWidth;

        int offset = value ? this.getWidth() - barWidth : 0;

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        context.drawGuiTexture(TEXTURE, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        context.drawGuiTexture(
            ToggleWidget.this.getHandleTexture(), this.getX() + offset, this.getY(), barWidth, this.getHeight());
      }
    });

    if (this.valueTextMapper != null) {
      this.valueLabel = this.layout.add(
          LabelWidget.builder(this.textRenderer, this.valueTextMapper.apply(this.value)).build());
    } else {
      this.valueLabel = null;
    }

    this.updateTexts();
  }

  @Override
  public void onPress() {
    if (this.pressAction != null) {
      this.pressAction.accept(this);
      return;
    }
    this.defaultOnPress();
  }

  protected void defaultOnPress() {
    boolean value = this.toggle();
    if (this.valueChanged != null) {
      this.valueChanged.accept(value);
    }
  }

  @Override
  protected void appendClickableNarrations(NarrationMessageBuilder builder) {
  }

  @Override
  protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    this.hovered = this.layout.getBounds().contains(mouseX, mouseY);
    this.layout.forEachElement((widget) -> {
      if (widget instanceof Drawable drawable) {
        drawable.render(context, mouseX, mouseY, delta);
      }
    });
  }

  @Override
  public boolean isMouseOver(double mouseX, double mouseY) {
    return this.active && this.visible && this.layout.getBounds().contains(mouseX, mouseY);
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
  }

  @Override
  public void refreshPositions() {
    int valueWidth = this.valueTextMapper == null ?
        0 :
        Math.max(this.textRenderer.getWidth(this.valueTextMapper.apply(true)),
            this.textRenderer.getWidth(this.valueTextMapper.apply(false))
        ) + this.layout.getSpacing();

    this.displayLabel.setWidth(this.getWidth() - valueWidth - this.controlWidth - this.layout.getSpacing());

    this.layout.setPositionAndDimensions(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    this.layout.refreshPositions();
  }

  public void matchTooltipWithLabel() {
    this.setTooltip(this.labelTextMapper);
  }

  public void setTooltip(ValueToTextMapper tooltipTextMapper) {
    this.tooltipTextMapper = tooltipTextMapper;
    this.updateTooltipText();
  }

  public boolean toggle() {
    this.setValue(!this.value);
    return this.value;
  }

  public void setValue(boolean value) {
    this.value = value;
    this.updateTexts();
  }

  public boolean getValue() {
    return this.value;
  }

  public void setPositionAndDimensions(int x, int y, int width, int height) {
    this.setDimensionsAndPosition(width, height, x, y);
  }

  public void setControlWidth(int controlWidth) {
    this.controlWidth = controlWidth;
  }

  public void setControlHeight(int controlHeight) {
    this.controlHeight = controlHeight;
  }

  public void setControlDimensions(int controlWidth, int controlHeight) {
    this.controlWidth = controlWidth;
    this.controlHeight = controlHeight;
  }

  public void setBarWidth(int barWidth) {
    this.barWidth = barWidth;
  }

  public void setControlSizes(int controlWidth, int controlHeight, int barWidth) {
    this.controlWidth = controlWidth;
    this.controlHeight = controlHeight;
    this.barWidth = barWidth;
  }

  protected void updateTexts() {
    this.updateLabelText();
    this.updateValueText();
    this.updateTooltipText();
  }

  protected void updateLabelText() {
    Text text = this.labelTextMapper.apply(this.value);
    this.setMessage(text);
    this.displayLabel.setText(text);
  }

  protected void updateValueText() {
    if (this.valueLabel == null || this.valueTextMapper == null) {
      return;
    }
    this.valueLabel.setText(this.valueTextMapper.apply(this.value));
  }

  protected void updateTooltipText() {
    if (this.tooltipTextMapper != null) {
      this.setTooltip(Tooltip.of(this.tooltipTextMapper.apply(this.value)));
    } else {
      this.setTooltip((Tooltip) null);
    }
  }

  protected Identifier getHandleTexture() {
    if (this.isFocused() || this.hovered) {
      return HANDLE_HIGHLIGHTED_TEXTURE;
    }
    return HANDLE_TEXTURE;
  }

  public static Builder onOffBuilder(TextRenderer textRenderer, ValueToTextMapper labelTextMapper) {
    return new Builder(textRenderer, labelTextMapper).showValue(ValueToTextMapper.onOff());
  }

  public static Builder yesNoBuilder(TextRenderer textRenderer, ValueToTextMapper labelTextMapper) {
    return new Builder(textRenderer, labelTextMapper).showValue(ValueToTextMapper.yesNo());
  }

  public static Builder enabledDisabledBuilder(
      TextRenderer textRenderer, ValueToTextMapper labelTextMapper, String modId
  ) {
    return new Builder(textRenderer, labelTextMapper).showValue(ValueToTextMapper.enabledDisabled(modId));
  }

  @Environment(EnvType.CLIENT)
  public interface ValueToTextMapper extends Function<Boolean, Text> {
    static ValueToTextMapper onOff() {
      return (value) -> value ? ScreenTexts.ON : ScreenTexts.OFF;
    }

    static ValueToTextMapper yesNo() {
      return (value) -> value ? ScreenTexts.YES : ScreenTexts.NO;
    }

    static ValueToTextMapper enabledDisabled(String modId) {
      return (value) -> value ?
          Text.translatable(modId + ".roundalib.toggle.enabled") :
          Text.translatable(modId + ".roundalib.toggle.disabled");
    }
  }

  @Environment(EnvType.CLIENT)
  public static class Builder {
    private final TextRenderer textRenderer;
    private final ValueToTextMapper labelTextMapper;

    private int x = 0;
    private int y = 0;
    private int width = 0;
    private int height = 0;
    private int controlWidth = DEFAULT_CONTROL_WIDTH;
    private int controlHeight = DEFAULT_CONTROL_HEIGHT;
    private int barWidth = DEFAULT_BAR_WIDTH;
    private Consumer<ToggleWidget> pressAction = null;
    private Consumer<Boolean> valueChanged = null;
    private ValueToTextMapper valueTextMapper = null;
    private ValueToTextMapper tooltipTextMapper = null;
    private boolean initialValue = false;

    private Builder(
        TextRenderer textRenderer, ValueToTextMapper labelTextMapper
    ) {
      this.textRenderer = textRenderer;
      this.labelTextMapper = labelTextMapper;
    }

    public Builder setX(int x) {
      this.x = x;
      return this;
    }

    public Builder setY(int y) {
      this.y = y;
      return this;
    }

    public Builder setPosition(int x, int y) {
      this.x = x;
      this.y = y;
      return this;
    }

    public Builder setWidth(int width) {
      this.width = width;
      return this;
    }

    public Builder setHeight(int height) {
      this.height = height;
      return this;
    }

    public Builder setDimensions(int width, int height) {
      this.width = width;
      this.height = height;
      return this;
    }

    public Builder setPositionAndDimensions(int x, int y, int width, int height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      return this;
    }

    public Builder setControlWidth(int controlWidth) {
      this.controlWidth = controlWidth;
      return this;
    }

    public Builder setControlHeight(int controlHeight) {
      this.controlHeight = controlHeight;
      return this;
    }

    public Builder setControlDimensions(int controlWidth, int controlHeight) {
      this.controlWidth = controlWidth;
      this.controlHeight = controlHeight;
      return this;
    }

    public Builder setBarWidth(int barWidth) {
      this.barWidth = barWidth;
      return this;
    }

    public Builder setControlSizes(int controlWidth, int controlHeight, int barWidth) {
      this.controlWidth = controlWidth;
      this.controlHeight = controlHeight;
      this.barWidth = barWidth;
      return this;
    }

    public Builder onPress(Consumer<ToggleWidget> pressAction) {
      this.pressAction = pressAction;
      return this;
    }

    public Builder onChange(Consumer<Boolean> valueChanged) {
      this.valueChanged = valueChanged;
      return this;
    }

    public Builder showValue(ValueToTextMapper valueTextMapper) {
      this.valueTextMapper = valueTextMapper;
      return this;
    }

    public Builder initially(boolean initialValue) {
      this.initialValue = initialValue;
      return this;
    }

    public Builder setTooltip(Text tooltip) {
      this.tooltipTextMapper = (value) -> tooltip;
      return this;
    }

    public Builder setTooltip(ValueToTextMapper tooltipTextMapper) {
      this.tooltipTextMapper = tooltipTextMapper;
      return this;
    }

    public Builder matchTooltipToLabel() {
      this.tooltipTextMapper = this.labelTextMapper;
      return this;
    }

    public ToggleWidget build() {
      return new ToggleWidget(this.x, this.y, this.width, this.height, this.pressAction, this.textRenderer,
          this.labelTextMapper, this.controlWidth, this.controlHeight, this.barWidth, this.valueChanged,
          this.valueTextMapper, this.tooltipTextMapper, this.initialValue
      );
    }
  }
}
