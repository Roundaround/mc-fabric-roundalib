package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.util.math.MatrixStack;

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

  protected Control(ConfigListWidget.OptionEntry<D, O> parent) {
    this.parent = parent;
    this.option = parent.getOption();
    this.option.subscribeToValueChanges(this::onConfigValueChange);

    this.widgetWidth = Math.max(WIDGET_MIN_WIDTH, Math.round(parent.getWidth() * 0.3f));
    this.widgetLeft = parent.getControlRight() - this.widgetWidth;
    this.widgetHeight = parent.getHeight() - PADDING * 2;
    this.widgetTop = parent.getTop() + PADDING;
    this.scrolledTop = this.widgetTop;
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

  public boolean isDisabled() {
    return this.option.isDisabled();
  }

  public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
  }

  public void setScrollAmount(double amount) {
    this.scrolledTop = this.widgetTop - (int) amount;
  }

  protected void onConfigValueChange(D prev, D curr) {
  }
}
