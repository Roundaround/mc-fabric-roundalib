package me.roundaround.roundalib;

import me.roundaround.roundalib.generated.Constants;
import me.roundaround.roundalib.util.PathAccessor;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RoundaLib implements ModInitializer {
  public static final Logger LOGGER = LogManager.getLogger(Constants.MOD_ID);

  @Override
  public void onInitialize() {
    PathAccessor.init();
  }
}
