package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.client.gui.layout.Spacing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

@Environment(EnvType.CLIENT)
public abstract class NarratableEntryListWidget<E extends NarratableEntryListWidget.Entry> extends FlowListWidget<E> {
  protected static final Text SELECTION_USAGE_TEXT = Text.translatable("narration.selection.usage");

  private boolean highlightSelection = true;

  protected NarratableEntryListWidget(MinecraftClient client, ThreePartsLayoutWidget layout) {
    super(client, layout);

    this.contentPadding = Spacing.of(2);
    this.rowSpacing = 3;
  }

  protected NarratableEntryListWidget(MinecraftClient client, int x, int y, int width, int height) {
    super(client, x, y, width, height);

    this.contentPadding = Spacing.of(2);
    this.rowSpacing = 3;
  }

  @Override
  public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
    if (this.getEntryCount() == 0) {
      return null;
    }

    if (this.isFocused() && navigation instanceof GuiNavigation.Arrow arrow) {
      E neighbor = this.getNeighboringEntry(arrow.direction());
      if (neighbor != null) {
        return GuiNavigationPath.of(this, GuiNavigationPath.of(neighbor));
      }

      return null;
    }

    if (!this.isFocused()) {
      E reference = this.getSelected();
      if (reference == null) {
        reference = this.getNeighboringEntry(navigation.getDirection());
      }
      if (reference == null) {
        return null;
      }
      return GuiNavigationPath.of(this, GuiNavigationPath.of(reference));
    }

    return null;
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
  protected void renderEntry(DrawContext context, int mouseX, int mouseY, float delta, E entry) {
    boolean showSelected = this.highlightSelection && entry == this.getSelected();
    if (showSelected) {
      entry.renderSelectionBackground(context);
    }
    super.renderEntry(context, mouseX, mouseY, delta, entry);
    if (showSelected) {
      entry.renderSelectionHighlight(context);
    }
  }

  protected void setHighlightSelection(boolean highlightSelection) {
    this.highlightSelection = highlightSelection;
  }

  @Environment(EnvType.CLIENT)
  public static abstract class Entry extends FlowListWidget.Entry {
    protected static final Spacing DEFAULT_MARGIN = FlowListWidget.Entry.DEFAULT_MARGIN.expand(2);

    private boolean focused;

    public Entry(int index, int left, int top, int width, int contentHeight) {
      super(index, left, top, width, contentHeight);
      this.margin = DEFAULT_MARGIN;
    }

    public abstract Text getNarration();

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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return true;
    }

    protected void renderSelectionBackground(DrawContext context) {
      context.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), Colors.BLACK);
    }

    protected void renderSelectionHighlight(DrawContext context) {
      context.drawBorder(
          this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.isFocused() ? Colors.WHITE : Colors.GRAY);
    }
  }
}
