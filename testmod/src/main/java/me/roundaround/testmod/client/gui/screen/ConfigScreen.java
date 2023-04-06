package me.roundaround.testmod.client.gui.screen;

import me.roundaround.roundalib.client.gui.widget.VariableHeightListWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
  private final Screen parent;

  public ConfigScreen(Screen parent) {
    super(Text.literal("RoundaLib Test Mod Config"));
    this.parent = parent;
  }

  @Override
  public void close() {
    this.client.setScreen(parent);
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    renderBackground(matrixStack);
    drawCenteredTextWithShadow(matrixStack, this.textRenderer, this.title, this.width / 2, 20, 16777215);
    super.render(matrixStack, mouseX, mouseY, delta);
  }
}
