package me.roundaround.roundalib.client.gui.widget.drawable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public abstract class DrawableWidget extends ClickableWidget {
  public DrawableWidget() {
    this(ScreenTexts.EMPTY);
  }

  public DrawableWidget(Text message) {
    this(0, 0, message);
  }

  public DrawableWidget(int width, int height) {
    this(width, height, ScreenTexts.EMPTY);
  }

  public DrawableWidget(int width, int height, Text message) {
    this(0, 0, width, height, message);
  }

  public DrawableWidget(int x, int y, int width, int height) {
    this(x, y, width, height, ScreenTexts.EMPTY);
  }

  public DrawableWidget(int x, int y, int width, int height, Text message) {
    super(x, y, width, height, message);
  }

  @Override
  protected void appendClickableNarrations(NarrationMessageBuilder builder) {
  }

  @Override
  public boolean isNarratable() {
    return false;
  }

  @Override
  protected boolean isValidClickButton(int button) {
    return false;
  }

  @Override
  @Nullable
  public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
    return null;
  }

  @Override
  public boolean isFocused() {
    return false;
  }

  @Override
  public void setFocused(boolean focused) {
  }
}
