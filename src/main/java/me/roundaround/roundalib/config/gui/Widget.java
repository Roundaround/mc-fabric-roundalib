package me.roundaround.roundalib.config.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Widget<T> extends DrawableHelper implements Drawable, Element {
    protected T parent;
    protected int width;
    protected int height;
    protected int top;
    protected int bottom;
    protected int left;
    protected int right;

    protected Widget(T parent, int top, int left, int height, int width) {
        this.parent = parent;
        this.top = top;
        this.bottom = top + height - 1;
        this.left = left;
        this.right = left + width - 1;
        this.height = height;
        this.width = width;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.left && mouseX <= this.right &&
                mouseY >= this.top && mouseY <= this.bottom;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.isMouseOver(mouseX, mouseY) && this.onMouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.isMouseOver(mouseX, mouseY) && this.onMouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.isMouseOver(mouseX, mouseY) && this.onMouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.isMouseOver(mouseX, mouseY) && this.onMouseScrolled(mouseX, mouseY, amount);
    }

    public void renderOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
    }

    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    public boolean onMouseScrolled(double mouseX, double mouseY, double amount) {
        return false;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTop() {
        return top;
    }

    public int getBottom() {
        return bottom;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public void moveTop(int top) {
        this.top = top;
        this.bottom = this.top + this.height - 1;
    }

    public static void drawHoverText(int x, int y, List<String> textLines, MatrixStack matrixStack) {
        // TODO: Find a way to piggyback off vanilla.

        MinecraftClient mc = MinecraftClient.getInstance();
        Screen currentScreen = mc.currentScreen;

        if (!textLines.isEmpty() && currentScreen != null) {
            TextRenderer font = mc.textRenderer;
            DiffuseLighting.disableGuiDepthLighting();
            RenderSystem.disableDepthTest();
            int maxLineLength = 0;
            int maxWidth = currentScreen.width;
            List<String> linesNew = new ArrayList<>();

            for (String lineOrig : textLines) {
                String[] lines = lineOrig.split("\\n");

                for (String line : lines) {
                    int length = font.getWidth(line);

                    if (length > maxLineLength) {
                        maxLineLength = length;
                    }

                    linesNew.add(line);
                }
            }

            textLines = linesNew;

            final int lineHeight = font.fontHeight + 1;
            int textHeight = textLines.size() * lineHeight - 2;
            int textStartX = x + 4;
            int textStartY = Math.max(8, y - textHeight - 6);

            if (textStartX + maxLineLength + 6 > maxWidth) {
                textStartX = Math.max(2, maxWidth - maxLineLength - 8);
            }

            double zLevel = 300;
            int borderColor = 0xF0100010;
            drawGradientRect(
                    textStartX - 3,
                    textStartY - 4, textStartX + maxLineLength + 3, textStartY - 3, zLevel, borderColor, borderColor);
            drawGradientRect(
                    textStartX - 3,
                    textStartY + textHeight + 3,
                    textStartX + maxLineLength + 3, textStartY + textHeight + 4, zLevel, borderColor, borderColor);
            drawGradientRect(
                    textStartX - 3,
                    textStartY - 3,
                    textStartX + maxLineLength + 3, textStartY + textHeight + 3, zLevel, borderColor, borderColor);
            drawGradientRect(
                    textStartX - 4,
                    textStartY - 3, textStartX - 3, textStartY + textHeight + 3, zLevel, borderColor, borderColor);
            drawGradientRect(
                    textStartX + maxLineLength + 3,
                    textStartY - 3,
                    textStartX + maxLineLength + 4, textStartY + textHeight + 3, zLevel, borderColor, borderColor);

            int fillColor1 = 0x505000FF;
            int fillColor2 = 0x5028007F;
            drawGradientRect(
                    textStartX - 3,
                    textStartY - 3 + 1,
                    textStartX - 3 + 1, textStartY + textHeight + 3 - 1, zLevel, fillColor1, fillColor2);
            drawGradientRect(
                    textStartX + maxLineLength + 2,
                    textStartY - 3 + 1,
                    textStartX + maxLineLength + 3, textStartY + textHeight + 3 - 1, zLevel, fillColor1, fillColor2);
            drawGradientRect(
                    textStartX - 3,
                    textStartY - 3, textStartX + maxLineLength + 3, textStartY - 3 + 1, zLevel, fillColor1, fillColor1);
            drawGradientRect(
                    textStartX - 3,
                    textStartY + textHeight + 2,
                    textStartX + maxLineLength + 3, textStartY + textHeight + 3, zLevel, fillColor2, fillColor2);

            for (String str : textLines) {
                font.drawWithShadow(matrixStack, str, textStartX, textStartY, 0xFFFFFFFF);
                textStartY += lineHeight;
            }

            RenderSystem.enableDepthTest();
            DiffuseLighting.enableGuiDepthLighting();
        }
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, double zLevel, int startColor, int endColor) {
        int sa = (startColor >> 24 & 0xFF);
        int sr = (startColor >> 16 & 0xFF);
        int sg = (startColor >> 8 & 0xFF);
        int sb = (startColor & 0xFF);

        int ea = (endColor >> 24 & 0xFF);
        int er = (endColor >> 16 & 0xFF);
        int eg = (endColor >> 8 & 0xFF);
        int eb = (endColor & 0xFF);

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.applyModelViewMatrix();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        buffer.vertex(right, top, zLevel).color(sr, sg, sb, sa).next();
        buffer.vertex(left, top, zLevel).color(sr, sg, sb, sa).next();
        buffer.vertex(left, bottom, zLevel).color(er, eg, eb, ea).next();
        buffer.vertex(right, bottom, zLevel).color(er, eg, eb, ea).next();

        tessellator.draw();

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }
}
