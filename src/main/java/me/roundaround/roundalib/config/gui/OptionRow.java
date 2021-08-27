package me.roundaround.roundalib.config.gui;

import com.google.common.collect.ImmutableList;
import me.roundaround.roundalib.config.gui.control.Control;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class OptionRow extends Widget<ConfigList> {
    public static final int HEIGHT = 20;
    protected static final int ROW_SHADE = 0x81000000;
    protected static final int LABEL_COLOR = 0xFFFFFFFF;
    protected static final int HIGHLIGHT_COLOR = 0x50FFFFFF;
    protected static final int PADDING = 4;
    protected static final int CONTROL_WIDTH = 80;

    protected final int index;
    protected final ConfigOption<?> configOption;
    protected final Control<?> control;
    protected final ResetButton resetButton;

    private final ImmutableList<Widget<?>> subWidgets;

    public OptionRow(ConfigList parent, int index, ConfigOption<?> configOption, int top, int left, int width) {
        super(parent, top, left, HEIGHT, width);

        this.index = index;
        this.configOption = configOption;
        this.control = configOption.createControl(this, this.top,
                this.right - CONTROL_WIDTH - ResetButton.WIDTH - (PADDING * 2), this.height, CONTROL_WIDTH);
        this.resetButton = new ResetButton(this,
                this.top + (HEIGHT - ResetButton.HEIGHT) / 2, this.right - PADDING - ResetButton.WIDTH);

        this.subWidgets = ImmutableList.of(this.control, this.resetButton);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack, mouseX, mouseY, partialTicks);
        this.renderLabel(matrixStack, mouseX, mouseY, partialTicks);
        this.renderControl(matrixStack, mouseX, mouseY, partialTicks);
        this.renderResetButton(matrixStack, mouseX, mouseY, partialTicks);
        this.renderDecorations(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public List<Text> getTooltip(int mouseX, int mouseY, float delta) {
        return this.subWidgets.stream()
                .map(subWidget -> subWidget.getTooltip(mouseX, mouseY, delta))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public void moveTop(int top) {
        super.moveTop(top);

        this.control.moveTop(top);
        this.resetButton.moveTop(top + (HEIGHT - ResetButton.HEIGHT) / 2);
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        for (Widget<?> subWidget : subWidgets) {
            if (subWidget.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    protected void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (index % 2 == 0) {
            fill(matrixStack, this.left - 1, this.top - 1, this.right + 2, this.bottom + 2, ROW_SHADE);
        }

        if (this.isMouseOver(mouseX, mouseY)) {
            drawHorizontalLine(matrixStack, this.left - 1, this.right + 1, this.top - 1, HIGHLIGHT_COLOR);
            drawHorizontalLine(matrixStack, this.left - 1, this.right + 1, this.bottom + 1, HIGHLIGHT_COLOR);
            drawVerticalLine(matrixStack, this.left - 1, this.top - 1, this.bottom + 1, HIGHLIGHT_COLOR);
            drawVerticalLine(matrixStack, this.right + 1, this.top - 1, this.bottom + 1, HIGHLIGHT_COLOR);
        }
    }

    protected void renderLabel(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        drawTextWithShadow(matrixStack, textRenderer, this.configOption.getLabel(),
                this.left + PADDING, this.top + (this.height - 8) / 2, LABEL_COLOR);
    }

    protected void renderControl(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.control.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    protected void renderResetButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.resetButton.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    protected void renderDecorations(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
//        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
//            drawHorizontalLine(matrixStack, this.left - 1, this.right + 1, this.top - 1, HIGHLIGHT_COLOR);
//            drawHorizontalLine(matrixStack, this.left - 1, this.right + 1, this.bottom + 1, HIGHLIGHT_COLOR);
//            drawVerticalLine(matrixStack, this.left - 1, this.top - 1, this.bottom + 1, HIGHLIGHT_COLOR);
//            drawVerticalLine(matrixStack, this.right + 1, this.top - 1, this.bottom + 1, HIGHLIGHT_COLOR);
//        }
    }

    public ConfigOption<?> getConfigOption() {
        return configOption;
    }

    public Control<?> getControl() {
        return control;
    }
}
