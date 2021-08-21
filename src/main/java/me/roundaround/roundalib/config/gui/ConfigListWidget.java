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
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Environment(EnvType.CLIENT)
public class ConfigListWidget extends DrawableHelper implements Drawable, Element {
    private static final int SCROLLBAR_WIDTH = 8;
    private static final int ELEMENT_HEIGHT = 14;
    private static final int PADDING_X = 3;
    private static final int PADDING_Y = 4;

    private final ConfigScreen parent;
    private final List<ConfigOptionWidget> configOptionWidgets = new ArrayList<>();
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
    private boolean scrolling;
    private double scrollAmount;

    public ConfigListWidget(ConfigScreen parent, int left, int top, int width, int height) {
        this.parent = parent;
        this.left = left;
        this.top = top;
        this.elementStartX = this.left + PADDING_X;
        this.elementStartY = this.top + PADDING_Y;
        this.setSize(width, height);
    }

    public void init() {
        configOptionWidgets.clear();

        ImmutableList<ConfigOption<?>> configOptions = this.parent.getModConfig().getConfigOptions();
        IntStream.range(0, configOptions.size()).forEach(idx -> configOptionWidgets.add(new ConfigOptionWidget(this, configOptions.get(idx), elementStartX,
                this.getElementTop(idx), elementWidth, elementHeight)));
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        this.elementWidth = width - (2 * PADDING_X) - SCROLLBAR_WIDTH;
        this.elementHeight = ELEMENT_HEIGHT;
        this.bottom = this.top + this.height;
        this.right = this.left + this.width;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.left && mouseX <= this.right &&
                mouseY >= this.top && mouseY <= this.bottom;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            ConfigOptionWidget element = this.getElementAtPosition(mouseX, mouseY);
            if (element != null) {
                if (element.mouseClicked(mouseX, mouseY, button)) {
//                    this.setFocused(element);
                    return true;
                }
            }

            return this.scrolling;
        }
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
//        if (this.getFocused() != null) {
//            this.getFocused().mouseReleased(mouseX, mouseY, button);
//        }

        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && this.scrolling) {
            if (mouseY < (double) this.top) {
                this.setScrollAmount(0.0D);
            } else if (mouseY > (double) this.bottom) {
                this.setScrollAmount(this.getMaxScroll());
            } else {
                double percent = Math.max(1, this.getMaxScroll());
                int bottom = this.height;
                int top = MathHelper.clamp(((int) ((float) bottom * bottom / this.getMaxPosition())), 32, bottom - 8);
                double scaled = Math.max(1, percent / (bottom - top));
                this.setScrollAmount(this.getScrollAmount() + deltaY * scaled);
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        this.setScrollAmount(this.getScrollAmount() - amount * this.elementHeight / 2D);
        return true;
    }

    public double getScrollAmount() {
        return this.scrollAmount;
    }

    public void setScrollAmount(double amount) {
        this.scrollAmount = MathHelper.clamp(amount, 0, this.getMaxScroll());

        IntStream.range(0, configOptionWidgets.size())
                .forEach(idx -> this.configOptionWidgets.get(idx).setTop(this.getElementTop(idx)));
    }

    public int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.height - 4));
    }

    protected int getMaxPosition() {
        return this.configOptionWidgets.size() * this.elementHeight;
    }

    protected void updateScrollingState(double mouseX, double mouseY, int button) {
        this.scrolling = button == 0 && mouseX >= this.right - SCROLLBAR_WIDTH && mouseX < this.right;
    }

    private ConfigOptionWidget getElementAtPosition(double mouseX, double mouseY) {
        return null;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        this.renderBackground(tessellator, bufferBuilder);
        this.renderConfigOptionEntries(matrixStack, mouseX, mouseY, partialTicks);
        this.renderScrollbar(tessellator, bufferBuilder);
    }

    protected void renderBackground(Tessellator tessellator, BufferBuilder bufferBuilder) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(this.left, this.bottom, 0)
                .texture(this.left / 32f, (float) (this.bottom + (int) this.getScrollAmount()) / 32f)
                .color(32, 32, 32, 255)
                .next();
        bufferBuilder.vertex(this.right, this.bottom, 0)
                .texture(this.right / 32f, (float) (this.bottom + (int) this.getScrollAmount()) / 32f)
                .color(32, 32, 32, 255)
                .next();
        bufferBuilder.vertex(this.right, this.top, 0)
                .texture(this.right / 32f, (float) (this.top + (int) this.getScrollAmount()) / 32f)
                .color(32, 32, 32, 255)
                .next();
        bufferBuilder.vertex(this.left, this.top, 0)
                .texture(this.left / 32f, (float) (this.top + (int) this.getScrollAmount()) / 32f)
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

    protected void renderScrollbar(Tessellator tessellator, BufferBuilder bufferBuilder) {
        int maxScroll = this.getMaxScroll();
        if (maxScroll <= 0) {
            return;
        }

        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        int scrollbarRight = this.right;
        int scrollbarLeft = scrollbarRight - SCROLLBAR_WIDTH;

        int handleHeight = (int) ((float) this.height * this.height / this.getMaxPosition());
        handleHeight = MathHelper.clamp(handleHeight, 32, this.height - 8);

        int handleTop = (int) this.getScrollAmount() * (this.height - handleHeight) / maxScroll + this.top;
        if (handleTop < this.top) {
            handleTop = this.top;
        }

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(scrollbarLeft, this.bottom, 0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(scrollbarRight, this.bottom, 0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(scrollbarRight, this.top, 0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(scrollbarLeft, this.top, 0).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(scrollbarLeft, handleTop + handleHeight, 0).color(128, 128, 128, 255).next();
        bufferBuilder.vertex(scrollbarRight, handleTop + handleHeight, 0).color(128, 128, 128, 255).next();
        bufferBuilder.vertex(scrollbarRight, handleTop, 0).color(128, 128, 128, 255).next();
        bufferBuilder.vertex(scrollbarLeft, handleTop, 0).color(128, 128, 128, 255).next();
        bufferBuilder.vertex(scrollbarLeft, handleTop + handleHeight - 1, 0).color(192, 192, 192, 255).next();
        bufferBuilder.vertex(scrollbarRight - 1, handleTop + handleHeight - 1, 0).color(192, 192, 192, 255).next();
        bufferBuilder.vertex(scrollbarRight - 1, handleTop, 0).color(192, 192, 192, 255).next();
        bufferBuilder.vertex(scrollbarLeft, handleTop, 0).color(192, 192, 192, 255).next();
        tessellator.draw();

        RenderSystem.enableTexture();
    }

    protected void renderConfigOptionEntries(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        configOptionWidgets.forEach(configOptionWidget -> {
            if (configOptionWidget.getBottom() >= this.top && configOptionWidget.getTop() <= this.bottom) {
                configOptionWidget.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        });
    }

    private int getElementTop(int idx) {
        return elementStartY - (int) this.getScrollAmount() + idx * elementHeight;
    }
}
