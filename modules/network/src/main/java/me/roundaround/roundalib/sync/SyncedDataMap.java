package me.roundaround.roundalib.sync;

import java.io.Serial;
import java.util.IdentityHashMap;
import java.util.function.BiConsumer;

public final class SyncedDataMap<H> extends IdentityHashMap<SyncedData<?, ? extends H>, Object> {
  @Serial
  private static final long serialVersionUID = 7662201137879034868L;

  @SuppressWarnings("unchecked")
  public <T> T get(SyncedData<T, H> definition) {
    return (T) super.get(definition);
  }

  @SuppressWarnings("unchecked")
  public void forEachUntyped(BiConsumer<SyncedData<Object, H>, Object> consumer) {
    this.forEach((definition, value) -> consumer.accept((SyncedData<Object, H>) definition, value));
  }
}
