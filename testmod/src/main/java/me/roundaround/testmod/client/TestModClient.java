package me.roundaround.testmod.client;

import me.roundaround.roundalib.client.gui.widget.config.ControlRegistry;
import me.roundaround.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import me.roundaround.testmod.client.screen.ExamplePositionEditScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

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
      MinecraftClient client, PositionConfigOption option
  ) {
    return new SubScreenControl<>(client, option, ExamplePositionEditScreen.getSubScreenFactory());
  }
}
