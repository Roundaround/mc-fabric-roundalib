package me.roundaround.testmod.client;

import me.roundaround.roundalib.client.gui.widget.config.ConfigListWidget;
import me.roundaround.roundalib.client.gui.widget.config.ControlRegistry;
import me.roundaround.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import me.roundaround.testmod.client.screen.ExamplePositionEditScreen;
import net.fabricmc.api.ClientModInitializer;

public class TestModClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    try {
      ControlRegistry.register("testOption8", TestModClient::getSubScreenControl);
      ControlRegistry.register("testOption9", TestModClient::getSubScreenControl);
    } catch (ControlRegistry.RegistrationException e) {
      throw new RuntimeException(e);
    }
  }

  private static SubScreenControl<Position, PositionConfigOption> getSubScreenControl(
      ConfigListWidget.OptionEntry<Position, PositionConfigOption> parent) {
    return new SubScreenControl<>(parent, ExamplePositionEditScreen.getSubScreenFactory());
  }
}
