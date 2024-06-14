package me.roundaround.testmod.client;

import me.roundaround.roundalib.client.event.ScreenInputEvent;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.config.ControlRegistry;
import me.roundaround.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import me.roundaround.testmod.client.screen.DemoScreen;
import me.roundaround.testmod.client.screen.ExamplePositionEditScreen;
import me.roundaround.testmod.client.screen.IconButtonDemoScreen;
import me.roundaround.testmod.client.screen.LabelDemoScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;

public class TestModClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    try {
      ControlRegistry.register("testOption9", TestModClient::getSubScreenControl);
      ControlRegistry.register("testOption10", TestModClient::getSubScreenControl);
    } catch (ControlRegistry.RegistrationException e) {
      throw new RuntimeException(e);
    }

    ScreenInputEvent.EVENT_BUS.register((screen, keyCode, scanCode, modifiers) -> {
      if (screen instanceof DemoScreen) {
        return false;
      }
      if (Screen.hasControlDown()) {
        switch (keyCode) {
          case GLFW.GLFW_KEY_L -> {
            GuiUtil.setScreen(new LabelDemoScreen(screen));
            return true;
          }
          case GLFW.GLFW_KEY_I -> {
            GuiUtil.setScreen(new IconButtonDemoScreen(screen));
            return true;
          }
        }
      }
      return false;
    });
  }

  private static SubScreenControl<Position, PositionConfigOption> getSubScreenControl(
      MinecraftClient client, PositionConfigOption option, int left, int top, int width, int height
  ) {
    return new SubScreenControl<>(client, option, left, top, width, height,
        SubScreenControl.getValueDisplayMessageFactory(), ExamplePositionEditScreen.getSubScreenFactory()
    );
  }
}
