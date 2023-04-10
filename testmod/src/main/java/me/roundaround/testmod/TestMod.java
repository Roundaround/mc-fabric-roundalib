package me.roundaround.testmod;

import me.roundaround.testmod.config.TestModConfig;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class TestMod implements ModInitializer {
  public static final String MOD_ID = "testmod";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
  public static final TestModConfig CONFIG = new TestModConfig();

  @Override
  public void onInitialize() {
    CONFIG.init();
  }
}
