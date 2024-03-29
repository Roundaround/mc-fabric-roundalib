package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

import java.util.Objects;

public abstract class Control<D, O extends ConfigOption<D, ?>> extends AbstractParentElement {
  protected static final int PADDING = 1;
  protected static final int WIDGET_MIN_WIDTH = 100;

  protected final ConfigListWidget.OptionEntry<D, O> parent;
  protected final O option;
  protected final int widgetLeft;
  protected final int widgetTop;
  protected final int widgetWidth;
  protected final int widgetHeight;

  protected int scrolledTop;
  protected boolean valid;
  protected boolean disabled;

  protected Control(ConfigListWidget.OptionEntry<D, O> parent) {
    this.parent = parent;
    this.option = parent.getOption();

    Screen screen = Objects.requireNonNull(parent.getClient().currentScreen);
    this.option.subscribeToValueChanges(screen.hashCode(), this::valueChanged);

    this.widgetWidth = Math.max(WIDGET_MIN_WIDTH, Math.round(parent.getWidth() * 0.3f));
    this.widgetLeft = parent.getControlRight() - this.widgetWidth;
    this.widgetHeight = parent.getHeight() - PADDING * 2;
    this.widgetTop = parent.getTop() + PADDING;
    this.scrolledTop = this.widgetTop;

    this.disabled = this.option.isDisabled();
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

  public void setScrollAmount(double amount) {
    this.scrolledTop = this.widgetTop - (int) amount;
  }

  private void valueChanged(D prev, D curr) {
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
