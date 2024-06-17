package me.roundaround.roundalib.mixin;

import me.roundaround.roundalib.client.gui.GuiUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClickableWidget.class)
public abstract class ClickableWidgetMixin {
  @Redirect(
      method = "render",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;scissorContains(II)Z")
  )
  private boolean scissorContainsAlt(DrawContext context, int mouseX, int mouseY) {
    return GuiUtil.scissorContainsIncludingScroll(context, mouseX, mouseY);
  }
}
