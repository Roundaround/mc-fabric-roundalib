package me.roundaround.roundalib.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public enum ConnectedWorldContext {
  ANY, INTEGRATED_SERVER, DEDICATED_SERVER, NONE;

  public boolean satisfies(ConnectedWorldContext context) {
    return switch (context) {
      case ANY -> this != NONE;
      case INTEGRATED_SERVER -> this == ANY || this == INTEGRATED_SERVER;
      case DEDICATED_SERVER -> this == ANY || this == DEDICATED_SERVER;
      case NONE -> false;
    };
  }

  public boolean satisfies() {
    return this.satisfies(getCurrent());
  }

  public static boolean isSinglePlayer() {
    if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
      return false;
    }

    ConnectedWorldContext context = getCurrent();
    return context == NONE || context == INTEGRATED_SERVER;
  }

  public static ConnectedWorldContext getCurrent() {
    if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
      return NONE;
    }

    Minecraft client = Minecraft.getInstance();
    ClientLevel world = client.level;
    if (world == null) {
      return ConnectedWorldContext.NONE;
    }

    if (client.isLocalServer()) {
      return ConnectedWorldContext.INTEGRATED_SERVER;
    }

    if (client.getCurrentServer() != null) {
      return ConnectedWorldContext.DEDICATED_SERVER;
    }

    return ConnectedWorldContext.NONE;
  }
}
