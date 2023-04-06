package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import me.roundaround.roundalib.client.gui.widget.VariableHeightListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;

public class ConfigListWidget {

  protected abstract static class Entry extends VariableHeightListWidget.Entry<Entry> {
    protected Entry(
        MinecraftClient client, VariableHeightListWidget<Entry> parent, int top, int height) {
      super(client, parent, top, height);
    }
  }

  protected static class CategoryEntry extends Entry {
    private static final int HEIGHT = 20;
    private final LabelWidget labelWidget;

    protected CategoryEntry(
        MinecraftClient client, VariableHeightListWidget<Entry> parent, int top, Text label) {
      super(client, parent, top, HEIGHT);

      this.labelWidget = LabelWidget.builder(client, label, 0, this.top + this.height / 2)
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
    public void render(MatrixStack matrixStack, int left, int width, int mouseX, int mouseY, float delta) {
      this.labelWidget.setPosX(left + GuiUtil.PADDING);
      this.labelWidget.render(matrixStack, mouseX, mouseY, delta);
    }
  }

  protected static class OptionEntry extends Entry {
    protected OptionEntry(
        MinecraftClient client, VariableHeightListWidget<Entry> parent, int top, int height) {
      super(client, parent, top, height);
    }

    @Override
    public List<Element> children() {
      return List.of();
    }
  }
}
