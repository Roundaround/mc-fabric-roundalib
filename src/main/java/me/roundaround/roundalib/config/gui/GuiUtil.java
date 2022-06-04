package me.roundaround.roundalib.config.gui;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

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

  @Nullable
  public static Screen getCurrentScreen() {
    return MINECRAFT.currentScreen;
  }
}