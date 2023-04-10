package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import me.roundaround.roundalib.client.gui.widget.VariableHeightListWidget;
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

  public ConfigListWidget(MinecraftClient client, int left, int top, int width, int height) {
    super(client, left, top, width, height);
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
    public void setTop(int top) {
      super.setTop(top);
      this.labelWidget.setPosY(this.getTop() + this.getHeight() / 2);
    }

    @Override
    public void renderContent(
        MatrixStack matrixStack, int index, int mouseX, int mouseY, float delta) {
      this.labelWidget.render(matrixStack, mouseX, mouseY, delta);
    }
  }

  protected static class OptionEntry<O extends ConfigOption<?, ?>> extends Entry {
    protected static final int HEIGHT = 20;
    protected static final int PADDING = 4;
    protected static final int CONTROL_MIN_WIDTH = 100;
    protected static final int HIGHLIGHT_COLOR = 0x30FFFFFF;

    protected final Control<O> control;
    protected final LabelWidget labelWidget;

    protected OptionEntry(MinecraftClient client, ConfigListWidget parent, O configOption) {
      super(client, parent, HEIGHT);

      this.control = ControlRegistry.create(parent, configOption);
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
      return this.control.getOption();
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
    public void setTop(int top) {
      super.setTop(top);
      this.labelWidget.setPosY(this.getTop() + this.getHeight() / 2);
    }

    @Override
    public void renderContent(
        MatrixStack matrixStack, int index, int mouseX, int mouseY, float delta) {
      this.labelWidget.render(matrixStack, mouseX, mouseY, delta);
    }
  }
}
