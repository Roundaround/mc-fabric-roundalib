package me.roundaround.roundalib.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.ValueFirstEncoder;
import net.minecraft.network.packet.CustomPayload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class CustomCodecs {
  private CustomCodecs() {
  }

  public static <T extends CustomPayload> PacketCodec<RegistryByteBuf, T> empty(Supplier<T> supplier) {
    return PacketCodec.of((val, buf) -> {
    }, (buf) -> supplier.get());
  }

  public static <B extends PacketByteBuf, V> PacketCodec<B, List<V>> forList(PacketCodec<PacketByteBuf, V> entryCodec) {
    return forList((value, buf) -> entryCodec.encode(buf, value), entryCodec::decode);
  }

  public static <B extends ByteBuf, V> PacketCodec<B, List<V>> forList(
      final ValueFirstEncoder<B, V> encoder, final PacketDecoder<B, V> decoder
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
      PacketCodec<PacketByteBuf, K> keyCodec,
      final ValueFirstEncoder<B, V> valueEncoder,
      final PacketDecoder<B, V> valueDecoder
  ) {
    return forMap((value, buf) -> keyCodec.encode(buf, value), keyCodec::decode, valueEncoder, valueDecoder);
  }

  public static <B extends PacketByteBuf, K, V> PacketCodec<B, Map<K, V>> forMap(
      final ValueFirstEncoder<B, K> keyEncoder,
      final PacketDecoder<B, K> keyDecoder,
      PacketCodec<PacketByteBuf, V> valueCodec
  ) {
    return forMap(keyEncoder, keyDecoder, (value, buf) -> valueCodec.encode(buf, value), valueCodec::decode);
  }

  public static <B extends PacketByteBuf, K, V> PacketCodec<B, Map<K, V>> forMap(
      PacketCodec<PacketByteBuf, K> keyCodec, PacketCodec<PacketByteBuf, V> valueCodec
  ) {
    return forMap((value, buf) -> keyCodec.encode(buf, value), keyCodec::decode,
        (value, buf) -> valueCodec.encode(buf, value), valueCodec::decode
    );
  }

  public static <B extends ByteBuf, K, V> PacketCodec<B, Map<K, V>> forMap(
      final ValueFirstEncoder<B, K> keyEncoder,
      final PacketDecoder<B, K> keyDecoder,
      final ValueFirstEncoder<B, V> valueEncoder,
      final PacketDecoder<B, V> valueDecoder
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
}
