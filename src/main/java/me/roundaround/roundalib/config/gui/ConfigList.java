package me.roundaround.roundalib.config.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.roundalib.config.option.ConfigOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ConfigList extends AbstractWidget<ConfigScreen> implements Scrollable {
  private static final int SCROLLBAR_WIDTH = 6;
  private static final int PADDING_X = 4;
  private static final int PADDING_Y = 4;
  private static final int ROW_PADDING = 2;

  public final ConfigScreen parent;

  private final Scrollbar scrollbar;
  private final List<OptionRow> optionRows = new ArrayList<>();
  private final int elementStartX;
  private final int elementStartY;
  private final int elementWidth;
  private final int elementHeight;
  private double scrollAmount = 0;

  public ConfigList(ConfigScreen parent, int top, int left, int height, int width) {
    super(parent, top, left, height, width);
    this.parent = parent;

    elementStartX = left + PADDING_X;
    elementStartY = top + PADDING_Y;

    elementWidth = width - (2 * PADDING_X) - SCROLLBAR_WIDTH;
    elementHeight = OptionRow.HEIGHT;

    scrollbar = new Scrollbar(
        this,
        (elementHeight + ROW_PADDING) / 2d,
        top,
        right - SCROLLBAR_WIDTH + 1,
        height,
        SCROLLBAR_WIDTH);
  }

  @Override
  public void init() {
    optionRows.clear();

    ImmutableList<ConfigOption<?, ?>> configOptions = parent.getModConfig().getConfigOptions();
    IntStream.range(0, configOptions.size())
        .forEach(
            idx -> optionRows.add(
                new OptionRow(
                    this,
                    idx,
                    configOptions.get(idx),
                    getElementTop(idx),
                    elementStartX,
                    elementWidth)));

    scrollbar.setMaxPosition(
        optionRows.size() * (elementHeight + ROW_PADDING) + PADDING_Y - ROW_PADDING + 1);
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    if (scrollbar.mouseClicked(mouseX, mouseY, button)) {
      return true;
    }

    return false;
  }

  @Override
  public boolean mouseDragged(
      double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    if (scrollbar.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
      return true;
    }

    return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
  }

  @Override
  public boolean onMouseScrolled(double mouseX, double mouseY, double amount) {
    return scrollbar.onMouseScrolled(mouseX, mouseY, amount);
  }

  public void scroll(double amount) {
    scrollbar.scroll(amount);
  }

  public void setScrollAmount(double amount) {
    scrollAmount = amount;

    IntStream.range(0, optionRows.size())
        .forEach(idx -> optionRows.get(idx).moveTop(getElementTop(idx)));
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderBackground(matrixStack);
    renderConfigOptionEntries(matrixStack, mouseX, mouseY, partialTicks);

    scrollbar.render(matrixStack, mouseX, mouseY, partialTicks);
  }

  @Override
  public List<Text> getTooltip(int mouseX, int mouseY, float delta) {
    return optionRows.stream()
        .map(optionRow -> optionRow.getTooltip(mouseX, mouseY, delta))
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  protected void renderBackground(MatrixStack matrixStack) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();

    RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
    RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
    bufferBuilder
        .vertex(0, bottom, 0)
        .texture(0, (float) (bottom + Math.round(scrollAmount)) / 32f)
        .color(32, 32, 32, 255)
        .next();
    bufferBuilder
        .vertex(parent.width, bottom, 0)
        .texture(parent.width / 32f, (float) (bottom + Math.round(scrollAmount)) / 32f)
        .color(32, 32, 32, 255)
        .next();
    bufferBuilder
        .vertex(parent.width, top, 0)
        .texture(parent.width / 32f, (float) (top + Math.round(scrollAmount)) / 32f)
        .color(32, 32, 32, 255)
        .next();
    bufferBuilder
        .vertex(0, top, 0)
        .texture(0, (float) (top + Math.round(scrollAmount)) / 32f)
        .color(32, 32, 32, 255)
        .next();
    tessellator.draw();

    RenderSystem.depthFunc(515);
    RenderSystem.disableDepthTest();
    RenderSystem.enableBlend();
    RenderSystem.blendFuncSeparate(
        GlStateManager.SrcFactor.SRC_ALPHA,
        GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
        GlStateManager.SrcFactor.ZERO,
        GlStateManager.DstFactor.ONE);
    RenderSystem.disableTexture();
    RenderSystem.setShader(GameRenderer::getPositionColorShader);

    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
    bufferBuilder.vertex(0, top + PADDING_Y, 0).color(0, 0, 0, 0).next();
    bufferBuilder.vertex(parent.width, top + PADDING_Y, 0).color(0, 0, 0, 0).next();
    bufferBuilder.vertex(parent.width, top, 0).color(0, 0, 0, 255).next();
    bufferBuilder.vertex(0, top, 0).color(0, 0, 0, 255).next();
    bufferBuilder.vertex(0, bottom, 0).color(0, 0, 0, 255).next();
    bufferBuilder.vertex(parent.width, bottom, 0).color(0, 0, 0, 255).next();
    bufferBuilder.vertex(parent.width, bottom - PADDING_Y, 0).color(0, 0, 0, 0).next();
    bufferBuilder.vertex(0, bottom - PADDING_Y, 0).color(0, 0, 0, 0).next();
    tessellator.draw();

    RenderSystem.enableTexture();
    RenderSystem.disableBlend();
  }

  protected void renderConfigOptionEntries(
      MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    optionRows.forEach(
        optionRow -> {
          if (optionRow.getBottom() >= top && optionRow.getTop() <= bottom) {
            optionRow.render(matrixStack, mouseX, mouseY, partialTicks);
          }
        });
  }

  private int getElementTop(int idx) {
    return elementStartY - (int) Math.round(scrollAmount) + idx * (elementHeight + ROW_PADDING);
  }

  @Override
  public void tick() {
    optionRows.forEach(OptionRow::tick);
  }

  @Override
  public List<SelectableElement> getSelectableElements() {
    return optionRows.stream()
        .map(OptionRow::getSelectableElements)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  public void onSetFocused(Element focused) {
    optionRows.stream()
        .filter((optionRow) -> optionRow.getSelectableElements().indexOf(focused) > -1)
        .forEach(this::ensureVisible);
  }

  public void ensureVisible(OptionRow optionRow) {
    int rowTop = optionRow.getTop() - PADDING_Y - 1;
    if (rowTop < top) {
      scroll(rowTop - top);
      return;
    }

    int rowBottom = optionRow.getBottom() + PADDING_Y + 1;
    if (rowBottom > bottom) {
      scroll(rowBottom - bottom);
    }
  }

  public boolean moveFocus(int amount) {
    if (optionRows.isEmpty()) {
      return false;
    }

    int desiredIndex = optionRows.stream()
        .filter((optionRow) -> optionRow.getSelectableElements().contains(getConfigScreen().getFocused())).findFirst()
        .orElse(optionRows.get(optionRows.size() - 1)).index + amount;
    desiredIndex = Math.min(Math.max(0, desiredIndex), optionRows.size() - 1);

    // TODO: Make this determined by the control (i.e. getPrimarySelectableElement)
    SelectableElement desiredFocus = optionRows.get(desiredIndex).control.getSelectableElements().get(0);
    getConfigScreen().setFocused(desiredFocus);
    return true;
  }

  public ConfigScreen getConfigScreen() {
    return getParent();
  }
}
