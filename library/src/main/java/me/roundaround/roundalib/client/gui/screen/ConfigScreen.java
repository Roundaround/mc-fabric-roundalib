package me.roundaround.roundalib.client.gui.screen;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.config.ConfigListWidget;
import me.roundaround.roundalib.config.ModConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ConfigScreen extends Screen {
  private static final int LIST_MIN_WIDTH = 400;
  private static final int FOOTER_BUTTON_WIDTH = 150;
  private static final int FOOTER_BUTTON_HEIGHT = 20;
  private static final int FOOTER_BUTTON_POS_Y = 28;
  private static final int FOOTER_BUTTON_SPACING = 8;
  private final Screen parent;
  private final ModConfig modConfig;
  private ConfigListWidget configListWidget;
  private ButtonWidget cancelButton;
  private ButtonWidget doneButton;
  private boolean shouldSave = false;

  public ConfigScreen(Screen parent, ModConfig modConfig) {
    super(Text.translatable(modConfig.getConfigScreenI18nKey()));
    this.parent = parent;
    this.modConfig = modConfig;
  }

  @Override
  public void init() {
    int listWidth = (int) Math.max(LIST_MIN_WIDTH, width / 1.5f);
    int listLeft = (int) ((width / 2f) - (listWidth / 2f));
    int listHeight = this.height - 64;
    int listTop = 32;

    this.configListWidget = addDrawableChild(new ConfigListWidget(this.client,
        this.modConfig,
        listLeft,
        listTop,
        listWidth,
        listHeight));


    int cancelButtonLeft = (width - FOOTER_BUTTON_SPACING) / 2 - FOOTER_BUTTON_WIDTH;
    int cancelButtonTop = height - FOOTER_BUTTON_POS_Y;
    this.cancelButton = addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, this::cancel)
        .position(cancelButtonLeft, cancelButtonTop)
        .size(FOOTER_BUTTON_WIDTH, FOOTER_BUTTON_HEIGHT)
        .build());

    int doneButtonLeft = (width + FOOTER_BUTTON_SPACING) / 2;
    int doneButtonTop = height - FOOTER_BUTTON_POS_Y;
    this.doneButton = addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, this::done)
        .position(doneButtonLeft, doneButtonTop)
        .size(FOOTER_BUTTON_WIDTH, FOOTER_BUTTON_HEIGHT)
        .build());
  }

  private void cancel(ButtonWidget button) {
    this.shouldSave = false;
    this.close();
  }

  private void done(ButtonWidget button) {
    this.shouldSave = true;
    this.close();
  }

  @Override
  public void close() {
    if (this.shouldSave) {
      this.modConfig.saveToFile();
    } else {
      this.modConfig.loadFromFile();
    }

    if (this.client == null) {
      return;
    }

    this.client.setScreen(this.parent);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == GLFW.GLFW_KEY_K) {
      this.configListWidget.nextCategory();
      return true;
    }

    return this.configListWidget.keyPressed(keyCode, scanCode, modifiers) ||
        super.keyPressed(keyCode, scanCode, modifiers);
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
