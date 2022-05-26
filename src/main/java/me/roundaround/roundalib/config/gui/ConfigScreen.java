package me.roundaround.roundalib.config.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.roundalib.config.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
  private static final int HEADER_HEIGHT = 36;
  private static final int FOOTER_HEIGHT = 36;
  private static final int LIST_MIN_WIDTH = 400;
  private static final int TITLE_COLOR = 0xFFFFFFFF;
  private static final int TITLE_POS_Y = 17;
  private static final int DONE_BUTTON_WIDTH = 200;
  private static final int DONE_BUTTON_HEIGHT = 20;
  private static final int DONE_BUTTON_POS_Y = 27;

  @Nullable
  private final Screen parent;
  private final ModConfig modConfig;

  private ConfigList listWidget;
  private ButtonWidget doneButton;

  public ConfigScreen(@Nullable Screen parent, ModConfig modConfig) {
    super(new TranslatableText(modConfig.getModInfo().getConfigScreenTitleI18nKey()));
    this.parent = parent;
    this.modConfig = modConfig;
  }

  @Override
  protected void init() {
    super.init();
    clearChildren();

    int listWidth = (int) Math.max(LIST_MIN_WIDTH, width / 1.5f);
    int listLeft = (int) ((width / 2f) - (listWidth / 2f));
    int listHeight = height - HEADER_HEIGHT - FOOTER_HEIGHT;
    listWidget = new ConfigList(this, HEADER_HEIGHT, listLeft, listHeight, listWidth);
    listWidget.init();

    int doneButtonLeft = (int) (width / 2f - DONE_BUTTON_WIDTH / 2f);
    int doneButtonTop = height - DONE_BUTTON_POS_Y;
    doneButton = new ButtonWidget(
        doneButtonLeft,
        doneButtonTop,
        DONE_BUTTON_WIDTH,
        DONE_BUTTON_HEIGHT,
        ScreenTexts.DONE,
        (button) -> {
          if (client == null) {
            return;
          }

          modConfig.saveToFile();
          client.setScreen(parent);
        });

    listWidget.getSelectableElements().forEach(this::addSelectableChild);
    addSelectableChild(doneButton);
    setInitialFocus(children().get(0));
  }

  @Override
  public void removed() {
    modConfig.saveToFile();
  }

  @Override
  public void onClose() {
    if (client == null) {
      return;
    }
    client.setScreen(parent);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (listWidget.mouseClicked(mouseX, mouseY, button)) {
      return true;
    }

    if (doneButton.mouseClicked(mouseX, mouseY, button)) {
      return true;
    }

    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    if (listWidget.mouseReleased(mouseX, mouseY, button)) {
      return true;
    }

    if (doneButton.mouseReleased(mouseX, mouseY, button)) {
      return true;
    }

    return super.mouseReleased(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseDragged(
      double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    if (listWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
      return true;
    }

    return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    if (listWidget.mouseScrolled(mouseX, mouseY, amount)) {
      return true;
    }

    return super.mouseScrolled(mouseX, mouseY, amount);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (listWidget.keyPressed(keyCode, scanCode, modifiers)) {
      return true;
    }

    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean charTyped(char chr, int modifiers) {
    if (listWidget.charTyped(chr, modifiers)) {
      return true;
    }

    return super.charTyped(chr, modifiers);
  }

  @Override
  public void resize(MinecraftClient client, int width, int height) {
    if (parent != null) {
      parent.resize(client, width, height);
    }

    super.resize(client, width, height);
  }

  @Override
  public void tick() {
    listWidget.tick();
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderBackgroundInRegion(0, height, 0, width);

    listWidget.render(matrixStack, mouseX, mouseY, partialTicks);

    renderHeader(matrixStack, mouseX, mouseY, partialTicks);
    renderFooter(matrixStack, mouseX, mouseY, partialTicks);

    renderTooltip(
        matrixStack, listWidget.getTooltip(mouseX, mouseY, partialTicks), mouseX, mouseY);
  }

  public void renderHeader(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderBackgroundInRegion(0, HEADER_HEIGHT, 0, width);
    drawCenteredText(
        matrixStack, textRenderer, title, width / 2, TITLE_POS_Y, TITLE_COLOR);
  }

  public void renderFooter(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderBackgroundInRegion(height - FOOTER_HEIGHT, height, 0, width);
    doneButton.render(matrixStack, mouseX, mouseY, partialTicks);
  }

  public void renderBackgroundInRegion(int top, int bottom, int left, int right) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
    RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND_TEXTURE);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

    float width = (right - left) / 32f;
    float height = (top - bottom) / 32f;

    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
    bufferBuilder.vertex(0, bottom, 0).texture(0, height).color(64, 64, 64, 255).next();
    bufferBuilder.vertex(right, bottom, 0).texture(width, height).color(64, 64, 64, 255).next();
    bufferBuilder.vertex(right, top, 0).texture(width, 0).color(64, 64, 64, 255).next();
    bufferBuilder.vertex(left, top, 0).texture(0, 0).color(64, 64, 64, 255).next();
    tessellator.draw();
  }

  public ModConfig getModConfig() {
    return modConfig;
  }

  @Override
  public void setFocused(Element focused) {
    super.setFocused(focused);
    if (listWidget != null) {
      listWidget.onSetFocused(focused);
    }
  }
}
