package me.roundaround.roundalib.config.gui;

import me.roundaround.roundalib.config.option.ConfigOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ConfigOptionWidget extends DrawableHelper implements Drawable, Element {
    private static final int LABEL_COLOR = 0xFFFFFFFF;

    private final ConfigListWidget parent;
    private final ConfigOption<?> configOption;
    private final int posX;
    private final int posY;
    private final int width;
    private final int height;
    private final int top;
    private final int bottom;
    private final int left;
    private final int right;

    public ConfigOptionWidget(ConfigListWidget parent, ConfigOption<?> configOption, int posX, int posY, int width, int height) {
        this.parent = parent;
        this.configOption = configOption;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.left = this.posX;
        this.right = this.posX + this.width;
        this.top = this.posY;
        this.bottom = this.posY + this.height;
    }

    public int getTop() {
        return this.top - (int)this.parent.getScrollAmount();
    }

    public int getBottom() {
        return this.bottom - (int)this.parent.getScrollAmount();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        matrixStack.push();
        matrixStack.translate(0, -this.parent.getScrollAmount(), 0);

        drawTextWithShadow(matrixStack, MinecraftClient.getInstance().textRenderer, this.configOption.getLabel(), this.posX, this.posY, LABEL_COLOR);

        matrixStack.pop();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.left && mouseX <= this.right &&
                mouseY >= this.getTop() && mouseY <= this.getBottom();
    }
}
