package me.roundaround.testmod.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.roundaround.roundalib.client.gui.screen.ConfigScreen;
import me.roundaround.testmod.TestMod;

public class ModMenuImpl implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return (parent) -> new ConfigScreen(parent, TestMod.CONFIG);
  }
}
