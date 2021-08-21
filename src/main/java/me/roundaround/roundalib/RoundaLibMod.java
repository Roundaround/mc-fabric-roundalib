package me.roundaround.roundalib;

import me.roundaround.roundalib.config.RoundaLibConfig;
import me.roundaround.roundalib.util.ModInfo;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RoundaLibMod implements ModInitializer {
    public static final String MOD_ID = "me.roundaround.roundalib";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final ModInfo MOD_INFO = new ModInfo(MOD_ID, VERSION, 1, "modname", "config.title");
    public static final RoundaLibConfig CONFIG = new RoundaLibConfig();

    @Override
    public void onInitialize() {
        CONFIG.init();
    }
}
