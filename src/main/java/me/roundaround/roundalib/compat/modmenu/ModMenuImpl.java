package me.roundaround.roundalib.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.roundaround.roundalib.RoundaLibMod;
import me.roundaround.roundalib.config.gui.ConfigScreen;

public class ModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
//        return (screen) -> null;
        return (screen) -> new ConfigScreen(screen, RoundaLibMod.CONFIG);
    }
}
