package me.roundaround.roundalib.config.gui.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.roundalib.config.gui.GuiUtil;
import me.roundaround.roundalib.config.gui.SelectableElement;
import me.roundaround.roundalib.config.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.config.gui.widget.ResetButtonWidget;
import me.roundaround.roundalib.config.gui.widget.Widget;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public abstract class ConfigOptionSubScreen<D, C extends ConfigOption<D, ?>> extends Screen {
  protected static final int DARKEN_STRENGTH = 120;

  protected final Screen parent;
  protected final C configOption;

  private final List<SelectableElement> selectableElements = new ArrayList<>();

  private C intermediateValue;
  private ResetButtonWidget<ConfigOptionSubScreen<D, C>> resetButton;
  private IconButtonWidget<ConfigOptionSubScreen<D, C>> cancelButton;
  private IconButtonWidget<ConfigOptionSubScreen<D, C>> doneButton;

  @SuppressWarnings("unchecked")
  protected ConfigOptionSubScreen(Text title, Screen parent, C configOption) {
    super(title);
    this.parent = parent;
    this.configOption = configOption;
    intermediateValue = (C) configOption.copy();
  }

  @Override
  protected void init() {
    doneButton = new IconButtonWidget<ConfigOptionSubScreen<D, C>>(
        this,
        height - 4 - IconButtonWidget.HEIGHT,
        width - 4 - IconButtonWidget.WIDTH,
        2,
        Text.translatable("roundalib.save.tooltip"),
        (button) -> {
          saveAndExit();
        });

    cancelButton = new IconButtonWidget<ConfigOptionSubScreen<D, C>>(
        this,
        height - 4 - IconButtonWidget.HEIGHT,
        doneButton.getLeft() - 4 - IconButtonWidget.WIDTH,
        1,
        Text.translatable("roundalib.discard.tooltip"),
        (button) -> {
          discardAndExit();
        });

    resetButton = new ResetButtonWidget<ConfigOptionSubScreen<D, C>>(
        this,
        height - 4 - IconButtonWidget.HEIGHT,
        cancelButton.getLeft() - 4 - IconButtonWidget.WIDTH) {
      @Override
      protected ConfigOption<?, ?> getConfigOption() {
        return intermediateValue;
      }
    };

    addSelectableChild(resetButton);
    addSelectableChild(cancelButton);
    addSelectableChild(doneButton);
  }

  @Override
  public void close() {
    if (client == null) {
      return;
    }
    client.setScreen(parent);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    return selectableElements.stream().anyMatch((element) -> {
      return element.mouseClicked(mouseX, mouseY, button);
    }) || super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    return selectableElements.stream().anyMatch((element) -> {
      return element.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }) || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    return selectableElements.stream().anyMatch((element) -> {
      return element.mouseScrolled(mouseX, mouseY, amount);
    }) || super.mouseScrolled(mouseX, mouseY, amount);
  }

  @Override
  public boolean mouseReleased(double mouseX, double mouseY, int button) {
    selectableElements.forEach((element) -> {
      element.mouseReleased(mouseX, mouseY, button);
    });

    return super.mouseReleased(mouseX, mouseY, button);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    switch (keyCode) {
      case GLFW.GLFW_KEY_ESCAPE:
        discardAndExit();
        return true;
      case GLFW.GLFW_KEY_S:
        if (Screen.hasControlDown()) {
          saveAndExit();
          return true;
        }
      case GLFW.GLFW_KEY_R:
        if (Screen.hasControlDown()) {
          resetToDefault();
          return true;
        }
    }

    return false;
  }

  @Override
  protected <T extends Element & Selectable> T addSelectableChild(T child) {
    selectableElements.add((SelectableElement) child);
    return super.addSelectableChild(child);
  }

  @Override
  protected void clearChildren() {
    selectableElements.clear();
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderBackground(matrixStack, mouseX, mouseY, partialTicks);
    renderContent(matrixStack, mouseX, mouseY, partialTicks);
    renderHelp(matrixStack, mouseX, mouseY, partialTicks);
    renderOverlay(matrixStack, mouseX, mouseY, partialTicks);
  }

  protected void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    if (parent == null) {
      renderDarkenBackground(matrixStack, mouseX, mouseY, partialTicks);
    } else {
      renderTextureBackground(matrixStack, mouseX, mouseY, partialTicks);
    }
  }

  protected void renderTextureBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
    RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND_TEXTURE);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
    bufferBuilder
        .vertex(0, height, 0)
        .texture(0, height / 32f)
        .color(32, 32, 32, 255)
        .next();
    bufferBuilder
        .vertex(width, height, 0)
        .texture(width / 32f, height / 32f)
        .color(32, 32, 32, 255)
        .next();
    bufferBuilder
        .vertex(width, 0, 0)
        .texture(width / 32f, 0)
        .color(32, 32, 32, 255)
        .next();
    bufferBuilder
        .vertex(0, 0, 0)
        .texture(0, 0)
        .color(32, 32, 32, 255)
        .next();
    tessellator.draw();
  }

  protected void renderDarkenBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    RenderSystem.disableTexture();
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    RenderSystem.setShader(GameRenderer::getPositionColorShader);

    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
    bufferBuilder
        .vertex(0, height, 0)
        .color(0, 0, 0, DARKEN_STRENGTH)
        .next();
    bufferBuilder
        .vertex(width, height, 0)
        .color(0, 0, 0, DARKEN_STRENGTH)
        .next();
    bufferBuilder
        .vertex(width, 0, 0)
        .color(0, 0, 0, DARKEN_STRENGTH)
        .next();
    bufferBuilder
        .vertex(0, 0, 0)
        .color(0, 0, 0, DARKEN_STRENGTH)
        .next();
    tessellator.draw();
  }

  protected void renderContent(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    drawCenteredText(matrixStack, textRenderer, title, width / 2, 17, GuiUtil.LABEL_COLOR);

    selectableElements.forEach(
        (element) -> {
          if (element instanceof Drawable) {
            ((Drawable) element).render(matrixStack, mouseX, mouseY, partialTicks);
          }
        });
  }

  protected void renderHelp(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    if (Screen.hasShiftDown()) {
      renderHelpExpanded(matrixStack, mouseX, mouseY, partialTicks);
    } else {
      renderHelpPrompt(matrixStack, mouseX, mouseY, partialTicks);
    }
  }

  protected void renderHelpPrompt(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderHelpLines(matrixStack, getHelpShort(mouseX, mouseY, partialTicks));
  }

  protected void renderHelpExpanded(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderHelpLines(matrixStack, getHelpLong(mouseX, mouseY, partialTicks));
  }

  private void renderHelpLines(MatrixStack matrixStack, List<Text> lines) {
    int startingOffset = height - 4 - textRenderer.fontHeight
        - (lines.size() - 1) * (textRenderer.fontHeight + 2);

    for (int i = 0; i < lines.size(); i++) {
      drawTextWithShadow(
          matrixStack,
          textRenderer,
          lines.get(i),
          4,
          startingOffset + i * (textRenderer.fontHeight + 2),
          GuiUtil.LABEL_COLOR);
    }
  }

  protected List<Text> getHelpShort(int mouseX, int mouseY, float partialTicks) {
    return List.of(Text.literal("Hold shift for help"));
  }

  protected List<Text> getHelpLong(int mouseX, int mouseY, float partialTicks) {
    // TODO: Migrate to translation keys and Text.translatables
    return List.of(
        Text.literal("Escape: cancel and discard changes"),
        (MinecraftClient.IS_SYSTEM_MAC ? Text.literal("Cmd + S: save changes")
            : Text.literal("Ctrl + S: save changes")),
        (MinecraftClient.IS_SYSTEM_MAC ? Text.literal("Cmd + R: reset value to default")
            : Text.literal("Ctrl + R: reset value to default")));
  }

  protected void renderOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    List<Text> tooltip = selectableElements.stream()
        .map((element) -> {
          if (element instanceof Widget) {
            return ((Widget) element).getTooltip(mouseX, mouseY, partialTicks);
          }
          return List.<Text>of();
        })
        .flatMap(List::stream)
        .collect(Collectors.toList());
    renderTooltip(matrixStack, tooltip, mouseX, mouseY);
  }

  protected void setValue(D value) {
    intermediateValue.setValue(value);
  }

  protected D getValue() {
    return intermediateValue.getValue();
  }

  protected boolean isDirty() {
    return intermediateValue.isDirty();
  }

  protected void commitValueToConfig() {
    configOption.setValue(getValue());
  }

  private void discardAndExit() {
    // TODO: Confirm?
    close();
  }

  private void saveAndExit() {
    commitValueToConfig();
    close();
  }

  private void resetToDefault() {
    setValue(configOption.getDefault());
  }
}
