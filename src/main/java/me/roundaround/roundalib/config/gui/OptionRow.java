package me.roundaround.roundalib.config.gui;

import me.roundaround.roundalib.config.gui.ConfigList;
import me.roundaround.roundalib.config.gui.control.Control;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class OptionRow extends DrawableHelper implements Drawable, Element {
    public static final int HEIGHT = 14;
    protected static final int LABEL_COLOR = 0xFFFFFFFF;
    protected static final int PADDING_LEFT = 4;

    protected final ConfigList parent;
    protected final ConfigOption<?> configOption;
    protected final Control<?> control;
    protected final int width;
    protected final int height;
    protected int top;
    protected int bottom;
    protected int left;
    protected int right;

    public OptionRow(ConfigList parent, ConfigOption<?> configOption, int left, int top, int width) {
        this.parent = parent;
        this.configOption = configOption;
        this.control = configOption.createControl();
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = HEIGHT;
        this.right = this.left + this.width;
        this.bottom = this.top + this.height;
    }

    public void setTop(int top) {
        this.top = top;
        this.bottom = this.top + this.height;
    }

    public int getTop() {
        return this.top;
    }

    public int getBottom() {
        return this.bottom;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        this.renderLabel(matrixStack, mouseX, mouseY, partialTicks);
        this.renderControl(matrixStack, mouseX, mouseY, partialTicks);
        this.renderDecorations(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.left && mouseX <= this.right &&
                mouseY >= this.top && mouseY <= this.bottom;
    }

    protected void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    }

    protected void renderLabel(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int textY = (int) (this.top + (this.height / 2f) - (textRenderer.fontHeight / 2f));
        drawTextWithShadow(matrixStack, textRenderer, this.configOption.getLabel(),
                this.left + PADDING_LEFT, textY, LABEL_COLOR);
    }

    protected void renderControl(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    }

    protected void renderDecorations(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    }
}
