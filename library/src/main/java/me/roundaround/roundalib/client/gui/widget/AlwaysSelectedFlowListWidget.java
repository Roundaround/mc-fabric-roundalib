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
import net.minecraft.util.Colors;

@Environment(EnvType.CLIENT)
public abstract class AlwaysSelectedFlowListWidget<E extends AlwaysSelectedFlowListWidget.Entry> extends FlowListWidget<E> {
  protected static final Text SELECTION_USAGE_TEXT = Text.translatable("narration.selection.usage");

  private boolean highlightSelection = true;

  protected AlwaysSelectedFlowListWidget(MinecraftClient client, ThreePartsLayoutWidget layout) {
    super(client, layout);
    this.setDefaultPaddingAndSpacing();
  }

  protected AlwaysSelectedFlowListWidget(MinecraftClient client, int x, int y, int width, int height) {
    super(client, x, y, width, height);
    this.setDefaultPaddingAndSpacing();
  }

  protected void setDefaultPaddingAndSpacing() {
    this.setContentPadding(2);
    this.setRowSpacing(3);
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
    boolean renderSelected = this.highlightSelection && entry == this.getSelected();
    if (renderSelected) {
      this.drawSelectionBackground(context, entry);
    }
    super.renderEntry(context, mouseX, mouseY, delta, entry);
    if (renderSelected) {
      this.drawSelectionHighlight(context, entry);
    }
  }

  protected void drawSelectionBackground(DrawContext context, E entry) {
    context.fill(entry.getX(), entry.getY(), entry.getRight(), entry.getBottom(), Colors.BLACK);
  }

  protected void drawSelectionHighlight(DrawContext context, E entry) {
    context.drawBorder(
        entry.getX(), entry.getY(), entry.getWidth(), entry.getHeight(), this.isFocused() ? Colors.WHITE : Colors.GRAY);
  }

  protected void highlightSelection(boolean highlightSelection) {
    this.highlightSelection = highlightSelection;
  }

  @Environment(EnvType.CLIENT)
  public static abstract class Entry extends FlowListWidget.Entry {
    private boolean focused;

    public Entry(int index, int left, int top, int width, int height) {
      super(index, left, top, width, height);

      this.setMargin(DEFAULT_MARGIN.expand(2));
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
    }

    @Override
    public Element getFocused() {
      return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return true;
    }
  }
}
