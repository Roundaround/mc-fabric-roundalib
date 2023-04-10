package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.value.ListOptionValue;

import java.util.HashMap;

public class ControlRegistry {
  private static final HashMap<Class<?>, ControlFactory<?>> byClazz = new HashMap<>();
  private static final HashMap<Class<?>, ControlFactory<?>> byOptionListClazz = new HashMap<>();
  private static final HashMap<String, ControlFactory<?>> byId = new HashMap<>();

  private ControlRegistry() {
  }

  public static <T extends ConfigOption<?, ?>> void register(
      Class<T> clazz,
      ControlFactory<T> factory)
      throws RegistrationException {
    if (byClazz.containsKey(clazz)) {
      throw new RegistrationException();
    }
    byClazz.put(clazz, factory);
  }

  public static <S extends ListOptionValue<S>, T extends OptionListConfigOption<S>> void registerOptionList(
      Class<S> clazz,
      ControlFactory<T> factory)
      throws RegistrationException {
    if (byOptionListClazz.containsKey(clazz)) {
      throw new RegistrationException();
    }
    byOptionListClazz.put(clazz, factory);
  }

  public static <T extends ConfigOption<?, ?>> void register(
      String id,
      ControlFactory<T> factory)
      throws RegistrationException {
    if (byId.containsKey(id)) {
      throw new RegistrationException();
    }
    byId.put(id, factory);
  }

  public static <O extends ConfigOption<?, ?>> Control<O> create(ConfigListWidget parent, O option) {
    return null;
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
