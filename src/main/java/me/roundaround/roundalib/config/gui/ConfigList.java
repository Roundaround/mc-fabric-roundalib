package me.roundaround.roundalib.config.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Environment(EnvType.CLIENT)
public class ConfigList extends Widget<ConfigScreen> implements Scrollable {
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int PADDING_X = 4;
    private static final int PADDING_Y = 4;
    private static final int ROW_PADDING = 2;

    private final ConfigScreen parent;
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

        this.elementStartX = this.left + PADDING_X;
        this.elementStartY = this.top + PADDING_Y;

        this.elementWidth = this.width - (2 * PADDING_X) - SCROLLBAR_WIDTH;
        this.elementHeight = OptionRow.HEIGHT;

        this.scrollbar = new Scrollbar(this, (this.elementHeight + ROW_PADDING) / 2d, this.top, this.right - SCROLLBAR_WIDTH + 1, this.height, SCROLLBAR_WIDTH);
    }

    public void init() {
        optionRows.clear();

        ImmutableList<ConfigOption<?>> configOptions = this.parent.getModConfig().getConfigOptions();
        IntStream.range(0, configOptions.size()).forEach(idx ->
                optionRows.add(new OptionRow(this, configOptions.get(idx), this.getElementTop(idx), elementStartX, elementWidth)));

        this.scrollbar.setMaxPosition(this.optionRows.size() * (this.elementHeight + ROW_PADDING) + PADDING_Y - ROW_PADDING + 1);
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (this.scrollbar.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        OptionRow element = this.getElementAtPosition(mouseX, mouseY);
        if (element != null) {
            return element.mouseClicked(mouseX, mouseY, button);
        }

        return false;
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
//        if (this.getFocused() != null) {
//            this.getFocused().mouseReleased(mouseX, mouseY, button);
//        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrollbar.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double amount) {
        return this.scrollbar.onMouseScrolled(mouseX, mouseY, amount);
    }

    public void setScrollAmount(double amount) {
        this.scrollAmount = amount;

        IntStream.range(0, optionRows.size())
                .forEach(idx -> this.optionRows.get(idx).moveTop(this.getElementTop(idx)));
    }

    private OptionRow getElementAtPosition(double mouseX, double mouseY) {
        for (OptionRow optionRow : this.optionRows) {
            if (optionRow.isMouseOver(mouseX, mouseY)) {
                return optionRow;
            }
        }
        return null;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        this.renderConfigOptionEntries(matrixStack, mouseX, mouseY, partialTicks);

        this.scrollbar.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        this.renderConfigOptionEntryOverlays(matrixStack, mouseX, mouseY, delta);
    }

    protected void renderBackground() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(this.left, this.bottom, 0)
                .texture(this.left / 32f, (float) (this.bottom + (int) this.scrollAmount) / 32f)
                .color(32, 32, 32, 255)
                .next();
        bufferBuilder.vertex(this.right, this.bottom, 0)
                .texture(this.right / 32f, (float) (this.bottom + (int) this.scrollAmount) / 32f)
                .color(32, 32, 32, 255)
                .next();
        bufferBuilder.vertex(this.right, this.top, 0)
                .texture(this.right / 32f, (float) (this.top + (int) this.scrollAmount) / 32f)
                .color(32, 32, 32, 255)
                .next();
        bufferBuilder.vertex(this.left, this.top, 0)
                .texture(this.left / 32f, (float) (this.top + (int) this.scrollAmount) / 32f)
                .color(32, 32, 32, 255)
                .next();
        tessellator.draw();

        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(this.left, this.top + PADDING_Y, 0).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(this.right, this.top + PADDING_Y, 0).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(this.right, this.top, 0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.left, this.top, 0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.left, this.bottom, 0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.right, this.bottom, 0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(this.right, this.bottom - PADDING_Y, 0).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(this.left, this.bottom - PADDING_Y, 0).color(0, 0, 0, 0).next();
        tessellator.draw();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    protected void renderConfigOptionEntries(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        optionRows.forEach(optionRow -> {
            if (optionRow.getBottom() >= this.top && optionRow.getTop() <= this.bottom) {
                optionRow.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        });
    }

    protected void renderConfigOptionEntryOverlays(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        optionRows.forEach(optionRow -> {
            if (optionRow.getBottom() >= this.top && optionRow.getTop() <= this.bottom) {
                optionRow.renderOverlay(matrixStack, mouseX, mouseY, partialTicks);
            }
        });
    }

    private int getElementTop(int idx) {
        return elementStartY - (int) this.scrollAmount + idx * (elementHeight + ROW_PADDING);
    }
}
