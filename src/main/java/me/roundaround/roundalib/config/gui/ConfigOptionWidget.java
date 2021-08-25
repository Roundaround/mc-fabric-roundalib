package me.roundaround.roundalib.config.gui;

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
public class ConfigOptionWidget extends DrawableHelper implements Drawable, Element {
    public static final int HEIGHT = 14;
    private static final int LABEL_COLOR = 0xFFFFFFFF;
    private static final int PADDING_LEFT = 4;

    private final ConfigListWidget parent;
    private final ConfigOption<?> configOption;
    private final int width;
    private final int height;
    private int top;
    private int bottom;
    private int left;
    private int right;

    public ConfigOptionWidget(ConfigListWidget parent, ConfigOption<?> configOption, int left, int top, int width) {
        this.parent = parent;
        this.configOption = configOption;
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
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int textY = (int)(this.top + (this.height / 2f) - (textRenderer.fontHeight / 2f));
        drawTextWithShadow(matrixStack, textRenderer, this.configOption.getLabel(), this.left + PADDING_LEFT, textY, LABEL_COLOR);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.left && mouseX <= this.right &&
                mouseY >= this.top && mouseY <= this.bottom;
    }
}
