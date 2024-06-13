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
    // Grab screen instance from client so that we don't have to do any gross casting.
    Screen screen = this.client.currentScreen;
    if (ScreenInputEvent.EVENT_BUS.invoker().handle(screen, keyCode, scanCode, modifiers)) {
      info.setReturnValue(true);
    }
  }
}
