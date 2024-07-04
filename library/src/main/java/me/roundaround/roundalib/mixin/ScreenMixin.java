package me.roundaround.roundalib.mixin;

import me.roundaround.roundalib.client.event.ScreenInputEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class ScreenMixin {
  @Shadow
  protected MinecraftClient client;

  @Inject(method = "keyPressed", at = @At(value = "HEAD"), cancellable = true)
  public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info) {
    if (ScreenInputEvent.EVENT.invoker().handle((Screen) (Object) this, keyCode, scanCode, modifiers)) {
      info.setReturnValue(true);
    }
  }
}
