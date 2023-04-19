package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.RoundaLib;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.value.Difficulty;
import me.roundaround.roundalib.config.value.ListOptionValue;

import java.util.HashMap;

public class ControlRegistry {
  private static final HashMap<Class<?>, ControlFactory<?, ?>> byClazz = new HashMap<>();
  private static final HashMap<Class<?>, ControlFactory<?, ?>> byOptionListClazz = new HashMap<>();
  private static final HashMap<String, ControlFactory<?, ?>> byId = new HashMap<>();

  static {
    registerDefaults();
  }

  private ControlRegistry() {
  }

  private static void registerDefaults() {
    try {
      register(BooleanConfigOption.class, ToggleControl::new);
      registerOptionList(Difficulty.class);
    } catch (RegistrationException e) {
      RoundaLib.LOGGER.error("There was an error registering the built-in control factories!", e);
      System.exit(0);
    }
  }

  public static <D, T extends ConfigOption<D, ?>> void register(
      Class<T> clazz, ControlFactory<D, T> factory) throws RegistrationException {
    if (byClazz.containsKey(clazz)) {
      throw new RegistrationException();
    }
    byClazz.put(clazz, factory);
  }

  public static <S extends ListOptionValue<S>> void registerOptionList(Class<S> clazz)
      throws RegistrationException {
    registerOptionList(clazz, OptionListControl::new);
  }

  public static <S extends ListOptionValue<S>, T extends OptionListConfigOption<S>> void registerOptionList(
      Class<S> clazz, ControlFactory<S, T> factory) throws RegistrationException {
    if (byOptionListClazz.containsKey(clazz)) {
      throw new RegistrationException();
    }
    byOptionListClazz.put(clazz, factory);
  }

  public static <D, T extends ConfigOption<D, ?>> void register(
      String id, ControlFactory<D, T> factory) throws RegistrationException {
    if (byId.containsKey(id)) {
      throw new RegistrationException();
    }
    byId.put(id, factory);
  }

  @SuppressWarnings("unchecked")
  public static <D, T extends ConfigOption<D, ?>> ControlFactory<D, T> getControlFactory(T configOption)
      throws NotRegisteredException {
    String id = configOption.getId();
    if (byId.containsKey(id)) {
      return (ControlFactory<D, T>) byId.get(id);
    }

    Class<?> clazz = configOption.getClass();
    if (byClazz.containsKey(clazz)) {
      return (ControlFactory<D, T>) byClazz.get(clazz);
    }

    if (clazz.equals(OptionListConfigOption.class)) {
      Class<?> subClazz = configOption.getValue().getClass();
      if (byOptionListClazz.containsKey(subClazz)) {
        return (ControlFactory<D, T>) byOptionListClazz.get(subClazz);
      }
    }

    throw new NotRegisteredException();
  }

  public static class RegistrationException extends Exception {
  }

  public static class NotRegisteredException extends Exception {
  }

  @FunctionalInterface
  public interface ControlFactory<D, O extends ConfigOption<D, ?>> {
    Control<D, O> create(ConfigListWidget.OptionEntry<D, O> parent);
  }
}
