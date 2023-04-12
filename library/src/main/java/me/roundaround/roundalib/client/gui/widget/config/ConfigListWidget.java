package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.RoundaLib;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import me.roundaround.roundalib.client.gui.widget.VariableHeightListWidget;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ConfigListWidget extends VariableHeightListWidget<ConfigListWidget.Entry> {
  private final ArrayList<CategoryEntry> categories = new ArrayList<>();
  private int currentCategory = 0;

  public ConfigListWidget(
      MinecraftClient client, ModConfig modConfig, int left, int top, int width, int height) {
    super(client, left, top, width, height);

    for (var entry : modConfig.getConfigOptions().entrySet()) {
      if (entry.getValue().stream().noneMatch(ConfigOption::shouldShowInConfigScreen)) {
        continue;
      }

      String modId = modConfig.getModId();
      String category = entry.getKey();
      if (modConfig.getShowGroupTitles() && !category.equals(modId)) {
        addCategory(Text.translatable(entry.getKey() + ".title"));
      }

      for (var option : entry.getValue()) {
        if (!option.shouldShowInConfigScreen()) {
          continue;
        }

        try {
          this.addEntry(new OptionEntry<>(this.client, this, option));
        } catch (ControlRegistry.NotRegisteredException e) {
          RoundaLib.LOGGER.error("Failed to create control for config option: " + option, e);
        }
      }
    }
  }

  public void addCategory(Text label) {
    this.categories.add(this.addEntry(new CategoryEntry(this.client, this, label)));
  }

  public void nextCategory() {
    this.currentCategory = (this.currentCategory + 1) % this.categories.size();
    this.ensureVisible(this.categories.get(this.currentCategory));
  }

  protected abstract static class Entry extends VariableHeightListWidget.Entry<Entry> {
    protected Entry(MinecraftClient client, ConfigListWidget parent, int height) {
      super(client, parent, height);
    }
  }

  protected static class CategoryEntry extends Entry {
    protected static final int HEIGHT = 20;

    protected final LabelWidget labelWidget;

    protected CategoryEntry(MinecraftClient client, ConfigListWidget parent, Text label) {
      super(client, parent, HEIGHT);

      this.labelWidget = LabelWidget.builder(client,
              label,
              this.getLeft() + GuiUtil.PADDING,
              this.getTop() + this.getHeight() / 2)
          .justifiedLeft()
          .alignedMiddle()
          .shiftForPadding()
          .showTextShadow()
          .hideBackground()
          .build();
    }

    @Override
    public List<? extends Element> children() {
      return List.of();
    }

    @Override
    public ConfigListWidget getParent() {
      return super.getParent();
    }

    @Override
    public void renderContent(
        MatrixStack matrixStack,
        int index,
        double scrollAmount,
        int mouseX,
        int mouseY,
        float delta) {
      this.labelWidget.setPosY(this.getTop() + this.getHeight() / 2 - (int) scrollAmount);
      this.labelWidget.render(matrixStack, mouseX, mouseY, delta);
    }
  }

  protected static class OptionEntry<O extends ConfigOption<?, ?>> extends Entry {
    protected static final int HEIGHT = 20;

    protected final O option;
    protected final Control<O> control;
    protected final LabelWidget labelWidget;

    protected OptionEntry(MinecraftClient client, ConfigListWidget parent, O configOption)
        throws ControlRegistry.NotRegisteredException {
      super(client, parent, HEIGHT);

      this.option = configOption;
      this.control = ControlRegistry.getControlFactory(configOption).create(this);
      this.labelWidget = LabelWidget.builder(client,
              configOption.getLabel(),
              this.getLeft() + GuiUtil.PADDING,
              this.getTop() + this.getHeight() / 2)
          .justifiedLeft()
          .alignedMiddle()
          .shiftForPadding()
          .showTextShadow()
          .hideBackground()
          .build();
    }

    public O getOption() {
      return this.option;
    }

    @Override
    public List<? extends Element> children() {
      return this.control.children();
    }

    @Override
    public ConfigListWidget getParent() {
      return super.getParent();
    }

    @Override
    public void renderContent(
        MatrixStack matrixStack, int index, double scrollAmount, int mouseX, int mouseY, float delta) {
      this.labelWidget.setPosY(this.getTop() + this.getHeight() / 2 - (int) scrollAmount);
      this.labelWidget.render(matrixStack, mouseX, mouseY, delta);

      this.control.setScrollAmount(scrollAmount);
      this.control.renderWidget(matrixStack, mouseX, mouseY, delta);
    }
  }
}
