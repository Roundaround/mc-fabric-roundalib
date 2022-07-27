package me.roundaround.roundalib.config.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvent;

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

  public static void playSoundEvent(SoundEvent soundEvent) {
    getSoundManager().play(PositionedSoundInstance.master(soundEvent, 1));
  }

  public static TextRenderer getTextRenderer() {
    return MINECRAFT.textRenderer;
  }

  public static int genColorInt(float r, float g, float b, float a) {
    return ((int) (a * 255) << 24) | ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
  }
}
