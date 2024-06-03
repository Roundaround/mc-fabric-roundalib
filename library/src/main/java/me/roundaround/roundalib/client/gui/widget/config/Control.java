package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.Positional;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class Control<D, O extends ConfigOption<D, ?>> extends Positional implements ParentElement {
  protected static final int PADDING = 1;

  protected final MinecraftClient client;
  protected final O option;

  protected boolean valid;
  protected boolean disabled;

  private double renderOffset;
  private Element focused;

  protected Control(MinecraftClient client, O option, int left, int top, int width, int height) {
    super(left, top, width, height);

    this.client = client;
    this.option = option;

    Screen screen = Objects.requireNonNull(client.currentScreen);
    this.option.subscribeToValueChanges(screen.hashCode(), this::onValueChanged);

    this.disabled = this.option.isDisabled();
  }

  @Override
  public void setDragging(boolean dragging) {
    // TODO: Do I need dragging behavior?
  }

  @Override
  public boolean isDragging() {
    // TODO: Do I need dragging behavior?
    return false;
  }

  @Override
  public void setFocused(Element focused) {
    if (this.focused != null) {
      this.focused.setFocused(false);
    }

    if (focused != null) {
      focused.setFocused(true);
    }

    this.focused = focused;
  }

  @Override
  public Element getFocused() {
    return this.focused;
  }

  @Override
  public void forEachElement(Consumer<Widget> consumer) {
    this.children()
        .stream()
        .filter((child) -> child instanceof Widget)
        .forEach((child) -> consumer.accept((Widget) child));
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

  public void setRenderOffset(double renderOffset) {
    this.renderOffset = renderOffset;
  }

  public double getRenderOffset() {
    return this.renderOffset;
  }

  public void tick() {
  }

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

  protected int getWidgetLeft() {
    return this.getLeft() + PADDING;
  }

  protected int getWidgetTop() {
    return this.getTop() + PADDING;
  }

  protected int getWidgetRight() {
    return this.getRight() - PADDING;
  }

  protected int getWidgetBottom() {
    return this.getBottom() - PADDING;
  }

  protected int getWidgetWidth() {
    return this.getWidth() - 2 * PADDING;
  }

  protected int getWidgetHeight() {
    return this.getHeight() - 2 * PADDING;
  }

  @FunctionalInterface
  public interface ControlFactory<D, O extends ConfigOption<D, ?>> {
    Control<D, O> create(MinecraftClient client, O option, int left, int top, int width, int height);
  }
}
