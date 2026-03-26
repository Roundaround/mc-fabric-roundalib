package me.roundaround.roundalib.client.gui.widget;

import me.roundaround.roundalib.client.gui.widget.drawable.DrawableWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class EmptyWidget extends DrawableWidget {
  public EmptyWidget() {
    super(0, 0, 0, 0);
  }

  @Override
  protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
  }
}
