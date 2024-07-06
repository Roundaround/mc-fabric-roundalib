package me.roundaround.roundalib.config;

import me.roundaround.roundalib.config.manage.ModConfigImpl;
import me.roundaround.roundalib.config.manage.store.WorldScopedFileStore;

public abstract class WorldScopedConfig extends ModConfigImpl implements WorldScopedFileStore {
  protected WorldScopedConfig(String modId) {
    super(modId);
  }

  protected WorldScopedConfig(String modId, String configId) {
    super(modId, configId);
  }

  protected WorldScopedConfig(String modId, int configVersion) {
    super(modId, configVersion);
  }

  protected WorldScopedConfig(String modId, String configId, int configVersion) {
    super(modId, configId, configVersion);
  }
}
