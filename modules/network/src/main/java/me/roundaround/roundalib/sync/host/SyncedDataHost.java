package me.roundaround.roundalib.sync.host;

import me.roundaround.roundalib.sync.SyncedData;
import me.roundaround.roundalib.sync.SyncedDataMap;
import net.minecraft.util.Clearable;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public interface SyncedDataHost<H> extends Clearable {
  default SyncedDataMap<H> roundalib$getSyncedMap() {
    throw new IllegalStateException("Placeholder implementation; override via mixin");
  }

  default boolean roundalib$shouldSendUpdates() {
    throw new IllegalStateException("Placeholder implementation; override via mixin");
  }

  default <T> void roundalib$sendUpdate(SyncedData<T, H> definition, T value) {
    throw new IllegalStateException("Placeholder implementation; override via mixin");
  }

  default void roundalib$initSynced(@Nullable SyncedDataMap<H> storedData) {
    this.roundalib$clearSynced();
    this.roundalib$getSyncedMap().putAll(storedData);
    // TODO: Send updates
  }

  default <T> T roundalib$getSynced(SyncedData<T, H> definition) {
    return this.roundalib$getSyncedMap().get(definition);
  }

  default <T> T roundalib$setSynced(SyncedData<T, H> definition, T value) {
    this.roundalib$getSyncedMap().put(definition, value);
    if (this.roundalib$shouldSendUpdates() && definition.packetCodec() != null) {
      this.roundalib$sendUpdate(definition, value);
    }
    return value;
  }

  default <T> T roundalib$modifySynced(SyncedData<T, H> definition, UnaryOperator<T> modifier) {
    return this.roundalib$setSynced(definition, modifier.apply(this.roundalib$getSynced(definition)));
  }

  default void roundalib$clearSynced() {
    this.roundalib$getSyncedMap().clear();
  }
}
