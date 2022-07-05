package me.roundaround.roundalib.config.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.roundalib.config.gui.GuiUtil;
import me.roundaround.roundalib.config.gui.SelectableElement;
import me.roundaround.roundalib.config.gui.control.Control;
import me.roundaround.roundalib.config.gui.screen.ConfigScreen;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public class OptionRowWidget extends AbstractWidget<ConfigListWidget> {
  public static final int HEIGHT = 20;
  protected static final int HIGHLIGHT_COLOR = 0x30FFFFFF;
  protected static final int PADDING = 4;
  protected static final int CONTROL_MIN_WIDTH = 100;
  protected static final int ROW_SHADE_STRENGTH = 85;
  protected static final int ROW_SHADE_FADE_WIDTH = 10;
  protected static final int ROW_SHADE_FADE_OVERFLOW = 10;

  protected final int index;
  protected final ConfigOption<?, ?> configOption;
  protected final Widget control;
  protected final ResetButtonWidget resetButton;

  private final ImmutableList<Widget> subWidgets;

  public OptionRowWidget(
      ConfigListWidget parent,
      int index,
      ConfigOption<?, ?> configOption,
      int top,
      int left,
      int width) {
    super(parent, top, left, HEIGHT, width);

    int controlWidth = Math.max(CONTROL_MIN_WIDTH, Math.round(width * 0.3f));

    this.index = index;
    this.configOption = configOption;
    control = getConfigList().createControl(
        configOption,
        this,
        top,
        right - controlWidth - ResetButtonWidget.WIDTH - (PADDING * 2),
        height,
        controlWidth);
    resetButton = new ResetButtonWidget(
        this,
        top + (HEIGHT - ResetButtonWidget.HEIGHT) / 2,
        right - PADDING - ResetButtonWidget.WIDTH);

    subWidgets = ImmutableList.of(control, resetButton);
  }

  @Override
  public boolean onMouseClicked(double mouseX, double mouseY, int button) {
    // TODO: Clicking on non-controls should click the primary element
    return super.onMouseClicked(mouseX, mouseY, button);
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderBackground(matrixStack, mouseX, mouseY, partialTicks);
    renderLabel(matrixStack, mouseX, mouseY, partialTicks);
    renderControl(matrixStack, mouseX, mouseY, partialTicks);
    renderResetButton(matrixStack, mouseX, mouseY, partialTicks);
    renderDecorations(matrixStack, mouseX, mouseY, partialTicks);
  }

  @Override
  public void tick() {
    subWidgets.forEach((widget) -> widget.tick());
  }

  @Override
  public List<Text> getTooltip(int mouseX, int mouseY, float delta) {
    return subWidgets.stream()
        .map(subWidget -> subWidget.getTooltip(mouseX, mouseY, delta))
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  @Override
  public void moveTop(int top) {
    super.moveTop(top);

    control.moveTop(top);
    resetButton.moveTop(top + (HEIGHT - ResetButtonWidget.HEIGHT) / 2);
  }

  protected void renderBackground(
      MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    if (index % 2 == 0) {
      RenderSystem.disableTexture();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);

      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferBuilder = tessellator.getBuffer();
      Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();

      int bgLeft = left - ROW_SHADE_FADE_OVERFLOW;
      int bgRight = right + ROW_SHADE_FADE_OVERFLOW;

      bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      bufferBuilder
          .vertex(matrix4f, bgLeft - 1 + ROW_SHADE_FADE_WIDTH, top - 1, 0)
          .color(0, 0, 0, ROW_SHADE_STRENGTH)
          .next();
      bufferBuilder.vertex(matrix4f, bgLeft - 1, top - 1, 0).color(0, 0, 0, 0).next();
      bufferBuilder.vertex(matrix4f, bgLeft - 1, bottom + 2, 0).color(0, 0, 0, 0).next();
      bufferBuilder
          .vertex(matrix4f, bgLeft - 1 + ROW_SHADE_FADE_WIDTH, bottom + 2, 0)
          .color(0, 0, 0, ROW_SHADE_STRENGTH)
          .next();

      bufferBuilder
          .vertex(matrix4f, bgRight + 2 - ROW_SHADE_FADE_WIDTH, top - 1, 0)
          .color(0, 0, 0, ROW_SHADE_STRENGTH)
          .next();
      bufferBuilder
          .vertex(matrix4f, bgLeft - 1 + ROW_SHADE_FADE_WIDTH, top - 1, 0)
          .color(0, 0, 0, ROW_SHADE_STRENGTH)
          .next();
      bufferBuilder
          .vertex(matrix4f, bgLeft - 1 + ROW_SHADE_FADE_WIDTH, bottom + 2, 0)
          .color(0, 0, 0, ROW_SHADE_STRENGTH)
          .next();
      bufferBuilder
          .vertex(matrix4f, bgRight + 2 - ROW_SHADE_FADE_WIDTH, bottom + 2, 0)
          .color(0, 0, 0, ROW_SHADE_STRENGTH)
          .next();

      bufferBuilder.vertex(matrix4f, bgRight + 2, top - 1, 0).color(0, 0, 0, 0).next();
      bufferBuilder
          .vertex(matrix4f, bgRight + 2 - ROW_SHADE_FADE_WIDTH, top - 1, 0)
          .color(0, 0, 0, ROW_SHADE_STRENGTH)
          .next();
      bufferBuilder
          .vertex(matrix4f, bgRight + 2 - ROW_SHADE_FADE_WIDTH, bottom + 2, 0)
          .color(0, 0, 0, ROW_SHADE_STRENGTH)
          .next();
      bufferBuilder.vertex(matrix4f, bgRight + 2, bottom + 2, 0).color(0, 0, 0, 0).next();
      tessellator.draw();

      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
    }
  }

  protected void renderLabel(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    drawTextWithShadow(
        matrixStack,
        GuiUtil.getTextRenderer(),
        configOption.getLabel(),
        left + PADDING,
        top + (height - 8) / 2,
        isValid() ? GuiUtil.LABEL_COLOR : GuiUtil.ERROR_COLOR);
  }

  protected void renderControl(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    control.render(matrixStack, mouseX, mouseY, partialTicks);
  }

  protected void renderResetButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    resetButton.render(matrixStack, mouseX, mouseY, partialTicks);
  }

  protected void renderDecorations(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    // TODO: Re-enable once clicking on row does anything
    // if (isMouseOver(mouseX, mouseY) && parent.isMouseOver(mouseX, mouseY)) {
    //   drawHorizontalLine(matrixStack, left - 1, right + 1, top - 1, HIGHLIGHT_COLOR);
    //   drawHorizontalLine(matrixStack, left - 1, right + 1, bottom + 1, HIGHLIGHT_COLOR);
    //   drawVerticalLine(matrixStack, left - 1, top - 1, bottom + 1, HIGHLIGHT_COLOR);
    //   drawVerticalLine(matrixStack, right + 1, top - 1, bottom + 1, HIGHLIGHT_COLOR);
    // }
  }

  public ConfigOption<?, ?> getConfigOption() {
    return configOption;
  }

  @Override
  public List<SelectableElement> getSelectableElements() {
    List<SelectableElement> elements = new ArrayList<>(control.getSelectableElements());
    elements.add(resetButton);
    return elements;
  }

  @Override
  public Optional<SelectableElement> getPrimarySelectableElement() {
    return control.getPrimarySelectableElement();
  }

  public boolean focusPrimaryElement() {
    if (control.getPrimarySelectableElement().isEmpty()) {
      return false;
    }
    return getConfigScreen().setFocused(control.getPrimarySelectableElement().get());
  }

  public ConfigListWidget getConfigList() {
    return getParent();
  }

  public ConfigScreen getConfigScreen() {
    return getConfigList().getConfigScreen();
  }

  public boolean isValid() {
    if (!(control instanceof Control<?>)) {
      // Should never hit here. Should be of type `Widget & Control<?>` but
      // can't figure out how to declare a union type field.
      return true;
    }
    return ((Control<?>) control).isValid();
  }
}
