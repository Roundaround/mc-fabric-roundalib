package me.roundaround.roundalib.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;

public class GuiUtil {
    public static int getScaledWindowWidth() {
        return MinecraftClient.getInstance().getWindow().getScaledWidth();
    }

    public static int getScaledWindowHeight() {
        return MinecraftClient.getInstance().getWindow().getScaledHeight();
    }

    public static int getDisplayWidth() {
        return MinecraftClient.getInstance().getWindow().getWidth();
    }

    public static int getDisplayHeight() {
        return MinecraftClient.getInstance().getWindow().getHeight();
    }

    @Nullable
    public static Screen getCurrentScreen() {
        return MinecraftClient.getInstance().currentScreen;
    }
}
