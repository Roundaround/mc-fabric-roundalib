package me.roundaround.roundalib.client.gui.widget;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.inventory.tooltip.MenuTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class TooltipWidget implements Renderable, LayoutElement {
  private int x;
  private int y;
  private int width;
  private int height;
  private List<Component> content;
  private List<FormattedCharSequence> lines;
  private Duration delay = Duration.ZERO;
  private long renderCheckTime;
  private boolean prevShouldRender;

  public TooltipWidget(Component content) {
    this(0, 0, List.of(content));
  }

  public TooltipWidget(List<Component> content) {
    this(0, 0, content);
  }

  public TooltipWidget(int width, int height, Component content) {
    this(width, height, List.of(content));
  }

  public TooltipWidget(int width, int height, List<Component> content) {
    this(0, 0, width, height, content);
  }

  public TooltipWidget(int x, int y, int width, int height, Component content) {
    this(x, y, width, height, List.of(content));
  }

  public TooltipWidget(int x, int y, int width, int height, List<Component> content) {
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

  public void setContent(Component content) {
    this.setContent(List.of(content));
  }

  public void setContent(List<Component> content) {
    this.content = content;
    this.lines = null;
  }

  public void setTooltipDelay(Duration delay) {
    this.delay = delay;
  }

  public List<FormattedCharSequence> getLines(Minecraft client) {
    if (this.lines == null) {
      this.lines = this.content.stream().flatMap((line) -> Tooltip.splitTooltip(client, line).stream()).toList();
    }
    return this.lines;
  }

  @Override
  public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
    boolean hovered = context.containsPointInScissor(mouseX, mouseY) && mouseX >= this.getX() && mouseY >= this.getY() &&
        mouseX < this.getX() + this.getWidth() && mouseY < this.getY() + this.getHeight();

    if (hovered != this.prevShouldRender) {
      if (hovered) {
        this.renderCheckTime = Util.getMillis();
      }
      this.prevShouldRender = hovered;
    }

    if (hovered && Util.getMillis() - this.renderCheckTime > this.delay.toMillis()) {
      Minecraft client = Minecraft.getInstance();
      context.setTooltipForNextFrame(
          client.font,
          this.getLines(client),
          new MenuTooltipPositioner(this.getRectangle()),
          mouseX,
          mouseY,
          false);
    }
  }

  @Override
  public void visitWidgets(Consumer<AbstractWidget> consumer) {
  }
}
