package me.roundaround.roundalib.sync;

import com.mojang.serialization.Codec;
import me.roundaround.roundalib.sync.host.HostType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public record SyncedData<T, H>(Identifier identifier,
                               HostType<? super H> hostType,
                               ServerSideFactory<T, H> serverSideFactory,
                               EmptyInitializer<T, H> emptyInitializer,
                               @Nullable Codec<T> persistenceCodec,
                               @Nullable PacketCodec<? super RegistryByteBuf, T> packetCodec) {
  @FunctionalInterface
  public interface ServerSideFactory<T, B> {
    T create(ServerWorld world, B host, NbtCompound nbt, RegistryWrapper.WrapperLookup registries);
  }

  @FunctionalInterface
  public interface EmptyInitializer<T, B> {
    T create(World world, B host);
  }
}
