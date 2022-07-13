package me.roundaround.roundalib.config.gui.control;

import java.util.HashMap;
import java.util.Map;

import me.roundaround.roundalib.RoundaLibMod;
import me.roundaround.roundalib.config.gui.widget.OptionRowWidget;
import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.option.FloatConfigOption;
import me.roundaround.roundalib.config.option.IntConfigOption;
import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.option.StringConfigOption;
import me.roundaround.roundalib.config.value.Difficulty;
import me.roundaround.roundalib.config.value.GameMode;
import me.roundaround.roundalib.config.value.GuiAlignment;
import me.roundaround.roundalib.config.value.GuiTheme;
import me.roundaround.roundalib.config.value.ListOptionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ControlFactoryRegistry {
  private static final Map<Class<?>, ControlFactory<?>> byClazz = new HashMap<>();
  private static final Map<Class<?>, ControlFactory<?>> byOptionListClazz = new HashMap<>();
  private static final Map<String, ControlFactory<?>> byId = new HashMap<>();

  static {
    registerDefaults();
  }

  private ControlFactoryRegistry() {
  }

  private static void registerDefaults() {
    try {
      register(BooleanConfigOption.class, ToggleControl::new);
      register(IntConfigOption.class, new IntControlFactory());
      register(FloatConfigOption.class, new FloatControlFactory());
      register(StringConfigOption.class, TextInputControl::new);
      registerOptionList(GuiAlignment.class, OptionListControl::new);
      registerOptionList(GuiTheme.class, OptionListControl::new);
      registerOptionList(Difficulty.class, OptionListControl::new);
      registerOptionList(GameMode.class, OptionListControl::new);
    } catch (RegistrationException e) {
      RoundaLibMod.LOGGER.error("There was an error registering the built-in control factories!", e);
      System.exit(0);
    }

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

  private static class IntControlFactory implements ControlFactory<IntConfigOption> {
    @Override
    public ControlWidget<IntConfigOption> apply(
        IntConfigOption configOption,
        OptionRowWidget optionRow,
        int top,
        int left,
        int height,
        int width) {
      ControlFactory<IntConfigOption> constructor = configOption.useSlider()
          ? IntSliderControl::new
          : IntInputControl::new;
      return constructor.apply(configOption, optionRow, top, left, height, width);
    }
  }

  private static class FloatControlFactory implements ControlFactory<FloatConfigOption> {
    @Override
    public ControlWidget<FloatConfigOption> apply(
        FloatConfigOption configOption,
        OptionRowWidget optionRow,
        int top,
        int left,
        int height,
        int width) {
      ControlFactory<FloatConfigOption> constructor = configOption.useSlider()
          ? FloatSliderControl::new
          : FloatInputControl::new;
      return constructor.apply(configOption, optionRow, top, left, height, width);
    }
  }
}
