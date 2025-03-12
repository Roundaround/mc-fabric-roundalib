package me.roundaround.roundalib.mixin;

import me.roundaround.roundalib.client.event.MinecraftServerEvents;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VanillaDataPackProvider.class)
public class VanillaDataPackProviderMixin {
  @Inject(
      method = "createManager(Lnet/minecraft/world/level/storage/LevelStorage$Session;)" +
          "Lnet/minecraft/resource/ResourcePackManager;", at = @At("HEAD")
  )
  private static void beforeResourceManagerCreated(
      LevelStorage.Session session, CallbackInfoReturnable<ResourcePackManager> cir
  ) {
    MinecraftServerEvents.RESOURCE_MANAGER_CREATING.invoker().beforeResourceManagerCreated(session);
  }

  @Inject(
      method = "createManager(Lnet/minecraft/world/level/storage/LevelStorage$Session;)" +
          "Lnet/minecraft/resource/ResourcePackManager;", at = @At("RETURN")
  )
  private static void afterResourceManagerCreated(
      LevelStorage.Session session, CallbackInfoReturnable<ResourcePackManager> cir
  ) {
    MinecraftServerEvents.RESOURCE_MANAGER_CREATED.invoker().afterResourceManagerCreated(session, cir.getReturnValue());
  }
}
