package me.roundaround.roundalib.config.gui.widget;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import me.roundaround.roundalib.config.gui.ConfigScreen;
import me.roundaround.roundalib.config.gui.Scrollable;
import me.roundaround.roundalib.config.gui.SelectableElement;
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
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ConfigListWidget extends AbstractWidget<ConfigScreen> implements Scrollable {
  private static final int SCROLLBAR_WIDTH = 6;
  private static final int PADDING_X = 4;
  private static final int PADDING_Y = 4;
  private static final int ROW_PADDING = 2;

  public final ConfigScreen parent;

  private final ScrollbarWidget scrollbar;
  private final List<AbstractWidget<ConfigListWidget>> rows = new ArrayList<>();
  private final int elementStartX;
  private final int elementStartY;
  private final int elementWidth;
  private double scrollAmount = 0;

  public ConfigListWidget(ConfigScreen parent, int top, int left, int height, int width) {
    super(parent, top, left, height, width);
    this.parent = parent;

    elementStartX = left + PADDING_X;
    elementStartY = top + PADDING_Y;

    elementWidth = width - (2 * PADDING_X) - SCROLLBAR_WIDTH;

    scrollbar = new ScrollbarWidget(
        this,
        (OptionRowWidget.HEIGHT + ROW_PADDING) / 2d,
        top,
        right - SCROLLBAR_WIDTH + 1,
        height,
        SCROLLBAR_WIDTH);
  }

  @Override
  public void init() {
    rows.clear();

    int currentOffset = elementStartY;
    int index = 0;
    for (var entry : parent.getModConfig().getConfigOptions().entrySet()) {
      String modId = parent.getModConfig().getModInfo().getModId();
      String groupId = entry.getKey();
      if (parent.getModConfig().getShowGroupTitles() && !groupId.equals(modId)) {
        String groupI18nKey = entry.getKey() + ".title";
        GroupTitleWidget groupTitle = new GroupTitleWidget(
            this,
            new TranslatableText(groupI18nKey),
            index++,
            currentOffset,
            elementStartX,
            elementWidth);

        currentOffset += groupTitle.height + ROW_PADDING;
        rows.add(groupTitle);
      }

      LinkedList<ConfigOption<?, ?>> configOptions = entry.getValue();
      for (ConfigOption<?, ?> configOption : configOptions) {
        OptionRowWidget optionRow = new OptionRowWidget(
            this,
            index++,
            configOption,
            currentOffset,
            elementStartX,
            elementWidth);

        currentOffset += optionRow.height + ROW_PADDING;
        rows.add(optionRow);
      }
    }

    scrollbar.setMaxPosition(currentOffset - elementStartY + PADDING_Y + 1);
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

    for (AbstractWidget<ConfigListWidget> optionRow : rows) {
      optionRow.moveTop(optionRow.getInitialTop() - (int) Math.round(scrollAmount));
    }
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderBackground(matrixStack);
    renderConfigOptionEntries(matrixStack, mouseX, mouseY, partialTicks);

    scrollbar.render(matrixStack, mouseX, mouseY, partialTicks);
  }

  @Override
  public List<Text> getTooltip(int mouseX, int mouseY, float delta) {
    return rows.stream()
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
    rows.forEach(
        row -> {
          if (row.getBottom() >= top && row.getTop() <= bottom) {
            row.render(matrixStack, mouseX, mouseY, partialTicks);
          }
        });
  }

  @Override
  public void tick() {
    rows.forEach((row) -> row.tick());
  }

  @Override
  public List<SelectableElement> getSelectableElements() {
    return rows.stream()
        .map((row) -> row.getSelectableElements())
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  public void onSetFocused(Element focused) {
    rows.stream()
        .filter((row) -> row.getSelectableElements().indexOf(focused) > -1)
        .forEach(this::ensureVisible);
  }

  public void ensureVisible(AbstractWidget<ConfigListWidget> row) {
    int rowTop = row.getTop() - PADDING_Y - 1;
    if (rowTop < top) {
      scroll(rowTop - top);
      return;
    }

    int rowBottom = row.getBottom() + PADDING_Y + 1;
    if (rowBottom > bottom) {
      scroll(rowBottom - bottom);
    }
  }

  public boolean moveFocus(int amount) {
    if (rows.isEmpty()) {
      return false;
    }

    int desiredIndex = IntStream.range(0, rows.size())
        .filter((index) -> {
          AbstractWidget<ConfigListWidget> row = rows.get(index);
          return row.getSelectableElements().contains(getConfigScreen().getFocused());
        })
        .findFirst()
        .orElse(rows.size() - 1 + amount);

    Optional<SelectableElement> desiredFocus = rows.get(desiredIndex).getPrimarySelectableElement();
    if (desiredFocus.isEmpty()) {
      return false;
    }

    getConfigScreen().setFocused(desiredFocus.get());
    return true;
  }

  public ConfigScreen getConfigScreen() {
    return getParent();
  }
}
