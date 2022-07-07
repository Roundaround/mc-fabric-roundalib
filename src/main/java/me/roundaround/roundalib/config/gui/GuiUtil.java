package me.roundaround.roundalib.config.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.SoundManager;

public class GuiUtil {
  public static int LABEL_COLOR = 0xFFFFFFFF;
  public static int ERROR_COLOR = 0xFFFF0000;

  private static final MinecraftClient MINECRAFT = MinecraftClient.getInstance();

  public static int getScaledWindowWidth() {
    return MINECRAFT.getWindow().getScaledWidth();
  }

  public static int getScaledWindowHeight() {
    return MINECRAFT.getWindow().getScaledHeight();
  }

  public static int getDisplayWidth() {
    return MINECRAFT.getWindow().getWidth();
  }

  public static int getDisplayHeight() {
    return MINECRAFT.getWindow().getHeight();
  }

  public static Screen getCurrentScreen() {
    return MINECRAFT.currentScreen;
  }

  public static void setScreen(Screen screen) {
    MINECRAFT.setScreen(screen);
  }

  public static SoundManager getSoundManager() {
    return MINECRAFT.getSoundManager();
  }

  public static TextRenderer getTextRenderer() {
    return MINECRAFT.textRenderer;
  }
}
