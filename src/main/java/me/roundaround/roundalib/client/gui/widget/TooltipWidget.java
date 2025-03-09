package me.roundaround.roundalib.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.WidgetTooltipPositioner;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class TooltipWidget implements Drawable, Widget {
  private int x;
  private int y;
  private int width;
  private int height;
  private List<Text> content;
  private List<OrderedText> lines;
  private Duration delay = Duration.ZERO;
  private long renderCheckTime;
  private boolean prevShouldRender;

  public TooltipWidget(Text content) {
    this(0, 0, List.of(content));
  }

  public TooltipWidget(List<Text> content) {
    this(0, 0, content);
  }

  public TooltipWidget(int width, int height, Text content) {
    this(width, height, List.of(content));
  }

  public TooltipWidget(int width, int height, List<Text> content) {
    this(0, 0, width, height, content);
  }

  public TooltipWidget(int x, int y, int width, int height, Text content) {
    this(x, y, width, height, List.of(content));
  }

  public TooltipWidget(int x, int y, int width, int height, List<Text> content) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.content = content;
  }

  @Override
  public int getX() {
    return this.x;
  }

  @Override
  public void setX(int x) {
    this.x = x;
  }

  @Override
  public int getY() {
    return this.y;
  }

  @Override
  public void setY(int y) {
    this.y = y;
  }

  @Override
  public int getWidth() {
    return this.width;
  }

  @Override
  public int getHeight() {
    return this.height;
  }

  public int getRight() {
    return this.getX() + this.getWidth();
  }

  public int getBottom() {
    return this.getY() + this.getHeight();
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public void setDimensions(int width, int height) {
    this.setWidth(width);
    this.setHeight(height);
  }

  public void setDimensionsAndPosition(int width, int height, int x, int y) {
    this.setDimensions(width, height);
    this.setPosition(x, y);
  }

  public void setContent(Text content) {
    this.setContent(List.of(content));
  }

  public void setContent(List<Text> content) {
    this.content = content;
    this.lines = null;
  }

  public void setTooltipDelay(Duration delay) {
    this.delay = delay;
  }

  public List<OrderedText> getLines(MinecraftClient client) {
    if (this.lines == null) {
      this.lines = this.content.stream().flatMap((line) -> Tooltip.wrapLines(client, line).stream()).toList();
    }
    return this.lines;
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    boolean hovered = context.scissorContains(mouseX, mouseY) && mouseX >= this.getX() && mouseY >= this.getY() &&
        mouseX < this.getX() + this.getWidth() && mouseY < this.getY() + this.getHeight();

    if (hovered != this.prevShouldRender) {
      if (hovered) {
        this.renderCheckTime = Util.getMeasuringTimeMs();
      }
      this.prevShouldRender = hovered;
    }

    if (hovered && Util.getMeasuringTimeMs() - this.renderCheckTime > this.delay.toMillis()) {
      MinecraftClient client = MinecraftClient.getInstance();
      Screen screen = client.currentScreen;
      if (screen != null) {
        screen.setTooltip(this.getLines(client), new WidgetTooltipPositioner(this.getNavigationFocus()), false);
      }
    }
  }

  @Override
  public void forEachChild(Consumer<ClickableWidget> consumer) {
  }
}
