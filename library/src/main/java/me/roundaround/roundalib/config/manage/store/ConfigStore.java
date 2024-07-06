package me.roundaround.roundalib.config.manage.store;

import com.electronwill.nightconfig.core.Config;
import me.roundaround.roundalib.config.option.ConfigOption;

import java.util.Collection;
import java.util.function.Consumer;

public interface ConfigStore {
  String getModId();

  int getVersion();

  Collection<ConfigOption<?>> getAll();

  void setStoreSuppliedVersion(int version);

  int getStoreSuppliedVersion();

  void readFromStore();

  void writeToStore();

  void clear();

  boolean isInitialized();

  default boolean isReady() {
    return this.isInitialized();
  }

  default boolean isDirty() {
    return this.getVersion() != this.getStoreSuppliedVersion() ||
        this.getAll().stream().anyMatch(ConfigOption::isDirty);
  }

  default void initializeStore() {
    this.syncWithStore();
  }

  default void syncWithStore() {
    this.setStoreSuppliedVersion(-1);
    this.readFromStore();
    this.writeToStore();
    this.refresh();
  }

  default void forEach(Consumer<ConfigOption<?>> consumer) {
    this.getAll().forEach(consumer);
  }

  default void refresh() {
    this.getAll().forEach(ConfigOption::update);
  }

  default boolean performConfigUpdate(int versionSnapshot, Config inMemoryConfigSnapshot) {
    return false;
  }
}
