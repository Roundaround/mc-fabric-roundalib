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
    protected Entry(
        MinecraftClient client, VariableHeightListWidget<Entry> parent, int height) {
      super(client, parent, height);
    }
  }

  protected static class CategoryEntry extends Entry {
    protected static final int HEIGHT = 20;

    protected final LabelWidget labelWidget;

    protected CategoryEntry(
        MinecraftClient client, VariableHeightListWidget<Entry> parent, Text label) {
      super(client, parent, HEIGHT);

      this.labelWidget = LabelWidget.builder(client,
              label,
              this.left + GuiUtil.PADDING,
              this.top + this.height / 2)
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
    public void setTop(int top) {
      super.setTop(top);
      this.labelWidget.setPosY(this.top + this.height / 2);
    }

    @Override
    public void renderContent(
        MatrixStack matrixStack, int index, int mouseX, int mouseY, float delta) {
      this.labelWidget.render(matrixStack, mouseX, mouseY, delta);
    }
  }

  protected static class OptionEntry extends Entry {
    protected static final int HEIGHT = 20;
    protected static final int PADDING = 4;
    protected static final int CONTROL_MIN_WIDTH = 100;
    protected static final int HIGHLIGHT_COLOR = 0x30FFFFFF;

    protected final ConfigOption<?, ?> configOption;
    protected final LabelWidget labelWidget;

    protected OptionEntry(
        MinecraftClient client,
        VariableHeightListWidget<Entry> parent,
        ConfigOption<?, ?> configOption) {
      super(client, parent, HEIGHT);

      this.configOption = configOption;
      this.labelWidget = LabelWidget.builder(client,
              configOption.getLabel(),
              this.left + GuiUtil.PADDING,
              this.top + this.height / 2)
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
    public void setTop(int top) {
      super.setTop(top);
      this.labelWidget.setPosY(this.top + this.height / 2);
    }

    @Override
    public void renderContent(
        MatrixStack matrixStack, int index, int mouseX, int mouseY, float delta) {
      this.labelWidget.render(matrixStack, mouseX, mouseY, delta);
    }
  }
}
