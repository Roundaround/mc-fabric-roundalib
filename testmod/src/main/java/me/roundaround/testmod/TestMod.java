package me.roundaround.testmod;

import me.roundaround.roundalib.client.gui.widget.config.ConfigListWidget;
import me.roundaround.roundalib.client.gui.widget.config.ControlRegistry;
import me.roundaround.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import me.roundaround.testmod.client.screen.ExamplePositionEditScreen;
import me.roundaround.testmod.config.TestModConfig;
import net.fabricmc.api.ModInitializer;

public final class TestMod implements ModInitializer {
  public static final String MOD_ID = "testmod";
  public static final TestModConfig CONFIG = new TestModConfig();

  @Override
  public void onInitialize() {
    CONFIG.init();

    try {
      ControlRegistry.register("testOption27", TestMod::getSubScreenControl);
    } catch (ControlRegistry.RegistrationException e) {
      throw new RuntimeException(e);
    }
  }

  private static SubScreenControl<Position, PositionConfigOption> getSubScreenControl(
      ConfigListWidget.OptionEntry<Position, PositionConfigOption> parent) {
    return new SubScreenControl<>(parent, ExamplePositionEditScreen.getSubScreenFactory());
  }
}
