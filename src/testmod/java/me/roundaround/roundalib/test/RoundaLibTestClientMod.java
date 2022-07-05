package me.roundaround.roundalib.test;

import org.lwjgl.glfw.GLFW;

import me.roundaround.roundalib.config.gui.GuiUtil;
import me.roundaround.roundalib.config.gui.control.ControlFactoryRegistry;
import me.roundaround.roundalib.config.gui.control.ControlFactoryRegistry.RegistrationException;
import me.roundaround.roundalib.config.gui.control.SubScreenControl;
import me.roundaround.roundalib.event.HandleInputCallback;
import me.roundaround.roundalib.test.client.screen.BeaconEffectPositionEditScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class RoundaLibTestClientMod implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    initCustomConfigControls();
    initKeyBindings();
  }

  private void initCustomConfigControls() {
    try {
      ControlFactoryRegistry.register(RoundaLibTestMod.CONFIG.BEACON_EFFECT_POSITION.getId(),
          SubScreenControl.getControlFactory(BeaconEffectPositionEditScreen.getSubScreenFactory()));
    } catch (RegistrationException e) {
      RoundaLibTestMod.LOGGER.error(e);
    }
  }

  private void initKeyBindings() {
    KeyBinding openPositionEditScreenKeybinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "roundalib-testmod.keybind.open_position_screen",
        InputUtil.Type.KEYSYM, // TKEYSYM for keyboard, MOUSE for mouse.
        GLFW.GLFW_KEY_K,
        "roundalib-testmod.keybind.category"));

    HandleInputCallback.EVENT.register(() -> {
      while (openPositionEditScreenKeybinding.wasPressed()) {
        GuiUtil.setScreen(
            BeaconEffectPositionEditScreen.getSubScreenFactory().apply(null,
                RoundaLibTestMod.CONFIG.BEACON_EFFECT_POSITION));
      }
    });
  }
}
