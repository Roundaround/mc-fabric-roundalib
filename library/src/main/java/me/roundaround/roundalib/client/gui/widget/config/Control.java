package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;

import java.util.List;
import java.util.Objects;

public abstract class Control<D, O extends ConfigOption<D, ?>> extends AbstractParentElement {
  protected static final int PADDING = 1;
  protected static final int WIDGET_MIN_WIDTH = 100;

  protected final MinecraftClient client;
  protected final O option;

  protected int widgetX;
  protected int widgetY;
  protected int widgetWidth;
  protected int widgetHeight;
  protected int scrolledTop;
  protected boolean valid;
  protected boolean disabled;

  protected Control(MinecraftClient client, O option) {
    this.client = client;
    this.option = option;

    Screen screen = Objects.requireNonNull(client.currentScreen);
    this.option.subscribeToValueChanges(screen.hashCode(), this::onValueChanged);

    this.disabled = this.option.isDisabled();
  }

  public List<? extends Selectable> selectableChildren() {
    return this.children()
        .stream()
        .filter((child) -> child instanceof Selectable)
        .map((child) -> (Selectable) child)
        .toList();
  }

  public O getOption() {
    return this.option;
  }

  public boolean isValid() {
    return this.valid;
  }

  public void markValid() {
    this.valid = true;
  }

  public void markInvalid() {
    this.valid = false;
  }

  public void tick() {
  }

  public void renderWidget(DrawContext drawContext, int mouseX, int mouseY, float delta) {
  }

  public void setBounds(int right, int y, int width, int height, double scrollAmount) {
    this.widgetWidth = Math.max(WIDGET_MIN_WIDTH, Math.round(width * 0.3f));
    this.widgetX = right - this.widgetWidth;
    this.widgetHeight = height - PADDING * 2;
    this.widgetY = y + PADDING;
    this.scrolledTop = this.widgetY - (int) scrollAmount;

    this.onBoundsChanged();
  }

  public void onBoundsChanged() {}

  private void onValueChanged(D prev, D curr) {
    boolean previousDisabled = this.disabled;
    this.disabled = this.option.isDisabled();
    this.onDisabledChange(previousDisabled, this.disabled);

    this.onConfigValueChange(prev, curr);
  }

  protected void onConfigValueChange(D prev, D curr) {
  }

  protected void onDisabledChange(boolean prev, boolean curr) {
  }
}
