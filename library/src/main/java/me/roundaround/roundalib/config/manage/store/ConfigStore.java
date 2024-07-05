package me.roundaround.roundalib.config.manage.store;

import com.electronwill.nightconfig.core.Config;
import me.roundaround.roundalib.config.option.ConfigOption;

import java.util.Collection;

public interface ConfigStore {
  int getVersion();

  Collection<ConfigOption<?>> getConfigOptionsForStore();

  void setStoreSuppliedVersion(int version);

  int getStoreSuppliedVersion();

  void readFromStore();

  void writeToStore();

  void refresh();

  default boolean isDirty() {
    return this.getVersion() != this.getStoreSuppliedVersion() ||
        this.getConfigOptionsForStore().stream().anyMatch(ConfigOption::isDirty);
  }

  default void syncWithStore() {
    this.setStoreSuppliedVersion(-1);
    this.readFromStore();
    this.writeToStore();
    this.refresh();
  }

  default boolean performConfigUpdate(int versionSnapshot, Config inMemoryConfigSnapshot) {
    return false;
  }
}
