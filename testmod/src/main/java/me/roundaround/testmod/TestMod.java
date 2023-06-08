package me.roundaround.testmod;

import me.roundaround.testmod.config.TestModConfig;
import net.fabricmc.api.ModInitializer;

public final class TestMod implements ModInitializer {
  public static final String MOD_ID = "testmod";
  public static final TestModConfig CONFIG = new TestModConfig();

  @Override
  public void onInitialize() {
    CONFIG.init();
  }
}
