package me.roundaround.roundalib.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.roundaround.roundalib.test.config.RoundaLibTestConfig;
import me.roundaround.roundalib.util.ModInfo;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;

public final class RoundaLibTestMod implements ClientModInitializer, DedicatedServerModInitializer {
  public static final String MOD_ID = "roundalib-testmod";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
  public static final ModInfo MOD_INFO = new ModInfo(MOD_ID, "0.0.7");
  public static final RoundaLibTestConfig CONFIG = new RoundaLibTestConfig();

  @Override
  public void onInitializeClient() {
    CONFIG.clientInit();
  }

  @Override
  public void onInitializeServer() {
    CONFIG.init();
  }
}
