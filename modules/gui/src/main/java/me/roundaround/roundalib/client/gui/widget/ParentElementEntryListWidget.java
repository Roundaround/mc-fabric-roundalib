package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.client.gui.layout.screen.ThreeSectionLayoutWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class ParentElementEntryListWidget<E extends ParentElementEntryListWidget.Entry> extends FlowListWidget<E> {
  protected ParentElementEntryListWidget(Minecraft client, ThreeSectionLayoutWidget layout) {
    super(client, layout);
  }

  protected ParentElementEntryListWidget(Minecraft client, int x, int y, int width, int height) {
    super(client, x, y, width, height);
  }

  @Override
  public void setFocused(GuiEventListener focused) {
    super.setFocused(focused);
    if (focused == null) {
      this.setSelected(null);
    }
  }

  @Override
  public NarratableEntry.NarrationPriority narrationPriority() {
    return this.isFocused() ? NarratableEntry.NarrationPriority.FOCUSED : super.narrationPriority();
  }

  @Override
  protected boolean isSelectedEntry(int index) {
    return false;
  }

  @Override
  public ComponentPath nextFocusPath(FocusNavigationEvent navigation) {
    if (this.getEntryCount() == 0) {
      return null;
    }

    if (!(navigation instanceof FocusNavigationEvent.ArrowNavigation arrow)) {
      return super.nextFocusPath(navigation);
    }

    E focused = this.getFocused();

    if (arrow.direction().getAxis() == ScreenAxis.HORIZONTAL && focused != null) {
      return ComponentPath.path(this, focused.nextFocusPath(navigation));
    }

    int index = -1;
    ScreenDirection direction = arrow.direction();

    if (focused != null) {
      index = focused.children().indexOf(focused.getFocused());
    }

    if (index == -1) {
      switch (direction) {
        case LEFT -> {
          index = Integer.MAX_VALUE;
          direction = ScreenDirection.DOWN;
        }
        case RIGHT -> {
          index = 0;
          direction = ScreenDirection.DOWN;
        }
        default -> index = 0;
      }
    }

    E cursor = focused;
    ComponentPath path = null;

    do {
      cursor = this.getNeighboringEntry(direction, (element) -> !element.children().isEmpty(), cursor);
      if (cursor == null) {
        return null;
      }

      path = cursor.getNavigationPath(arrow, index);
    } while (path == null);

    return ComponentPath.path(this, path);
  }

  @Override
  public void updateWidgetNarration(NarrationElementOutput builder) {
    E hovered = this.getHoveredEntry();
    if (hovered != null) {
      hovered.appendNarrations(builder.nest());
      this.appendNarrations(builder, hovered);
    } else {
      E focused = this.getFocused();
      if (focused != null) {
        focused.appendNarrations(builder.nest());
        this.appendNarrations(builder, focused);
      }
    }

    builder.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
  }

  @Environment(EnvType.CLIENT)
  public static abstract class Entry extends FlowListWidget.Entry implements ContainerEventHandler {
    private final ArrayList<GuiEventListener> children = new ArrayList<>();
    private final ArrayList<NarratableEntry> selectables = new ArrayList<>();

    private GuiEventListener focused;
    private NarratableEntry focusedSelectable;
    private boolean dragging;

    public Entry(int index, int left, int top, int width, int contentHeight) {
      super(index, left, top, width, contentHeight);
    }

    protected <T extends NarratableEntry> T addSelectable(T selectable) {
      this.selectables.add(selectable);
      return selectable;
    }

    protected <T extends GuiEventListener & NarratableEntry> T addSelectableChild(T child) {
      this.children.add(child);
      this.selectables.add(child);
      return child;
    }

    protected <T extends Renderable & GuiEventListener & NarratableEntry> T addDrawableChild(T child) {
      this.children.add(child);
      this.drawables.add(child);
      this.selectables.add(child);
      return child;
    }

    @Override
    protected void clearChildren() {
      super.clearChildren();
      this.children.clear();
      this.selectables.clear();
    }

    @Override
    public List<? extends GuiEventListener> children() {
      return this.children;
    }

    public List<? extends NarratableEntry> selectableChildren() {
      return this.selectables;
    }

    @Override
    public boolean isDragging() {
      return this.dragging;
    }

    @Override
    public void setDragging(boolean dragging) {
      this.dragging = dragging;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
      return ContainerEventHandler.super.mouseClicked(click, doubled);
    }

    @Override
    public void setFocused(GuiEventListener focused) {
      if (this.focused != null) {
        this.focused.setFocused(false);
      }

      if (focused != null) {
        focused.setFocused(true);
      }

      this.focused = focused;
    }

    @Override
    public GuiEventListener getFocused() {
      return this.focused;
    }

    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent navigation) {
      if (!(navigation instanceof FocusNavigationEvent.ArrowNavigation(ScreenDirection direction))) {
        return ContainerEventHandler.super.nextFocusPath(navigation);
      }

      int delta = switch (direction) {
        case LEFT -> -1;
        case RIGHT -> 1;
        default -> 0;
      };

      if (delta == 0) {
        return null;
      }

      int index = Mth.clamp(delta + this.children().indexOf(this.getFocused()), 0, this.children().size() - 1);

      for (int i = index; i >= 0 && i < this.children().size(); i += delta) {
        ComponentPath path = this.children().get(i).nextFocusPath(navigation);
        if (path != null) {
          return ComponentPath.path(this, path);
        }
      }

      return ContainerEventHandler.super.nextFocusPath(navigation);
    }

    public ComponentPath getNavigationPath(FocusNavigationEvent navigation, int index) {
      if (this.children().isEmpty()) {
        return null;
      }

      GuiEventListener child = this.children().get(Math.min(index, this.children().size() - 1));
      ComponentPath path = child.nextFocusPath(navigation);
      return ComponentPath.path(this, path);
    }

    public void appendNarrations(NarrationElementOutput builder) {
      List<? extends NarratableEntry> list = this.selectableChildren();
      Screen.NarratableSearchResult data = Screen.findNarratableWidget(list, this.focusedSelectable);

      if (data != null) {
        if (data.priority().isTerminal()) {
          this.focusedSelectable = data.entry();
        }

        if (list.size() > 1) {
          builder.add(
              NarratedElementType.POSITION,
              Component.translatable("narrator.position.object_list", data.index() + 1, list.size())
          );
          if (data.priority() == NarratableEntry.NarrationPriority.FOCUSED) {
            builder.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
          }
        }

        data.entry().updateNarration(builder.nest());
      }
    }
  }
}
