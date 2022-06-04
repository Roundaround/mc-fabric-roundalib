package me.roundaround.roundalib.config.gui.control;

import java.util.HashMap;
import java.util.Map;

import me.roundaround.roundalib.RoundaLibMod;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.option.IntConfigOption;
import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.option.StringConfigOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ControlFactoryRegistry {
  // TODO: Improve this. Had to remove the generic type from ListOptionValue and
  // related classes in order to allow registering here by generic. Perhaps the
  // control itself should not care about the config option's type, and should
  // use an interface somehow?

  private static final Map<Class<?>, ControlFactory<?>> byClazz = new HashMap<>();
  private static final Map<String, ControlFactory<?>> byId = new HashMap<>();

  static {
    registerDefaults();
  }

  private ControlFactoryRegistry() {
  }

  private static void registerDefaults() {
    try {
      register(BooleanConfigOption.class, ToggleControl::new);
      register(IntConfigOption.class, IntInputControl::new);
      register(StringConfigOption.class, TextInputControl::new);
      register(OptionListConfigOption.class, OptionListControl::new);
    } catch (RegistrationException e) {
      RoundaLibMod.LOGGER.error("There was an error registering the built-in control factories!", e);
      System.exit(0);
    }

  }

  public static <T extends ConfigOption<?>> void register(Class<T> clazz, ControlFactory<T> factory)
      throws RegistrationException {
    if (byClazz.containsKey(clazz)) {
      throw new RegistrationException();
    }
    byClazz.put(clazz, factory);
  }

  public static <T extends ConfigOption<?>> void register(String id, ControlFactory<T> factory)
      throws RegistrationException {
    if (byId.containsKey(id)) {
      throw new RegistrationException();
    }
    byId.put(id, factory);
  }

  @SuppressWarnings("unchecked")
  public static <T extends ConfigOption<?>> ControlFactory<T> getControlFactory(T configOption)
      throws RegistrationException {
    String id = configOption.getId();
    if (byId.containsKey(id)) {
      return (ControlFactory<T>) byId.get(id);
    }

    Class<?> clazz = configOption.getClass();
    if (byClazz.containsKey(clazz)) {
      return (ControlFactory<T>) byClazz.get(clazz);
    }

    throw new RegistrationException();
  }

  public static class RegistrationException extends Exception {
  }
}
