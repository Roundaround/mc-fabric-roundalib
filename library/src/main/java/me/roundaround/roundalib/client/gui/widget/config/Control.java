package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.widget.PositionalWidget;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.widget.Widget;

import java.util.function.Consumer;

public abstract class Control<D, O extends ConfigOption<D>> extends PositionalWidget implements ParentElement {
  protected final MinecraftClient client;
  protected final O option;

  protected boolean valid;

  private Element focused;
  private boolean dragging;

  protected Control(MinecraftClient client, O option, int left, int top, int width, int height) {
    super(left, top, width, height);

    this.client = client;
    this.option = option;
  }

  @Override
  public void setDragging(boolean dragging) {
    this.dragging = dragging;
  }

  @Override
  public boolean isDragging() {
    return this.dragging;
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

  protected void update() {
  }

  @FunctionalInterface
  public interface ControlFactory<D, O extends ConfigOption<D>> {
    Control<D, O> create(MinecraftClient client, O option, int left, int top, int width, int height);
  }
}
