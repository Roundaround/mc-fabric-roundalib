package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.client.gui.widget.drawable.DrawableWidget;
import net.minecraft.client.gui.DrawContext;

public class EmptyWidget extends DrawableWidget {
  public EmptyWidget() {
    super(0, 0, 0, 0);
  }

  @Override
  protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
  }
}
