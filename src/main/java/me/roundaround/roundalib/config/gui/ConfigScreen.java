package me.roundaround.roundalib.config.gui;

import me.roundaround.roundalib.config.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
    private static final int TITLE_COLOR = 0xFFFFFFFF;
    private static final int PADDING_X = 10;
    private static final int OFFSET_Y = 40;
    private static final int TITLE_POS_Y = 5;

    @Nullable
    private final Screen parent;
    private final ModConfig modConfig;

    private ConfigListWidget listWidget;

    public ConfigScreen(@Nullable Screen parent, ModConfig modConfig) {
        super(new TranslatableText(modConfig.getModInfo().getConfigScreenTitleI18nKey()));
        this.parent = parent;
        this.modConfig = modConfig;

        this.recreateListWidget();
    }

    @Override
    protected void init() {
        super.init();

        if (this.listWidget != null) {
            this.listWidget.setSize(this.width - (PADDING_X * 2), this.height - OFFSET_Y);
            this.listWidget.init();
        }
    }

    @Override
    public void removed() {
        this.modConfig.saveToFile();
    }

    @Override
    public void onClose() {
        if (this.client == null) {
            return;
        }
        this.client.setScreen(this.parent);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        if (this.parent != null) {
            this.parent.resize(client, width, height);
        }

        super.resize(client, width, height);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        this.renderTitle(matrixStack);

        if (listWidget != null) {
            listWidget.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    public void renderTitle(MatrixStack matrixStack) {
        drawCenteredText(matrixStack, this.textRenderer, this.title, this.width / 2, TITLE_POS_Y, TITLE_COLOR);
    }

    public ModConfig getModConfig() {
        return this.modConfig;
    }

    private void recreateListWidget() {
        listWidget = new ConfigListWidget(this, PADDING_X, OFFSET_Y,
                this.width - (PADDING_X * 2), this.height - OFFSET_Y);
    }
}
