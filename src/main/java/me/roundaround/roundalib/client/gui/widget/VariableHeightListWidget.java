package me.roundaround.roundalib.client.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public class VariableHeightListWidget<E extends VariableHeightListWidget.Entry<E>>
    extends AbstractParentElement implements Drawable, Selectable {
  private final MinecraftClient client;
  private final int left;
  private final int top;
  private final int width;
  private final int height;

  private E selectedEntry;
  private E hoveredEntry;

  public VariableHeightListWidget(
      MinecraftClient client, int left, int top, int width, int height) {
    this.client = client;
    this.left = left;
    this.top = top;
    this.width = width;
    this.height = height;
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    enableScissor(
        this.left,
        this.top,
        this.left + this.width,
        this.top + this.height);

    this.renderList(matrixStack, mouseX, mouseY, delta);

    disableScissor();
  }

  protected void renderList(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
  }

  @Override
  public void appendNarrations(NarrationMessageBuilder builder) {

  }

  @Override
  public List<? extends Element> children() {
    return List.of();
  }

  @Override
  public SelectionType getType() {
    if (this.isFocused()) {
      return SelectionType.FOCUSED;
    } else {
      return this.hoveredEntry != null ? SelectionType.HOVERED : SelectionType.NONE;
    }
  }

  public abstract static class Entry<E extends Entry<E>> extends AbstractParentElement
      implements Element {
    private final MinecraftClient client;
    private final VariableHeightListWidget<E> parent;
    private final int height;

    public Entry(MinecraftClient client, VariableHeightListWidget<E> parent, int height) {
      this.client = client;
      this.parent = parent;
      this.height = height;
    }

    public void render(
        MatrixStack matrixStack,
        int top,
        int left,
        int width,
        int mouseX,
        int mouseY,
        float delta) {

    }
  }
}
