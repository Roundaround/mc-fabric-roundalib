package me.roundaround.roundalib.mixin;

import me.roundaround.roundalib.client.event.MinecraftClientEvents;
import net.minecraft.client.render.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {
  @Inject(method = "<init>", at = @At(value = "TAIL"))
  private void constructor(CallbackInfo info) {
    MinecraftClientEvents.AFTER_INIT_EVENT_BUS.invoker().handle();
  }
}
