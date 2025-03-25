package me.roundaround.roundalib.sync;

import com.mojang.serialization.Codec;
import me.roundaround.roundalib.generated.Constants;
import me.roundaround.roundalib.sync.host.HostTypes;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

import java.util.HashMap;

public class SyncedDataStorage extends PersistentState {
  private static final String NBT_DATA = "Data";
  private static final String NBT_BLOCK_ENTITIES = "BlockEntities";
  private static final String NBT_BLOCK_POS = "BlockPos";

  private final HashMap<BlockPos, SyncedDataMap<BlockEntity>> blockEntitySyncedData = new HashMap<>();

  @Override
  public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
    this.writeBlockEntities(nbt, registries);
    return nbt;
  }

  public <T> void setBlockEntitySyncedData(
      BlockPos blockPos,
      SyncedData<T, ? extends BlockEntity> definition,
      T value
  ) {
    this.blockEntitySyncedData.computeIfAbsent(blockPos, (key) -> new SyncedDataMap<>()).put(definition, value);
    this.markDirty();
  }

  public SyncedDataMap<BlockEntity> getBlockEntitySyncedData(BlockPos blockPos) {
    return this.blockEntitySyncedData.get(blockPos);
  }

  @SuppressWarnings("unchecked")
  private void writeBlockEntities(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
    if (this.blockEntitySyncedData.isEmpty()) {
      return;
    }

    NbtCompound blockEntitiesRootNbt = new NbtCompound();

    NbtList dataNbtList = new NbtList();
    for (var blockEntityEntry : this.blockEntitySyncedData.entrySet()) {
      NbtCompound blockEntityNbt = new NbtCompound();
      blockEntityNbt.put(NBT_BLOCK_POS, NbtHelper.fromBlockPos(blockEntityEntry.getKey()));

      SyncedDataMap<BlockEntity> syncedData = blockEntityEntry.getValue();
      for (var dataEntry : syncedData.entrySet()) {
        Codec<Object> codec = (Codec<Object>) dataEntry.getKey().persistenceCodec();
        if (codec == null) {
          // No error - intentionally skipping
          continue;
        }

        codec.encodeStart(registries.getOps(NbtOps.INSTANCE), dataEntry.getValue()).ifSuccess((serialized) -> {
          blockEntityNbt.put(dataEntry.getKey().identifier().toString(), serialized);
        }).ifError((error) -> {
          // TODO: Log error
        });
      }

      dataNbtList.add(blockEntityNbt);
    }
    blockEntitiesRootNbt.put(NBT_DATA, dataNbtList);

    nbt.put(NBT_BLOCK_ENTITIES, blockEntitiesRootNbt);
  }

  private void readBlockEntitySyncedData(NbtCompound rootNbt, RegistryWrapper.WrapperLookup registries) {
    if (!rootNbt.contains(NBT_BLOCK_ENTITIES, NbtElement.COMPOUND_TYPE)) {
      return;
    }

    NbtCompound blockEntitiesRootNbt = rootNbt.getCompound(NBT_BLOCK_ENTITIES);

    NbtList dataNbtList = blockEntitiesRootNbt.getList(NBT_DATA, NbtElement.COMPOUND_TYPE);
    for (NbtElement nbtElement : dataNbtList) {
      NbtCompound blockEntityNbt = (NbtCompound) nbtElement;
      BlockPos blockPos = NbtHelper.toBlockPos(blockEntityNbt, NBT_BLOCK_POS).orElse(null);

      if (blockPos == null) {
        // TODO: Log error
        continue;
      }

      SyncedDataMap<BlockEntity> parsed = new SyncedDataMap<>();

      for (String key : blockEntityNbt.getKeys()) {
        SyncedData<?, ? extends BlockEntity> definition = SyncedDataRegistry.get(
            Identifier.of(key),
            HostTypes.BLOCK_ENTITY
        );
        if (definition == null) {
          // TODO: Log error (unknown data type)
          continue;
        }

        Codec<?> codec = definition.persistenceCodec();
        if (codec == null) {
          // No error - intentionally skipping
          continue;
        }

        codec.parse(registries.getOps(NbtOps.INSTANCE), blockEntityNbt.get(key)).ifSuccess((deserialized) -> {
          parsed.put(definition, deserialized);
        }).ifError((error) -> {
          // TODO: Log error
        });
      }

      if (!parsed.isEmpty()) {
        this.blockEntitySyncedData.put(blockPos, parsed);
      }
    }
  }

  public static SyncedDataStorage getInstance(ServerWorld world) {
    Type<SyncedDataStorage> persistentStateType = new PersistentState.Type<>(
        SyncedDataStorage::new,
        (nbt, registryLookup) -> fromNbt(nbt, world.getRegistryManager()),
        null
    );
    return world.getPersistentStateManager().getOrCreate(persistentStateType, Constants.CONSUMER_MOD_ID + "SyncedData");
  }

  private static SyncedDataStorage fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
    SyncedDataStorage storage = new SyncedDataStorage();

    storage.readBlockEntitySyncedData(nbt, registries);

    return storage;
  }
}
