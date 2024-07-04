package me.roundaround.roundalib;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RoundaLib implements PreLaunchEntrypoint {
  public static final Logger LOGGER = LogManager.getLogger("roundalib");

  @Override
  public void onPreLaunch() {
    PathAccessor.init();
  }
}
