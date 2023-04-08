package me.roundaround.testmod.client.gui.screen;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.config.ConfigListWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
  private static final int LIST_MIN_WIDTH = 400;
  private final Screen parent;
  private ConfigListWidget configListWidget;

  public ConfigScreen(Screen parent) {
    super(Text.literal("RoundaLib Test Mod Config"));
    this.parent = parent;
  }

  @Override
  public void init() {
    int listWidth = (int) Math.max(LIST_MIN_WIDTH, width / 1.5f);
    int listLeft = (int) ((width / 2f) - (listWidth / 2f));
    int listHeight = this.height - 64;
    int listTop = 32;

    this.configListWidget = addDrawableChild(new ConfigListWidget(this.client,
        listLeft,
        listTop,
        listWidth,
        listHeight));
    this.configListWidget.addCategory(Text.literal("Category 1"));
    this.configListWidget.addOption(Text.literal("Option 1"));
    this.configListWidget.addOption(Text.literal("Option 2"));
    this.configListWidget.addOption(Text.literal("Option 3"));

    this.configListWidget.addCategory(Text.literal("Category 2"));
    this.configListWidget.addOption(Text.literal("Option 4"));
    this.configListWidget.addOption(Text.literal("Option 5"));
    this.configListWidget.addOption(Text.literal("Option 6"));

    this.configListWidget.addCategory(Text.literal("Category 3"));
    this.configListWidget.addOption(Text.literal("Option 7"));
    this.configListWidget.addOption(Text.literal("Option 8"));
    this.configListWidget.addOption(Text.literal("Option 9"));

    this.configListWidget.addCategory(Text.literal("Category 4"));
    this.configListWidget.addOption(Text.literal("Option 10"));
    this.configListWidget.addOption(Text.literal("Option 11"));
    this.configListWidget.addOption(Text.literal("Option 12"));

    this.configListWidget.addCategory(Text.literal("Category 5"));
    this.configListWidget.addOption(Text.literal("Option 13"));
    this.configListWidget.addOption(Text.literal("Option 14"));
    this.configListWidget.addOption(Text.literal("Option 15"));
  }

  @Override
  public void close() {
    if (this.client == null) {
      super.close();
      return;
    }
    this.client.setScreen(this.parent);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    return this.configListWidget.mouseClicked(mouseX, mouseY, button) ||
        super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    return this.configListWidget.mouseReleased(mouseX, mouseY, button) ||
        super.mouseReleased(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseDragged(
      double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    return this.configListWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) ||
        super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    return this.configListWidget.mouseScrolled(mouseX, mouseY, amount) ||
        super.mouseScrolled(mouseX, mouseY, amount);
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    renderBackground(matrixStack);
    drawCenteredTextWithShadow(matrixStack,
        this.textRenderer,
        this.title,
        this.width / 2,
        20,
        GuiUtil.LABEL_COLOR);
    super.render(matrixStack, mouseX, mouseY, delta);
  }
}
