package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.RoundaLib;
import me.roundaround.roundalib.config.option.*;
import me.roundaround.roundalib.config.value.*;
import net.minecraft.client.MinecraftClient;

import java.util.HashMap;

public class ControlRegistry {
  private static final HashMap<Class<?>, Control.ControlFactory<?, ?>> byClazz = new HashMap<>();
  private static final HashMap<Class<?>, Control.ControlFactory<?, ?>> byOptionListClazz = new HashMap<>();
  private static final HashMap<String, Control.ControlFactory<?, ?>> byId = new HashMap<>();

  static {
    registerDefaults();
  }

  private ControlRegistry() {
  }

  private static void registerDefaults() {
    try {
      register(BooleanConfigOption.class, ToggleControl::new);
      register(StringConfigOption.class, TextControl::new);
      register(IntConfigOption.class, ControlRegistry::intControlFactory);
      register(FloatConfigOption.class, ControlRegistry::floatControlFactory);
      registerOptionList(Difficulty.class);
      registerOptionList(GameMode.class);
      registerOptionList(GuiAlignment.class);
      registerOptionList(GuiAlignmentWithCenter.class);
      registerOptionList(GuiTheme.class);
    } catch (RegistrationException e) {
      RoundaLib.LOGGER.error("There was an error registering the built-in control factories!", e);
      System.exit(0);
    }
  }

  public static <D, T extends ConfigOption<D>> void register(
      Class<T> clazz, Control.ControlFactory<D, T> factory
  ) throws RegistrationException {
    if (byClazz.containsKey(clazz)) {
      throw new RegistrationException();
    }
    byClazz.put(clazz, factory);
  }

  public static <S extends ListOptionValue<S>> void registerOptionList(Class<S> clazz) throws RegistrationException {
    registerOptionList(clazz, OptionListControl::new);
  }

  public static <S extends ListOptionValue<S>, T extends OptionListConfigOption<S>> void registerOptionList(
      Class<S> clazz, Control.ControlFactory<S, T> factory
  ) throws RegistrationException {
    if (byOptionListClazz.containsKey(clazz)) {
      throw new RegistrationException();
    }
    byOptionListClazz.put(clazz, factory);
  }

  public static <D, T extends ConfigOption<D>> void register(
      String id, Control.ControlFactory<D, T> factory
  ) throws RegistrationException {
    if (byId.containsKey(id)) {
      throw new RegistrationException();
    }
    byId.put(id, factory);
  }

  @SuppressWarnings("unchecked")
  public static <D, T extends ConfigOption<D>> Control.ControlFactory<D, T> getControlFactory(T configOption)
      throws NotRegisteredException {
    String id = configOption.getId();
    if (byId.containsKey(id)) {
      return (Control.ControlFactory<D, T>) byId.get(id);
    }

    Class<?> clazz = configOption.getClass();
    if (byClazz.containsKey(clazz)) {
      return (Control.ControlFactory<D, T>) byClazz.get(clazz);
    }

    if (clazz.equals(OptionListConfigOption.class)) {
      Class<?> subClazz = configOption.getValue().getClass();
      if (byOptionListClazz.containsKey(subClazz)) {
        return (Control.ControlFactory<D, T>) byOptionListClazz.get(subClazz);
      }
    }

    throw new NotRegisteredException();
  }

  public static class RegistrationException extends Exception {
  }

  public static class NotRegisteredException extends Exception {
  }

  private static Control<Integer, IntConfigOption> intControlFactory(
      MinecraftClient client, IntConfigOption option, int left, int top, int width, int height
  ) {
    Control.ControlFactory<Integer, IntConfigOption> constructor = option.useSlider() ?
        IntSliderControl::new :
        IntTextControl::new;
    return constructor.create(client, option, left, top, width, height);
  }

  private static Control<Float, FloatConfigOption> floatControlFactory(
      MinecraftClient client, FloatConfigOption option, int left, int top, int width, int height
  ) {
    Control.ControlFactory<Float, FloatConfigOption> constructor = option.useSlider() ?
        FloatSliderControl::new :
        FloatTextControl::new;
    return constructor.create(client, option, left, top, width, height);
  }
}
