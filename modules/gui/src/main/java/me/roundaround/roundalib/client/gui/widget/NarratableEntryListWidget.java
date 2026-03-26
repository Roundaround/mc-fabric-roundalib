package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.client.gui.layout.screen.ThreeSectionLayoutWidget;
import me.roundaround.roundalib.client.gui.util.Spacing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

@Environment(EnvType.CLIENT)
public abstract class NarratableEntryListWidget<E extends NarratableEntryListWidget.Entry> extends FlowListWidget<E> {
  protected static final Component SELECTION_USAGE_TEXT = Component.translatable("narration.selection.usage");

  private boolean highlightSelection = true;
  private boolean highlightHover = true;
  private boolean highlightSelectionDuringHover = false;

  protected NarratableEntryListWidget(Minecraft client, ThreeSectionLayoutWidget layout) {
    super(client, layout);

    this.contentPadding = Spacing.of(2);
    this.rowSpacing = 3;
  }

  protected NarratableEntryListWidget(Minecraft client, int x, int y, int width, int height) {
    super(client, x, y, width, height);

    this.contentPadding = Spacing.of(2);
    this.rowSpacing = 3;
  }

  @Override
  public boolean keyPressed(KeyEvent input) {
    Entry selected = this.getSelected();
    return selected != null && selected.keyPressed(input) || super.keyPressed(input);
  }

  @Override
  public ComponentPath nextFocusPath(FocusNavigationEvent navigation) {
    if (this.getEntryCount() == 0) {
      return null;
    }

    if (this.isFocused() && navigation instanceof FocusNavigationEvent.ArrowNavigation(ScreenDirection direction)) {
      E neighbor = this.getNeighboringEntry(direction);
      if (neighbor != null) {
        return ComponentPath.path(this, ComponentPath.leaf(neighbor));
      }

      return null;
    }

    if (!this.isFocused()) {
      E reference = this.getSelected();
      if (reference == null) {
        reference = this.getNeighboringEntry(navigation.getVerticalDirectionForInitialFocus());
      }
      if (reference == null) {
        return null;
      }
      return ComponentPath.path(this, ComponentPath.leaf(reference));
    }

    return null;
  }

  @Override
  public void updateWidgetNarration(NarrationElementOutput builder) {
    E hovered = this.getHoveredEntry();

    if (hovered != null) {
      this.appendNarrations(builder, hovered);
      hovered.appendNarrations(builder.nest());
    } else {
      E selected = this.getSelected();
      if (selected != null) {
        this.appendNarrations(builder.nest(), selected);
        selected.appendNarrations(builder);
      }
    }

    if (this.isFocused()) {
      builder.add(NarratedElementType.USAGE, SELECTION_USAGE_TEXT);
    }
  }

  @Override
  protected void renderEntry(GuiGraphics context, int mouseX, int mouseY, float delta, E entry) {
    boolean noHovers = !this.highlightHover || this.getHoveredEntry() == null;
    boolean renderHover = this.highlightHover && entry == this.getHoveredEntry();
    boolean renderSelection =
        this.highlightSelection && entry == this.getSelected() && (noHovers || this.highlightSelectionDuringHover);

    if (renderHover) {
      entry.renderHoverBackground(context);
    }
    if (renderSelection) {
      entry.renderSelectionBackground(context);
    }

    super.renderEntry(context, mouseX, mouseY, delta, entry);

    if (renderHover) {
      entry.renderHoverHighlight(context);
    }
    if (renderSelection) {
      entry.renderSelectionHighlight(context);
    }
  }

  protected void setShouldHighlightSelection(boolean highlightSelection) {
    this.highlightSelection = highlightSelection;
  }

  protected void setShouldHighlightHover(boolean highlightHover) {
    this.highlightHover = highlightHover;
  }

  protected void setShouldHighlightSelectionDuringHover(boolean highlightSelectionDuringHover) {
    this.highlightSelectionDuringHover = highlightSelectionDuringHover;
  }

  @Environment(EnvType.CLIENT)
  public static abstract class Entry extends FlowListWidget.Entry {
    protected static final Spacing DEFAULT_MARGIN = FlowListWidget.Entry.DEFAULT_MARGIN.expand(2);

    private boolean focused;

    public Entry(int index, int left, int top, int width, int contentHeight) {
      super(index, left, top, width, contentHeight);
      this.margin = DEFAULT_MARGIN;
    }

    public abstract Component getNarration();

    public void appendNarrations(NarrationElementOutput builder) {
      builder.add(NarratedElementType.TITLE, this.getNarration());
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
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      return true;
    }

    protected void renderHoverBackground(GuiGraphics context) {
      context.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), CommonColors.BLACK);
    }

    protected void renderHoverHighlight(GuiGraphics context) {
      context.renderOutline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), CommonColors.LIGHT_GRAY);
    }

    protected void renderSelectionBackground(GuiGraphics context) {
      context.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), CommonColors.BLACK);
    }

    protected void renderSelectionHighlight(GuiGraphics context) {
      context.renderOutline(
          this.getX(),
          this.getY(),
          this.getWidth(),
          this.getHeight(),
          this.isFocused() ? CommonColors.WHITE : CommonColors.GRAY
      );
    }
  }
}
