package me.roundaround.roundalib.config.gui;

import me.roundaround.roundalib.config.ModConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

public class ConfigScreen extends Screen {
    @Nullable
    private final Screen parent;
    private final ModConfig modConfig;

    public ConfigScreen(@Nullable Screen parent, ModConfig modConfig) {
        super(new TranslatableText(modConfig.getModInfo().getConfigScreenTitleI18nKey()));
        this.parent = parent;
        this.modConfig = modConfig;
    }

    @Override
    public void removed() {
        if (this.client == null) {
            return;
        }
        this.client.options.write();
    }

    @Override
    public void onClose() {
        if (this.client == null) {
            return;
        }
        this.client.setScreen(this.parent);
    }
}
