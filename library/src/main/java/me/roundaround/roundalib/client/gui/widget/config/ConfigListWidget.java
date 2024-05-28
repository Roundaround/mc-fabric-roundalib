package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.RoundaLib;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import me.roundaround.roundalib.client.gui.widget.VariableHeightListWidget;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.stream.Stream;

public class ConfigListWidget extends VariableHeightListWidget<ConfigListWidget.Entry> {
  private static final int LIST_MIN_WIDTH = 400;

  public ConfigListWidget(MinecraftClient client, ThreePartsLayoutWidget layout, ModConfig modConfig) {
    super(client, layout.getX(), layout.getHeaderHeight(), Math.round(Math.min(Math.max(LIST_MIN_WIDTH, layout.getWidth() / 1.5f), layout.getWidth())), layout.getContentHeight());

    for (var entry : modConfig.getConfigOptions().entrySet()) {
      if (entry.getValue().stream().noneMatch(ConfigOption::shouldShowInConfigScreen)) {
        continue;
      }

      String modId = modConfig.getModId();
      String category = entry.getKey();
      if (modConfig.getShowGroupTitles() && !category.equals(modId)) {
        this.addEntry(new CategoryEntry(this.client, Text.translatable(entry.getKey() + ".title"), this.getContentLeft(), this.getNextEntryY(), this.getContentWidth()));
      }

      for (var option : entry.getValue()) {
        if (!option.shouldShowInConfigScreen()) {
          continue;
        }

        try {
          this.addEntry(new OptionEntry<>(this.client, option, this.getContentLeft(), this.getNextEntryY(), this.getContentWidth()));
        } catch (ControlRegistry.NotRegisteredException e) {
          RoundaLib.LOGGER.error("Failed to create control for config option: {}", option, e);
        }
      }
    }
  }

  public void updatePosition(ThreePartsLayoutWidget layout) {
    super.updatePosition(layout.getX(), layout.getHeaderHeight(), Math.round(Math.min(Math.max(LIST_MIN_WIDTH, layout.getWidth() / 1.5f), layout.getWidth())), layout.getContentHeight());
  }

  public void tick() {
    this.forEachEntry(Entry::tick);
  }

  public abstract static class Entry extends VariableHeightListWidget.Entry {
    protected Entry(MinecraftClient client, int x, int y, int width, int height) {
      super(client, x, y, width, height);
    }

    public void tick() {
    }
  }

  public static class CategoryEntry extends Entry {
    protected static final int HEIGHT = 20;

    protected final LabelWidget labelWidget;

    protected CategoryEntry(MinecraftClient client, Text label, int x, int y, int width) {
      super(client, x, y, width, HEIGHT);

      this.labelWidget = LabelWidget.builder(client, label, (this.getX() + this.getRight()) / 2, this.getY() + this.getHeight() / 2).justifiedCenter().alignedMiddle().shiftForPadding().showTextShadow().hideBackground().build();
    }

    @Override
    public List<? extends Element> children() {
      return List.of();
    }

    @Override
    public void renderContent(DrawContext drawContext, int mouseX, int mouseY, float delta) {
      this.labelWidget.setPosY(this.getY() + this.getHeight() / 2 - (int) this.getScrollAmount());
      this.labelWidget.render(drawContext, mouseX, mouseY, delta);
    }
  }

  public static class OptionEntry<D, O extends ConfigOption<D, ?>> extends Entry {
    protected static final int HEIGHT = 20;

    protected final O option;
    protected final Control<D, O> control;
    protected final LabelWidget labelWidget;
    protected final IconButtonWidget resetButton;

    protected OptionEntry(MinecraftClient client, O configOption, int x, int y, int width) throws ControlRegistry.NotRegisteredException {
      super(client, x, y, width, HEIGHT);

      this.option = configOption;
      this.control = ControlRegistry.getControlFactory(configOption).create(this);
      this.labelWidget = LabelWidget.builder(client, configOption.getLabel(), this.getX() + GuiUtil.PADDING, this.getY() + this.getHeight() / 2).justifiedLeft().alignedMiddle().shiftForPadding().showTextShadow().hideBackground().build();
      this.resetButton = RoundaLibIconButtons.resetButton(this.getControlRight() + GuiUtil.PADDING, this.getY() + (this.getHeight() - RoundaLibIconButtons.SIZE_L) / 2, this.option, RoundaLibIconButtons.SIZE_L);
    }

    public O getOption() {
      return this.option;
    }

    public int getControlRight() {
      return this.getRight() - RoundaLibIconButtons.SIZE_L - 2 * GuiUtil.PADDING;
    }

    @Override
    public List<? extends Element> children() {
      return Stream.of(this.control.children(), List.of(this.resetButton)).flatMap(List::stream).toList();
    }

    protected List<? extends Element> navigableChildren() {
      return this.children().stream().filter(element -> {
        if (element instanceof PressableWidget pressable) {
          return pressable.active;
        }
        return true;
      }).toList();
    }

    @Override
    public GuiNavigationPath getNavigationPath(GuiNavigation navigation, int index) {
      List<? extends Element> navigableChildren = this.navigableChildren();
      if (navigableChildren.isEmpty()) {
        return null;
      }

      Element child = navigableChildren.get(Math.min(index, navigableChildren.size() - 1));
      GuiNavigationPath path = child.getNavigationPath(navigation);
      return GuiNavigationPath.of(this, path);
    }

    @Override
    public void tick() {
      this.control.tick();
    }

    @Override
    public void renderContent(DrawContext drawContext, int mouseX, int mouseY, float delta) {
      this.labelWidget.setPosY(this.getY() + this.getHeight() / 2 - (int) this.getScrollAmount());
      this.labelWidget.render(drawContext, mouseX, mouseY, delta);

      this.control.updateBounds(this.getScrollAmount());
      this.control.renderWidget(drawContext, mouseX, mouseY, delta);

      this.resetButton.setY(this.getY() + (this.getHeight() - RoundaLibIconButtons.SIZE_L) / 2 - (int) this.getScrollAmount());
      this.resetButton.render(drawContext, mouseX, mouseY, delta);
    }
  }
}
