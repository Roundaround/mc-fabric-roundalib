package me.roundaround.testmod.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.roundaround.roundalib.client.gui.screen.ConfigScreen;
import me.roundaround.testmod.TestMod;
import me.roundaround.testmod.config.PerWorldTestModConfig;
import me.roundaround.testmod.config.TestModConfig;

public class ModMenuImpl implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return (parent) -> new ConfigScreen(
        parent, TestMod.MOD_ID, TestModConfig.getInstance(), PerWorldTestModConfig.getInstance());
  }
}
