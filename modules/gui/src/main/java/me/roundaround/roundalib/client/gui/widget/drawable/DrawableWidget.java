package me.roundaround.roundalib.client.gui.widget.drawable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public abstract class DrawableWidget extends AbstractWidget {
  public DrawableWidget() {
    this(CommonComponents.EMPTY);
  }

  public DrawableWidget(Component message) {
    this(0, 0, message);
  }

  public DrawableWidget(int width, int height) {
    this(width, height, CommonComponents.EMPTY);
  }

  public DrawableWidget(int width, int height, Component message) {
    this(0, 0, width, height, message);
  }

  public DrawableWidget(int x, int y, int width, int height) {
    this(x, y, width, height, CommonComponents.EMPTY);
  }

  public DrawableWidget(int x, int y, int width, int height, Component message) {
    super(x, y, width, height, message);
  }

  @Override
  protected void updateWidgetNarration(NarrationElementOutput builder) {
  }

  @Override
  protected boolean isValidClickButton(MouseButtonInfo input) {
    return false;
  }

  @Override
  @Nullable
  public ComponentPath nextFocusPath(FocusNavigationEvent navigation) {
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
