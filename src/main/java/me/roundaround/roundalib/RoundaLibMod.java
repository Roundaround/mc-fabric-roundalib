package me.roundaround.roundalib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.roundaround.roundalib.config.RoundaLibConfig;
import me.roundaround.roundalib.util.ModInfo;
import net.fabricmc.api.ModInitializer;

public final class RoundaLibMod implements ModInitializer {
  public static final String MOD_ID = "roundalib";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
  public static final ModInfo MOD_INFO = new ModInfo(MOD_ID, "0.0.5", 1);
  public static final RoundaLibConfig CONFIG = new RoundaLibConfig();

  @Override
  public void onInitialize() {
    System.out.println("INITIALIZING ROUNDALIB");
    System.out.println("INITIALIZING ROUNDALIB");
    System.out.println("INITIALIZING ROUNDALIB");
    System.out.println("INITIALIZING ROUNDALIB");
    System.out.println("INITIALIZING ROUNDALIB");
    System.out.println("INITIALIZING ROUNDALIB");
    System.out.println("INITIALIZING ROUNDALIB");
    System.out.println("INITIALIZING ROUNDALIB");
    System.out.println("INITIALIZING ROUNDALIB");
    System.out.println("INITIALIZING ROUNDALIB");
    CONFIG.init();
  }
}
