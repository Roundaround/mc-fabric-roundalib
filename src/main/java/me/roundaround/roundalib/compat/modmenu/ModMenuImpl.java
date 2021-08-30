package me.roundaround.roundalib.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuImpl implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return (screen) -> null;
    //    return (screen) -> new ConfigScreen(screen, RoundaLibMod.CONFIG);
  }
}
