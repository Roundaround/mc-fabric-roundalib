package me.roundaround.roundalib;

import me.roundaround.roundalib.util.ModInfo;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RoundaLibMod implements ModInitializer {
  public static final String MOD_ID = "roundalib";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
  public static final ModInfo MOD_INFO = new ModInfo(MOD_ID, "0.0.3", 1);

  @Override
  public void onInitialize() {
  }
}
