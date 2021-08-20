package me.roundaround.roundalib.config.gui;

import me.roundaround.roundalib.config.option.ConfigOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ConfigOptionWidget extends DrawableHelper implements Drawable {
    private static final int LABEL_COLOR = 0xFFFFFFFF;

    private final ConfigListWidget parent;
    private final ConfigOption<?> configOption;
    private final int posX;
    private final int posY;
    private final int width;
    private final int height;

    public ConfigOptionWidget(ConfigListWidget parent, ConfigOption<?> configOption, int posX, int posY, int width, int height) {
        this.parent = parent;
        this.configOption = configOption;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        drawTextWithShadow(matrixStack, MinecraftClient.getInstance().textRenderer, this.configOption.getLabel(), this.posX, this.posY, LABEL_COLOR);
    }
}
