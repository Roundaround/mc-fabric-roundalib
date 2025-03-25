package me.roundaround.roundalib.sync;

import com.mojang.serialization.Codec;
import me.roundaround.roundalib.RoundaLib;
import me.roundaround.roundalib.sync.host.HostType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public final class SyncedDataRegistry {
  private static final HashMap<HostType<?>, HashMap<Identifier, SyncedData<?, ?>>> store = new HashMap<>();

  private static <T, H> SyncedData<T, H> register(Identifier id, SyncedData<T, H> definition) {
    HashMap<Identifier, SyncedData<?, ? extends H>> storeForHost = getStoreForHost(definition.hostType());
    SyncedData<?, ? extends H> existing = storeForHost.put(id, definition);
    if (existing != null) {
      RoundaLib.LOGGER.warn("Duplicate synced data entry registered. Previous entry has been discarded: {}", id);
    }
    return definition;
  }

  public static <T, H> SyncedData<T, H> register(
      Identifier id,
      HostType<? super H> hostType,
      SyncedData.ServerSideFactory<T, H> serverSideFactory,
      SyncedData.EmptyInitializer<T, H> emptyInitializer,
      @Nullable Codec<T> persistenceCodec,
      @Nullable PacketCodec<? super RegistryByteBuf, T> packetCodec
  ) {
    return register(
        id,
        new SyncedData<>(id, hostType, serverSideFactory, emptyInitializer, persistenceCodec, packetCodec)
    );
  }

  public static <T, H> SyncedData<T, H> register(
      Identifier id,
      HostType<? super H> hostType,
      SyncedData.ServerSideFactory<T, H> serverSideFactory,
      SyncedData.EmptyInitializer<T, H> emptyInitializer,
      Codec<T> persistenceCodec
  ) {
    return register(id, hostType, serverSideFactory, emptyInitializer, persistenceCodec, null);
  }

  public static <T, H> SyncedData<T, H> register(
      Identifier id,
      HostType<? super H> hostType,
      SyncedData.ServerSideFactory<T, H> serverSideFactory,
      SyncedData.EmptyInitializer<T, H> emptyInitializer,
      PacketCodec<? super RegistryByteBuf, T> packetCodec
  ) {
    return register(id, hostType, serverSideFactory, emptyInitializer, null, packetCodec);
  }

  @Nullable
  public static <H> SyncedData<?, ? extends H> get(Identifier id, HostType<? super H> hostType) {
    HashMap<Identifier, SyncedData<?, ? extends H>> storeForHost = getStoreForHost(hostType);
    if (storeForHost == null) {
      return null;
    }
    return storeForHost.get(id);
  }

  @SuppressWarnings("unchecked")
  private static <H, T extends HostType<? super H>> HashMap<Identifier, SyncedData<?, ? extends H>> getStoreForHost(T hostType) {
    return (HashMap<Identifier, SyncedData<?, ? extends H>>) (Object) store.computeIfAbsent(
        hostType,
        (key) -> new HashMap<>()
    );
  }

  private SyncedDataRegistry() {
  }
}
