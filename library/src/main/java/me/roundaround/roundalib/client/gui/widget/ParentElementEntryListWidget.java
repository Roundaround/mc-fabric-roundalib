package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.client.gui.widget.layout.screen.ThreeSectionLayoutWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.navigation.NavigationAxis;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class ParentElementEntryListWidget<E extends ParentElementEntryListWidget.Entry> extends FlowListWidget<E> {
  protected ParentElementEntryListWidget(MinecraftClient client, ThreeSectionLayoutWidget layout) {
    super(client, layout);
  }

  protected ParentElementEntryListWidget(MinecraftClient client, int x, int y, int width, int height) {
    super(client, x, y, width, height);
  }

  @Override
  public void setFocused(Element focused) {
    super.setFocused(focused);
    if (focused == null) {
      this.setSelected(null);
    }
  }

  @Override
  public Selectable.SelectionType getType() {
    return this.isFocused() ? Selectable.SelectionType.FOCUSED : super.getType();
  }

  @Override
  protected boolean isSelectedEntry(int index) {
    return false;
  }

  @Override
  public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
    if (this.getEntryCount() == 0) {
      return null;
    }

    if (!(navigation instanceof GuiNavigation.Arrow arrow)) {
      return super.getNavigationPath(navigation);
    }

    E focused = this.getFocused();

    if (arrow.direction().getAxis() == NavigationAxis.HORIZONTAL && focused != null) {
      return GuiNavigationPath.of(this, focused.getNavigationPath(navigation));
    }

    int index = -1;
    NavigationDirection direction = arrow.direction();

    if (focused != null) {
      index = focused.children().indexOf(focused.getFocused());
    }

    if (index == -1) {
      switch (direction) {
        case LEFT -> {
          index = Integer.MAX_VALUE;
          direction = NavigationDirection.DOWN;
        }
        case RIGHT -> {
          index = 0;
          direction = NavigationDirection.DOWN;
        }
        default -> index = 0;
      }
    }

    E cursor = focused;
    GuiNavigationPath path = null;

    do {
      cursor = this.getNeighboringEntry(direction, (element) -> !element.children().isEmpty(), cursor);
      if (cursor == null) {
        return null;
      }

      path = cursor.getNavigationPath(arrow, index);
    } while (path == null);

    return GuiNavigationPath.of(this, path);
  }

  @Override
  public void appendClickableNarrations(NarrationMessageBuilder builder) {
    E hovered = this.getHoveredEntry();
    if (hovered != null) {
      hovered.appendNarrations(builder.nextMessage());
      this.appendNarrations(builder, hovered);
    } else {
      E focused = this.getFocused();
      if (focused != null) {
        focused.appendNarrations(builder.nextMessage());
        this.appendNarrations(builder, focused);
      }
    }

    builder.put(NarrationPart.USAGE, Text.translatable("narration.component_list.usage"));
  }

  @Environment(EnvType.CLIENT)
  public static abstract class Entry extends FlowListWidget.Entry implements ParentElement {
    private final ArrayList<Element> children = new ArrayList<>();
    private final ArrayList<Selectable> selectables = new ArrayList<>();

    private Element focused;
    private Selectable focusedSelectable;
    private boolean dragging;

    public Entry(int index, int left, int top, int width, int contentHeight) {
      super(index, left, top, width, contentHeight);
    }

    protected <T extends Selectable> T addSelectable(T selectable) {
      this.selectables.add(selectable);
      return selectable;
    }

    protected <T extends Element & Selectable> T addSelectableChild(T child) {
      this.children.add(child);
      this.selectables.add(child);
      return child;
    }

    protected <T extends Drawable & Element & Selectable> T addDrawableChild(T child) {
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
    public List<? extends Element> children() {
      return this.children;
    }

    public List<? extends Selectable> selectableChildren() {
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return ParentElement.super.mouseClicked(mouseX, mouseY, button);
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
    public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
      if (!(navigation instanceof GuiNavigation.Arrow arrow)) {
        return ParentElement.super.getNavigationPath(navigation);
      }

      int delta = switch (arrow.direction()) {
        case LEFT -> -1;
        case RIGHT -> 1;
        default -> 0;
      };

      if (delta == 0) {
        return null;
      }

      int index = MathHelper.clamp(delta + this.children().indexOf(this.getFocused()), 0, this.children().size() - 1);

      for (int i = index; i >= 0 && i < this.children().size(); i += delta) {
        GuiNavigationPath path = this.children().get(i).getNavigationPath(navigation);
        if (path != null) {
          return GuiNavigationPath.of(this, path);
        }
      }

      return ParentElement.super.getNavigationPath(navigation);
    }

    public GuiNavigationPath getNavigationPath(GuiNavigation navigation, int index) {
      if (this.children().isEmpty()) {
        return null;
      }

      Element child = this.children().get(Math.min(index, this.children().size() - 1));
      GuiNavigationPath path = child.getNavigationPath(navigation);
      return GuiNavigationPath.of(this, path);
    }

    public void appendNarrations(NarrationMessageBuilder builder) {
      List<? extends Selectable> list = this.selectableChildren();
      Screen.SelectedElementNarrationData data = Screen.findSelectedElementData(list, this.focusedSelectable);

      if (data != null) {
        if (data.selectType.isFocused()) {
          this.focusedSelectable = data.selectable;
        }

        if (list.size() > 1) {
          builder.put(NarrationPart.POSITION,
              Text.translatable("narrator.position.object_list", data.index + 1, list.size())
          );
          if (data.selectType == Selectable.SelectionType.FOCUSED) {
            builder.put(NarrationPart.USAGE, Text.translatable("narration.component_list.usage"));
          }
        }

        data.selectable.appendNarrations(builder.nextMessage());
      }
    }
  }
}
