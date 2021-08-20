package me.roundaround.roundalib.config.gui;

import com.google.common.collect.ImmutableList;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Environment(EnvType.CLIENT)
public class ConfigListWidget extends DrawableHelper implements Drawable {
    private static final int SCROLLBAR_WIDTH = 14;
    private static final int ELEMENT_HEIGHT = 14;
    private static final int PADDING_X = 3;
    private static final int PADDING_Y = 4;

    private final ConfigScreen parent;
    private final int posX;
    private final int posY;
    private final int elementStartX;
    private final int elementStartY;
    private int width;
    private int height;
    private int elementWidth;
    private int elementHeight;
    private final List<ConfigOptionWidget> configOptionWidgets = new ArrayList<>();

    public ConfigListWidget(ConfigScreen parent, int posX, int posY, int width, int height) {
        this.parent = parent;
        this.posX = posX;
        this.posY = posY;
        this.elementStartX = this.posX + PADDING_X;
        this.elementStartY = this.posY + PADDING_Y;
        this.setSize(width, height);
    }

    public void init() {
        configOptionWidgets.clear();

        ImmutableList<ConfigOption<?>> configOptions = this.parent.getModConfig().getConfigOptions();
        IntStream.range(0, configOptions.size()).forEach(i -> {
            configOptionWidgets.add(new ConfigOptionWidget(this, configOptions.get(i), elementStartX,
                    elementStartY + i * elementHeight, elementWidth, elementHeight));
        });
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        this.elementWidth = width - SCROLLBAR_WIDTH;
        this.elementHeight = ELEMENT_HEIGHT;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        configOptionWidgets.forEach(configOptionWidget -> configOptionWidget.render(matrixStack, mouseX, mouseY, partialTicks));
    }
}
