package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.RoundaLib;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.value.ListOptionValue;

import java.util.HashMap;

public class ControlRegistry {
  private static final HashMap<Class<?>, ControlFactory<?>> byClazz = new HashMap<>();
  private static final HashMap<Class<?>, ControlFactory<?>> byOptionListClazz = new HashMap<>();
  private static final HashMap<String, ControlFactory<?>> byId = new HashMap<>();

  static {
    registerDefaults();
  }

  private ControlRegistry() {
  }

  private static void registerDefaults() {
    try {
      register(BooleanConfigOption.class, ToggleControl::new);
    } catch (RegistrationException e) {
      RoundaLib.LOGGER.error("There was an error registering the built-in control factories!", e);
      System.exit(0);
    }
  }

  public static <T extends ConfigOption<?, ?>> void register(
      Class<T> clazz, ControlFactory<T> factory) throws RegistrationException {
    if (byClazz.containsKey(clazz)) {
      throw new RegistrationException();
    }
    byClazz.put(clazz, factory);
  }

  public static <S extends ListOptionValue<S>, T extends OptionListConfigOption<S>> void registerOptionList(
      Class<S> clazz, ControlFactory<T> factory) throws RegistrationException {
    if (byOptionListClazz.containsKey(clazz)) {
      throw new RegistrationException();
    }
    byOptionListClazz.put(clazz, factory);
  }

  public static <T extends ConfigOption<?, ?>> void register(
      String id, ControlFactory<T> factory) throws RegistrationException {
    if (byId.containsKey(id)) {
      throw new RegistrationException();
    }
    byId.put(id, factory);
  }

  @SuppressWarnings("unchecked")
  public static <T extends ConfigOption<?, ?>> ControlFactory<T> getControlFactory(T configOption)
      throws NotRegisteredException {
    String id = configOption.getId();
    if (byId.containsKey(id)) {
      return (ControlFactory<T>) byId.get(id);
    }

    Class<?> clazz = configOption.getClass();
    if (byClazz.containsKey(clazz)) {
      return (ControlFactory<T>) byClazz.get(clazz);
    }

    if (clazz.equals(OptionListConfigOption.class)) {
      Class<?> subClazz = configOption.getValue().getClass();
      if (byOptionListClazz.containsKey(subClazz)) {
        return (ControlFactory<T>) byOptionListClazz.get(subClazz);
      }
    }

    throw new NotRegisteredException();
  }

  public static class RegistrationException extends Exception {
  }

  public static class NotRegisteredException extends Exception {
  }

  @FunctionalInterface
  public interface ControlFactory<O extends ConfigOption<?, ?>> {
    Control<O> create(ConfigListWidget.OptionEntry<O> parent);
  }
}
