package me.roundaround.roundalib.util;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashSet;

public class BuiltinResourcePack {
  private static final HashSet<String> FORCED_IDS = new HashSet<>();

  private BuiltinResourcePack() {
  }

  public static boolean register(String modId, String packId, Text packName) {
    return register(modId, packId, packName, true);
  }

  public static boolean register(String modId, String packId, Text packName, boolean forceVersionCompat) {
    Identifier id = Identifier.of(modId, packId);

    boolean success = FabricLoader.getInstance().getModContainer(modId).map((container) -> {
      return ResourceManagerHelper.registerBuiltinResourcePack(id, container, packName,
          ResourcePackActivationType.NORMAL
      );
    }).orElse(false);

    if (forceVersionCompat) {
      FORCED_IDS.add(id.toString());
    }

    return success;
  }

  public static boolean shouldForceVersionCompat(String id) {
    return FORCED_IDS.contains(id);
  }
}
