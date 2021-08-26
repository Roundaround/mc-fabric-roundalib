package me.roundaround.roundalib.config.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Environment(EnvType.CLIENT)
public class ConfigList extends DrawableHelper implements Drawable, Element, Scrollable {
    private static final int SCROLLBAR_WIDTH = 8;
    private static final int PADDING_X = 3;
    private static final int PADDING_Y = 4;

    private final ConfigScreen parent;
    private final Scrollbar scrollbar;
    private final List<OptionRow> optionRows = new ArrayList<>();
    private int width;
    private int height;
    private int top;
    private int bottom;
    private int left;
    private int right;
    private final int elementStartX;
    private final int elementStartY;
    private int elementWidth;
    private int elementHeight;
    private double scrollAmount;

    public ConfigList(ConfigScreen parent, int left, int top, int width, int height) {
        this.parent = parent;
        this.scrollbar = new Scrollbar(this, OptionRow.HEIGHT / 2d);

        this.left = left;
        this.top = top;
        this.elementStartX = this.left + PADDING_X;
        this.elementStartY = this.top + PADDING_Y;
        this.setSize(width, height);
    }

    public void init() {
        optionRows.clear();

        ImmutableList<ConfigOption<?>> configOptions = this.parent.getModConfig().getConfigOptions();
        IntStream.range(0, configOptions.size()).forEach(idx -> optionRows.add(new OptionRow(this, configOptions.get(idx), elementStartX,
                this.getElementTop(idx), elementWidth)));

        this.scrollbar.setMaxPosition(this.optionRows.size() * this.elementHeight);
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        this.elementWidth = width - (2 * PADDING_X) - SCROLLBAR_WIDTH;
        this.elementHeight = OptionRow.HEIGHT;
        this.bottom = this.top + this.height;
        this.right = this.left + this.width;

        this.scrollbar.setBoundingBox(this.top, this.bottom, this.right - SCROLLBAR_WIDTH, this.right);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.left && mouseX <= this.right &&
                mouseY >= this.top && mouseY <= this.bottom;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        }

        if (this.scrollbar.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        OptionRow element = this.getElementAtPosition(mouseX, mouseY);
        if (element != null) {
            if (element.mouseClicked(mouseX, mouseY, button)) {
//                    this.setFocused(element);
                return true;
            }
        }

        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
//        if (this.getFocused() != null) {
//            this.getFocused().mouseReleased(mouseX, mouseY, button);
//        }

        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrollbar.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }

        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.scrollbar.mouseScrolled(mouseX, mouseY, amount);
    }

    public void setScrollAmount(double amount) {
        this.scrollAmount = amount;

        IntStream.range(0, optionRows.size())
                .forEach(idx -> this.optionRows.get(idx).setTop(this.getElementTop(idx)));
    }

    private OptionRow getElementAtPosition(double mouseX, double mouseY) {
        for (int i = 0; i < this.optionRows.size(); i++) {
            OptionRow optionRow = this.optionRows.get(i);
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

    private int getElementTop(int idx) {
        return elementStartY - (int) this.scrollAmount + idx * elementHeight;
    }
}
