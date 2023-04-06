package me.roundaround.testmod.client.gui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
  private final Screen parent;

  public ConfigScreen(Screen parent) {
    super(Text.literal("RoundaLib Test Mod Config"));
    this.parent = parent;
  }

  @Override
  public void close() {
    this.client.setScreen(parent);
  }
}
