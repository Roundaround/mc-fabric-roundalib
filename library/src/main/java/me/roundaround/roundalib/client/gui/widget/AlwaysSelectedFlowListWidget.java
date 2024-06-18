package me.roundaround.roundalib.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class AlwaysSelectedFlowListWidget<E extends AlwaysSelectedFlowListWidget.Entry> extends FlowListWidget<E> {
  protected static final Text SELECTION_USAGE_TEXT = Text.translatable("narration.selection.usage");

  private boolean highlightSelection = true;

  protected AlwaysSelectedFlowListWidget(MinecraftClient client, ThreePartsLayoutWidget layout) {
    super(client, layout);

    this.setContentPadding(2);
  }

  protected AlwaysSelectedFlowListWidget(MinecraftClient client, int x, int y, int width, int height) {
    super(client, x, y, width, height);
  }

  @Override
  public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
    if (this.getEntryCount() == 0) {
      return null;
    }

    if (this.isFocused()) {
      if (!(navigation instanceof GuiNavigation.Arrow arrow)) {
        return null;
      }

      E neighbor = this.getNeighboringEntry(arrow.direction());
      if (neighbor != null) {
        return GuiNavigationPath.of(this, GuiNavigationPath.of(neighbor));
      }

      return null;
    }

    E reference = this.getSelected();
    if (reference == null) {
      reference = this.getNeighboringEntry(navigation.getDirection());
    }
    if (reference == null) {
      return null;
    }
    return GuiNavigationPath.of(this, GuiNavigationPath.of(reference));
  }

  @Override
  public void appendClickableNarrations(NarrationMessageBuilder builder) {
    E hovered = this.getHoveredEntry();

    if (hovered != null) {
      this.appendNarrations(builder, hovered);
      hovered.appendNarrations(builder.nextMessage());
    } else {
      E selected = this.getSelected();
      if (selected != null) {
        this.appendNarrations(builder.nextMessage(), selected);
        selected.appendNarrations(builder);
      }
    }

    if (this.isFocused()) {
      builder.put(NarrationPart.USAGE, SELECTION_USAGE_TEXT);
    }
  }

  @Override
  public void renderDecorations(DrawContext context, int mouseX, int mouseY, float delta) {
    E selectedEntry = this.getSelected();
    if (selectedEntry != null && this.highlightSelection) {
      this.drawSelectionHighlight(context, selectedEntry, this.isFocused() ? -1 : -8355712, -16777216);
    }
  }

  protected void drawSelectionHighlight(DrawContext context, E entry, int borderColor, int fillColor) {
    int left = entry.getLeft();
    int right = entry.getRight();
    int top = entry.getTop();
    int bottom = entry.getBottom();

    context.fill(left, top - 2, right, bottom + 2, borderColor);
    context.fill(left + 1, top - 1, right - 1, bottom + 1, fillColor);
  }

  protected void highlightSelection(boolean highlightSelection) {
    this.highlightSelection = highlightSelection;
  }

  @Environment(EnvType.CLIENT)
  public static abstract class Entry extends FlowListWidget.Entry {
    private boolean focused;

    public Entry(int index, int left, int top, int width, int height) {
      super(index, left, top, width, height);
    }

    public abstract Text getNarration();

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
      builder.put(NarrationPart.TITLE, this.getNarration());
    }

    @Override
    public void setFocused(boolean focused) {
      this.focused = focused;
    }

    @Override
    public boolean isFocused() {
      return this.focused;
    }

    @Override
    public void setFocused(Element focused) {
      this.focused = focused == this;
    }

    @Override
    public Element getFocused() {
      return null;
    }
  }
}
