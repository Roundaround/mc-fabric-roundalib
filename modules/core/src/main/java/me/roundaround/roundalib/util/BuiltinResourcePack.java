package me.roundaround.roundalib.util;

import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.HashSet;

public class BuiltinResourcePack {
  private static final HashSet<String> FORCED_IDS = new HashSet<>();

  private BuiltinResourcePack() {
  }

  public static boolean register(String modId, String packId, Component packName) {
    return register(modId, packId, packName, true);
  }

  public static boolean register(String modId, String packId, Component packName, boolean forceVersionCompat) {
    Identifier id = Identifier.fromNamespaceAndPath(modId, packId);

    boolean success = FabricLoader.getInstance()
        .getModContainer(modId)
        .map((container) -> ResourceLoader.registerBuiltinPack(id, container, packName, PackActivationType.NORMAL))
        .orElse(false);

    if (forceVersionCompat) {
      FORCED_IDS.add(id.toString());
    }

    return success;
  }

  public static boolean shouldForceVersionCompat(String id) {
    return FORCED_IDS.contains(id);
  }
}
