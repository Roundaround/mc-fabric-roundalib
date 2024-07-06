package me.roundaround.roundalib.config;

import me.roundaround.roundalib.config.manage.ModConfigImpl;
import me.roundaround.roundalib.config.manage.store.WorldScopedFileStore;
import me.roundaround.roundalib.config.option.ConfigOption;

import java.util.function.Function;

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

  public static class RegistrationBuilder<T extends ConfigOption<?>> extends ModConfigImpl.RegistrationBuilderImpl<T,
      RegistrationBuilder<T>> {
    protected ConnectedWorldContext applicableContext = ConnectedWorldContext.ANY;

    public RegistrationBuilder(T option, Function<RegistrationBuilder<T>, T> onCommit) {
      super(option, onCommit);
    }

    public RegistrationBuilder<T> singlePlayerOnly() {
      return this.integratedServerOnly();
    }

    public RegistrationBuilder<T> integratedServerOnly() {
      this.applicableContext = ConnectedWorldContext.INTEGRATED_SERVER;
      return this;
    }

    public RegistrationBuilder<T> multiplayerOnly() {
      return this.dedicatedServerOnly();
    }

    public RegistrationBuilder<T> dedicatedServerOnly() {
      this.applicableContext = ConnectedWorldContext.DEDICATED_SERVER;
      return this;
    }
  }
}
