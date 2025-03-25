package me.roundaround.roundalib;

import me.roundaround.roundalib.sync.SyncedData;
import me.roundaround.roundalib.sync.SyncedDataMap;
import me.roundaround.roundalib.sync.SyncedDataStorage;
import me.roundaround.roundalib.sync.host.SyncedDataHost;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements SyncedDataHost<BlockEntity> {
  @Unique
  private final SyncedDataMap<BlockEntity> syncedData = new SyncedDataMap<>();

  @Shadow
  public abstract @Nullable World getWorld();

  @Shadow
  public abstract BlockPos getPos();

  @Shadow
  public abstract void markDirty();

  @Inject(method = "read", at = @At("RETURN"))
  private void readSyncedDataFromPersistentState(
      NbtCompound nbt,
      RegistryWrapper.WrapperLookup registries,
      CallbackInfo ci
  ) {
    World world = this.getWorld();
    if (world == null || world.isClient()) {
      return;
    }

    this.syncedData.clear();

    SyncedDataStorage storage = SyncedDataStorage.getInstance((ServerWorld) world);
    SyncedDataMap<BlockEntity> storedData = storage.getBlockEntitySyncedData(this.getPos());
    if (storedData != null) {
      this.syncedData.putAll(storedData);
    }

    if (this.roundalib$shouldSendUpdates()) {
      this.syncedData.forEachUntyped(this::roundalib$sendUpdate);
    }
  }

  @Inject(method = "createNbt", at = @At("RETURN"))
  private void afterCreatingNbt(RegistryWrapper.WrapperLookup registries, CallbackInfoReturnable<NbtCompound> cir) {
    World world = this.getWorld();
    if (world == null || world.isClient()) {
      return;
    }

    SyncedDataStorage storage = SyncedDataStorage.getInstance((ServerWorld) world);
    this.roundalib$initSynced(storage.getBlockEntitySyncedData(this.getPos()));
  }

  @Override
  public SyncedDataMap<BlockEntity> roundalib$getSyncedMap() {
    return this.syncedData;
  }

  @Override
  public <T> T roundalib$setSynced(SyncedData<T, BlockEntity> definition, T value) {
    this.markDirty();
    return SyncedDataHost.super.roundalib$setSynced(definition, value);
  }

  @Override
  public boolean roundalib$shouldSendUpdates() {
    World world = this.getWorld();
    return world != null && !world.isClient();
  }

  @Override
  public <T> void roundalib$sendUpdate(SyncedData<T, BlockEntity> definition, T value) {
    // TODO: ClientNetworking.sendDataSync(this.getPos(), definition, value);
  }
}
