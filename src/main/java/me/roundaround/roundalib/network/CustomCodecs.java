package me.roundaround.roundalib.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.ValueFirstEncoder;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public final class CustomCodecs {
  public static final PacketCodec<ByteBuf, Date> DATE = PacketCodec.tuple(
      PacketCodecs.VAR_LONG,
      Date::getTime,
      Date::new
  );

  public static <T extends CustomPayload> PacketCodec<RegistryByteBuf, T> empty(Supplier<T> supplier) {
    return PacketCodec.of(
        (val, buf) -> {
        }, (buf) -> supplier.get()
    );
  }

  public static <B extends PacketByteBuf, V> PacketCodec<B, List<V>> forList(PacketCodec<? super B, V> entryCodec) {
    return forList((value, buf) -> entryCodec.encode(buf, value), entryCodec);
  }

  public static <B extends PacketByteBuf, V> PacketCodec<B, List<V>> forList(
      ValueFirstEncoder<? super B, V> encoder,
      PacketDecoder<? super B, V> decoder
  ) {
    return new PacketCodec<>() {
      @Override
      public List<V> decode(B buf) {
        int size = buf.readInt();
        List<V> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
          list.add(decoder.decode(buf));
        }
        return list;
      }

      @Override
      public void encode(B buf, List<V> list) {
        buf.writeInt(list.size());
        for (V entry : list) {
          encoder.encode(entry, buf);
        }
      }
    };
  }

  public static <B extends PacketByteBuf, K, V> PacketCodec<B, Map<K, V>> forMap(
      PacketCodec<? super B, K> keyCodec,
      ValueFirstEncoder<? super B, V> valueEncoder,
      PacketDecoder<? super B, V> valueDecoder
  ) {
    return forMap((value, buf) -> keyCodec.encode(buf, value), keyCodec, valueEncoder, valueDecoder);
  }

  public static <B extends PacketByteBuf, K, V> PacketCodec<B, Map<K, V>> forMap(
      ValueFirstEncoder<? super B, K> keyEncoder,
      PacketDecoder<? super B, K> keyDecoder,
      PacketCodec<? super B, V> valueCodec
  ) {
    return forMap(keyEncoder, keyDecoder, (value, buf) -> valueCodec.encode(buf, value), valueCodec);
  }

  public static <B extends PacketByteBuf, K, V> PacketCodec<B, Map<K, V>> forMap(
      PacketCodec<? super B, K> keyCodec,
      PacketCodec<? super B, V> valueCodec
  ) {
    return forMap(
        (value, buf) -> keyCodec.encode(buf, value),
        keyCodec,
        (value, buf) -> valueCodec.encode(buf, value),
        valueCodec
    );
  }

  public static <B extends PacketByteBuf, K, V> PacketCodec<B, Map<K, V>> forMap(
      ValueFirstEncoder<? super B, K> keyEncoder,
      PacketDecoder<? super B, K> keyDecoder,
      ValueFirstEncoder<? super B, V> valueEncoder,
      PacketDecoder<? super B, V> valueDecoder
  ) {
    return new PacketCodec<>() {
      @Override
      public Map<K, V> decode(B buf) {
        int size = buf.readInt();
        Map<K, V> map = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
          map.put(keyDecoder.decode(buf), valueDecoder.decode(buf));
        }
        return map;
      }

      @Override
      public void encode(B buf, Map<K, V> map) {
        buf.writeInt(map.size());
        map.forEach((key, value) -> {
          keyEncoder.encode(key, buf);
          valueEncoder.encode(value, buf);
        });
      }
    };
  }

  public static <B extends ByteBuf, V> PacketCodec<B, V> nullable(PacketCodec<B, V> codec) {
    return new PacketCodec<>() {
      public @Nullable V decode(B byteBuf) {
        return byteBuf.readBoolean() ? codec.decode(byteBuf) : null;
      }

      public void encode(B byteBuf, @Nullable V nullable) {
        if (nullable != null) {
          byteBuf.writeBoolean(true);
          codec.encode(byteBuf, nullable);
        } else {
          byteBuf.writeBoolean(false);
        }
      }
    };
  }

  private CustomCodecs() {
  }
}
