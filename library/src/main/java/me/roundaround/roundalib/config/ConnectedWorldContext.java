package me.roundaround.roundalib.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

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

  public static ConnectedWorldContext getCurrent() {
    if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
      return NONE;
    }

    MinecraftClient client = MinecraftClient.getInstance();
    ClientWorld world = client.world;
    if (world == null) {
      return ConnectedWorldContext.NONE;
    }

    if (client.isInSingleplayer()) {
      return ConnectedWorldContext.INTEGRATED_SERVER;
    }

    if (client.getCurrentServerEntry() != null) {
      return ConnectedWorldContext.DEDICATED_SERVER;
    }

    return ConnectedWorldContext.NONE;
  }
}
