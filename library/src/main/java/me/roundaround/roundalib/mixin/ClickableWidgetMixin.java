package me.roundaround.roundalib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.roundaround.roundalib.client.gui.GuiUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClickableWidget.class)
public abstract class ClickableWidgetMixin {
  @Shadow
  public abstract int getX();

  @Shadow
  public abstract int getY();

  @Shadow
  public abstract int getWidth();

  @Shadow
  public abstract int getHeight();

  @Shadow
  protected boolean hovered;

  @Redirect(
      method = "render", at = @At(
      value = "FIELD", target = "Lnet/minecraft/client/gui/widget/ClickableWidget;hovered:Z", opcode = Opcodes.PUTFIELD
  )
  )
  private void isHoveredAlt(
      ClickableWidget target,
      boolean original,
      @Local(argsOnly = true) DrawContext context,
      @Local(argsOnly = true, ordinal = 0) int mouseX,
      @Local(argsOnly = true, ordinal = 1) int mouseY
  ) {
    if (!GuiUtil.SCROLL_TRACKER.isInScrollingContext()) {
      this.hovered = original;
      return;
    }

    int effectiveMouseY = mouseY + GuiUtil.SCROLL_TRACKER.getScrollAmount();
    this.hovered = context.scissorContains(mouseX, mouseY) && mouseX >= this.getX() && effectiveMouseY >= this.getY() &&
        mouseX < this.getX() + this.getWidth() && effectiveMouseY < this.getY() + this.getHeight();
  }
}
