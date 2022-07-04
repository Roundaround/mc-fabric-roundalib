package me.roundaround.roundalib.test.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.roundaround.roundalib.config.gui.screen.ConfigScreen;
import me.roundaround.roundalib.test.RoundaLibTestMod;

public class ModMenuImpl implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return (screen) -> new ConfigScreen(screen, RoundaLibTestMod.CONFIG);
  }
}
