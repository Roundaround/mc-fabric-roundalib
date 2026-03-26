package me.roundaround.roundalib.mixin;

import me.roundaround.roundalib.event.ResourceManagerEvents;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPacksSource.class)
public class VanillaDataPackProviderMixin {
  @Inject(
      method = "createPackRepository(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;)Lnet/minecraft/server/packs/repository/PackRepository;", at = @At("HEAD")
  )
  private static void beforeResourceManagerCreated(
      LevelStorageSource.LevelStorageAccess session,
      CallbackInfoReturnable<PackRepository> cir
  ) {
    ResourceManagerEvents.CREATING.invoker().beforeResourceManagerCreated(session);
  }

  @Inject(
      method = "createPackRepository(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;)Lnet/minecraft/server/packs/repository/PackRepository;", at = @At("RETURN")
  )
  private static void afterResourceManagerCreated(
      LevelStorageSource.LevelStorageAccess session,
      CallbackInfoReturnable<PackRepository> cir
  ) {
    ResourceManagerEvents.CREATED.invoker().afterResourceManagerCreated(session, cir.getReturnValue());
  }
}
