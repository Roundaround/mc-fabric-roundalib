package me.roundaround.roundalib.network;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class RoundaLibPacketCodecs {
  public static final StreamCodec<ByteBuf, Date> DATE = StreamCodec.composite(
      ByteBufCodecs.VAR_LONG,
      Date::getTime,
      Date::new
  );

  public static <T extends CustomPacketPayload> StreamCodec<RegistryFriendlyByteBuf, T> empty(Supplier<T> supplier) {
    return StreamCodec.ofMember(
        (val, buf) -> {
        }, (buf) -> supplier.get()
    );
  }

  public static <B extends FriendlyByteBuf, V> StreamCodec<B, List<V>> forList(StreamCodec<? super B, V> entryCodec) {
    return forList((value, buf) -> entryCodec.encode(buf, value), entryCodec);
  }

  public static <B extends FriendlyByteBuf, V> StreamCodec<B, List<V>> forList(
      StreamMemberEncoder<? super B, V> encoder,
      StreamDecoder<? super B, V> decoder
  ) {
    return new StreamCodec<>() {
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

  public static <B extends FriendlyByteBuf, K, V> StreamCodec<B, Map<K, V>> forMap(
      StreamCodec<? super B, K> keyCodec,
      StreamMemberEncoder<? super B, V> valueEncoder,
      StreamDecoder<? super B, V> valueDecoder
  ) {
    return forMap((value, buf) -> keyCodec.encode(buf, value), keyCodec, valueEncoder, valueDecoder);
  }

  public static <B extends FriendlyByteBuf, K, V> StreamCodec<B, Map<K, V>> forMap(
      StreamMemberEncoder<? super B, K> keyEncoder,
      StreamDecoder<? super B, K> keyDecoder,
      StreamCodec<? super B, V> valueCodec
  ) {
    return forMap(keyEncoder, keyDecoder, (value, buf) -> valueCodec.encode(buf, value), valueCodec);
  }

  public static <B extends FriendlyByteBuf, K, V> StreamCodec<B, Map<K, V>> forMap(
      StreamCodec<? super B, K> keyCodec,
      StreamCodec<? super B, V> valueCodec
  ) {
    return forMap(
        (value, buf) -> keyCodec.encode(buf, value),
        keyCodec,
        (value, buf) -> valueCodec.encode(buf, value),
        valueCodec
    );
  }

  public static <B extends FriendlyByteBuf, K, V> StreamCodec<B, Map<K, V>> forMap(
      StreamMemberEncoder<? super B, K> keyEncoder,
      StreamDecoder<? super B, K> keyDecoder,
      StreamMemberEncoder<? super B, V> valueEncoder,
      StreamDecoder<? super B, V> valueDecoder
  ) {
    return new StreamCodec<>() {
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

  public static <B extends ByteBuf, V> StreamCodec<B, V> nullable(StreamCodec<B, V> codec) {
    return new StreamCodec<>() {
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

  private RoundaLibPacketCodecs() {
  }
}
