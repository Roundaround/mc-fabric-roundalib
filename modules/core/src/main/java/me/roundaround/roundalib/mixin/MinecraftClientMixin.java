package me.roundaround.roundalib.mixin;

import me.roundaround.roundalib.event.MinecraftClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
  @Unique
  private Minecraft self() {
    return (Minecraft) (Object) this;
  }

  @Inject(method = "<init>", at = @At("TAIL"))
  private void afterInit(GameConfig args, CallbackInfo ci) {
    MinecraftClientEvents.INIT.invoker().onInit(this.self());
  }

  @Inject(method = "close", at = @At(value = "HEAD"))
  private void close(CallbackInfo info) {
    MinecraftClientEvents.CLOSE.invoker().onClose(this.self());
  }

  @Inject(method = "handleKeybinds", at = @At(value = "HEAD"))
  public void handleInputEvents(CallbackInfo info) {
    MinecraftClientEvents.HANDLE_INPUT.invoker().onHandleInput(this.self());
  }
}

