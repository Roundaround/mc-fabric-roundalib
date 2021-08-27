package me.roundaround.roundalib.config.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ResetButton extends Widget<OptionRow> {
    public static final int HEIGHT = 12;
    public static final int WIDTH = 12;
    protected static final Identifier TEXTURE = new Identifier("roundalib", "textures/gui.png");

    protected ResetButton(OptionRow parent, int top, int left) {
        super(parent, top, left, HEIGHT, WIDTH);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.applyModelViewMatrix();

        int u = this.getImageOffset(this.isMouseOver(mouseX, mouseY)) * WIDTH;
        int v = HEIGHT;

        drawTexture(matrixStack, this.left, this.top, u, v, WIDTH, HEIGHT);
    }

    @Override
    public List<Text> getTooltip(int mouseX, int mouseY, float delta) {
        if (this.isDisabled() || !this.isMouseOver(mouseX, mouseY)) {
            return List.of();
        }

        return List.of(new LiteralText("Reset to default"));
    }

    @Override
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (this.isDisabled()) {
            return false;
        }

        this.parent.getConfigOption().resetToDefault();
        SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
        soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1));
        return true;
    }

    protected boolean isDisabled() {
        return !this.parent.getConfigOption().isModified();
    }

    protected int getImageOffset(boolean hovered) {
        if (this.isDisabled()) {
            return 0;
        } else if (hovered) {
            return 2;
        }

        return 1;
    }
}
