package me.roundaround.roundalib.mixin;

import me.roundaround.roundalib.client.event.MinecraftClientEvents;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
  @Inject(
      method = "close",
      at = @At(value = "INVOKE", target = "net/minecraft/client/resource/PeriodicNotificationManager.close()V")
  )
  private void close(CallbackInfo info) {
    MinecraftClientEvents.ON_CLOSE_EVENT_BUS.invoker().handle();
  }

  @Inject(method = "handleInputEvents", at = @At(value = "HEAD"))
  public void handleInputEvents(CallbackInfo info) {
    MinecraftClientEvents.ON_INPUT_EVENT_BUS.invoker().handle();
  }
}

