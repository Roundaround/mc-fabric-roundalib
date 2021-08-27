package me.roundaround.roundalib.config.gui;

import me.roundaround.roundalib.config.gui.control.Control;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class OptionRow extends Widget<ConfigList> {
    public static final int HEIGHT = 20;
    protected static final int LABEL_COLOR = 0xFFFFFFFF;
    protected static final int HIGHLIGHT_COLOR = 0x50FFFFFF;
    protected static final int PADDING = 4;
    protected static final int CONTROL_WIDTH = 80;

    protected final ConfigOption<?> configOption;
    protected final Control<?> control;

    public OptionRow(ConfigList parent, ConfigOption<?> configOption, int top, int left, int width) {
        super(parent, top, left, HEIGHT, width);

        this.configOption = configOption;
        this.control = configOption.createControl(this, this.top, this.right - CONTROL_WIDTH - PADDING, this.height, CONTROL_WIDTH);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        this.renderLabel(matrixStack, mouseX, mouseY, partialTicks);
        this.renderControl(matrixStack, mouseX, mouseY, partialTicks);
        this.renderDecorations(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void moveTop(int top) {
        super.moveTop(top);

        this.control.moveTop(top);
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        return this.control.mouseClicked(mouseX, mouseY, button);
    }

    protected void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    }

    protected void renderLabel(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        drawTextWithShadow(matrixStack, textRenderer, this.configOption.getLabel(), this.left + PADDING, this.top + (this.height - 8) / 2, LABEL_COLOR);
    }

    protected void renderControl(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.control.render(matrixStack, mouseX, mouseY, partialTicks);
    }


    protected void renderDecorations(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            drawHorizontalLine(matrixStack, this.left - 1, this.right + 1, this.top - 1, HIGHLIGHT_COLOR);
            drawHorizontalLine(matrixStack, this.left - 1, this.right + 1, this.bottom + 1, HIGHLIGHT_COLOR);
            drawVerticalLine(matrixStack, this.left - 1, this.top - 1, this.bottom + 1, HIGHLIGHT_COLOR);
            drawVerticalLine(matrixStack, this.right + 1, this.top - 1, this.bottom + 1, HIGHLIGHT_COLOR);
        }
    }
}
