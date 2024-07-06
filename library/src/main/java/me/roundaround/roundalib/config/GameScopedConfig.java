package me.roundaround.roundalib.config;

import me.roundaround.roundalib.config.manage.ModConfigImpl;
import me.roundaround.roundalib.config.manage.store.GameScopedFileStore;

public abstract class GameScopedConfig extends ModConfigImpl implements GameScopedFileStore {
  protected GameScopedConfig(String modId) {
    super(modId);
  }

  protected GameScopedConfig(String modId, String configId) {
    super(modId, configId);
  }

  protected GameScopedConfig(String modId, int configVersion) {
    super(modId, configVersion);
  }

  protected GameScopedConfig(String modId, String configId, int configVersion) {
    super(modId, configId, configVersion);
  }
}
