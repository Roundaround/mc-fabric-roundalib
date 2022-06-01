package me.roundaround.roundalib.config.gui.control;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import me.roundaround.roundalib.config.option.BooleanConfigOption;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.option.IntConfigOption;
import me.roundaround.roundalib.config.option.OptionListConfigOption;
import me.roundaround.roundalib.config.option.StringConfigOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DefaultControlFactoryMap {
  public static final DefaultControlFactoryMap INSTANCE = new DefaultControlFactoryMap();

  private final Map<Class<?>, ControlFactory<?>> internal = new HashMap<>();

  @SuppressWarnings("unchecked")
  private DefaultControlFactoryMap() {
    register(BooleanConfigOption.class, ToggleControl::new);
    register(IntConfigOption.class, IntInputControl::new);
    register(StringConfigOption.class, TextInputControl::new);
    register(OptionListConfigOption.class, OptionListControl::new);
  }

  private <T extends ConfigOption<?>> void register(Class<T> clazz, ControlFactory<T> factory) {
    internal.put(clazz, factory);
  }

  @SuppressWarnings("unchecked")
  private <T extends ConfigOption<?>> Optional<ControlFactory<T>> internalGetControlFactory(T configOption) {
    Class<?> clazz = configOption.getClass();
    if (!internal.containsKey(clazz)) {
      return Optional.empty();
    }
    return Optional.of((ControlFactory<T>) internal.get(clazz));
  }

  public static <T extends ConfigOption<?>> Optional<ControlFactory<T>> getControlFactory(T configOption) {
    return INSTANCE.internalGetControlFactory(configOption);
  }
}
