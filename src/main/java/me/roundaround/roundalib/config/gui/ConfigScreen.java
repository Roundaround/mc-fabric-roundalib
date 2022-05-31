package me.roundaround.roundalib.config.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.mojang.blaze3d.systems.RenderSystem;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import me.roundaround.roundalib.RoundaLibMod;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.gui.widget.ButtonWidget;
import me.roundaround.roundalib.config.gui.widget.ConfigListWidget;
import me.roundaround.roundalib.config.gui.widget.OptionRowWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
  private static final int HEADER_HEIGHT = 36;
  private static final int FOOTER_HEIGHT = 36;
  private static final int LIST_MIN_WIDTH = 400;
  private static final int TITLE_COLOR = 0xFFFFFFFF;
  private static final int TITLE_POS_Y = 17;
  private static final int FOOTER_BUTTON_WIDTH = 150;
  private static final int FOOTER_BUTTON_HEIGHT = 20;
  private static final int FOOTER_BUTTON_POS_Y = 27;
  private static final int FOOTER_BUTTON_SPACING = 12;

  @Nullable
  private final Screen parent;
  private final ModConfig modConfig;
  private final List<SelectableElement> selectableElements = new ArrayList<>();
  private final Set<OptionRowWidget> invalidRows = new HashSet<>(); 

  private ConfigListWidget listWidget;
  private ButtonWidget cancelButton;
  private ButtonWidget doneButton;
  private Optional<SelectableElement> focused = Optional.empty();
  private boolean shouldSave = false;

  public ConfigScreen(@Nullable Screen parent, ModConfig modConfig) {
    super(new TranslatableText(modConfig.getModInfo().getConfigScreenTitleI18nKey()));
    this.parent = parent;
    this.modConfig = modConfig;
  }

  @Override
  protected void init() {
    client.keyboard.setRepeatEvents(true);

    int listWidth = (int) Math.max(LIST_MIN_WIDTH, width / 1.5f);
    int listLeft = (int) ((width / 2f) - (listWidth / 2f));
    int listHeight = height - HEADER_HEIGHT - FOOTER_HEIGHT;
    listWidget = new ConfigListWidget(this, HEADER_HEIGHT, listLeft, listHeight, listWidth);
    listWidget.init();

    int cancelButtonLeft = (int) (width / 2f - FOOTER_BUTTON_WIDTH) - FOOTER_BUTTON_SPACING;
    int cancelButtonTop = height - FOOTER_BUTTON_POS_Y;
    cancelButton = new ButtonWidget(
        cancelButtonTop,
        cancelButtonLeft,
        FOOTER_BUTTON_HEIGHT,
        FOOTER_BUTTON_WIDTH,
        ScreenTexts.CANCEL,
        (button) -> {
          onClose();
        });

    int doneButtonLeft = (int) (width / 2f) + FOOTER_BUTTON_SPACING;
    int doneButtonTop = height - FOOTER_BUTTON_POS_Y;
    doneButton = new ButtonWidget(
        doneButtonTop,
        doneButtonLeft,
        FOOTER_BUTTON_HEIGHT,
        FOOTER_BUTTON_WIDTH,
        ScreenTexts.DONE,
        (button) -> {
          shouldSave = true;
          onClose();
        });

    selectableElements.addAll(listWidget.getSelectableElements());
    selectableElements.add(cancelButton);
    selectableElements.add(doneButton);
  }

  @Override
  public void removed() {
    client.keyboard.setRepeatEvents(false);
  }

  @Override
  public void onClose() {
    RoundaLibMod.LOGGER.info("onClose");
    if (shouldSave) {
      modConfig.saveToFile();
    } else {
      modConfig.loadFromFile();
    }

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
    return selectableElements.stream().anyMatch((element) -> {
      return element.mouseClicked(mouseX, mouseY, button);
    }) || super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    if (listWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
      return true;
    }
    return selectableElements.stream().anyMatch((element) -> {
      return element.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }) || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    if (listWidget.mouseScrolled(mouseX, mouseY, amount)) {
      return true;
    }
    return selectableElements.stream().anyMatch((element) -> {
      return element.mouseScrolled(mouseX, mouseY, amount);
    }) || super.mouseScrolled(mouseX, mouseY, amount);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (super.keyPressed(keyCode, scanCode, modifiers)) {
      return true;
    }

    switch (keyCode) {
      case GLFW.GLFW_KEY_UP:
        return listWidget.moveFocus(-1);
      case GLFW.GLFW_KEY_DOWN:
        return listWidget.moveFocus(1);
      case GLFW.GLFW_KEY_PAGE_UP:
        return listWidget.moveFocus(-4);
      case GLFW.GLFW_KEY_PAGE_DOWN:
        return listWidget.moveFocus(4);
      case GLFW.GLFW_KEY_HOME:
        return listWidget.moveFocus(Integer.MIN_VALUE);
      case GLFW.GLFW_KEY_END:
        return listWidget.moveFocus(Integer.MAX_VALUE);
    }

    return false;
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

    matrixStack.push();
    matrixStack.translate(0, 0, 1);
    renderHeader(matrixStack, mouseX, mouseY, partialTicks);
    renderFooter(matrixStack, mouseX, mouseY, partialTicks);
    matrixStack.pop();

    renderTooltip(
        matrixStack, listWidget.getTooltip(mouseX, mouseY, partialTicks), mouseX, mouseY);
  }

  public void renderHeader(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderBackgroundInRegion(0, HEADER_HEIGHT, 0, width);
    drawCenteredText(
        matrixStack, textRenderer, title, width / 2, TITLE_POS_Y, TITLE_COLOR);
  }

  public void renderFooter(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderBackgroundInRegion(height - FOOTER_HEIGHT - 1, height, 0, width);
    cancelButton.render(matrixStack, mouseX, mouseY, partialTicks);
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
    bufferBuilder.vertex(left, bottom, 0).texture(0, height).color(64, 64, 64, 255).next();
    bufferBuilder.vertex(right, bottom, 0).texture(width, height).color(64, 64, 64, 255).next();
    bufferBuilder.vertex(right, top, 0).texture(width, 0).color(64, 64, 64, 255).next();
    bufferBuilder.vertex(left, top, 0).texture(0, 0).color(64, 64, 64, 255).next();
    tessellator.draw();
  }

  public ModConfig getModConfig() {
    return modConfig;
  }

  @Override
  protected void clearChildren() {
    selectableElements.clear();
  }

  @Override
  public boolean changeFocus(boolean lookForwards) {
    if (selectableElements.isEmpty()) {
      return false;
    }

    int index = focused.isPresent() ? selectableElements.indexOf(focused.get()) : -1;
    int step = lookForwards ? 1 : -1;
    if (!lookForwards && index == -1) {
      index = 0;
    }

    index = (index + selectableElements.size() + step) % selectableElements.size();

    int originalIndex = index;
    while (!setFocused(selectableElements.get(index))) {
      index = (index + selectableElements.size() + step) % selectableElements.size();
      if (index == originalIndex) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Element getFocused() {
    return focused.orElse(null);
  }

  public boolean setFocused(SelectableElement newFocused) {
    if (focused.isPresent() && !this.focused.get().equals(newFocused)) {
      focused.get().setIsFocused(false);
    }
    boolean result = newFocused.setIsFocused(true);
    if (result) {
      focused = Optional.of(newFocused);
      listWidget.onSetFocused(newFocused);
    }
    return result;
  }

  public void declareFocused(SelectableElement newFocused) {
    if (focused.isPresent() && !this.focused.get().equals(newFocused)) {
      focused.get().setIsFocused(false);
    }
    focused = Optional.of(newFocused);
    listWidget.onSetFocused(newFocused);
  }

  public void markInvalid(OptionRowWidget optionRow) {
    invalidRows.add(optionRow);
    updateDoneButton();
  }

  public void markValid(OptionRowWidget optionRow) {
    invalidRows.remove(optionRow);
    updateDoneButton();
  }

  private void updateDoneButton() {
    if (doneButton == null) {
      return;
    }
    doneButton.active = invalidRows.isEmpty();
  }
}
