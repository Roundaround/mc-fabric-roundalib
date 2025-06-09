package me.roundaround.roundalib.client.gui.screen;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class BaseScreen extends Screen {
  protected final @NotNull ScreenParent parent;
  protected final @NotNull MinecraftClient client;

  public BaseScreen(
      @NotNull Text title,
      @NotNull ScreenParent parent,
      @NotNull MinecraftClient client) {
    super(title);
    this.parent = parent;
    this.client = client;
  }

  @Override
  public void close() {
    this.client.setScreen(this.parent.get());
  }

  protected void navigateTo(Screen screen) {
    this.client.setScreen(screen);
  }

  protected void done(ButtonWidget button) {
    this.close();
  }
}
