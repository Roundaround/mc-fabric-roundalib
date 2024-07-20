package me.roundaround.roundalib.client.gui.layout.linear;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.Widget;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public interface AutoLinearLayoutCellConfigurator<T extends Widget> {
  void onAddToLinearLayout(LinearLayoutCellConfigurator<T> configurator);

  @SuppressWarnings("unchecked")
  static <T extends Widget> Optional<AutoLinearLayoutCellConfigurator<T>> of(T widget) {
    if (!(widget instanceof AutoLinearLayoutCellConfigurator<?>)) {
      return Optional.empty();
    }

    Class<?> widgetClass = widget.getClass();
    Type[] interfaces = widgetClass.getGenericInterfaces();

    for (Type type : interfaces) {
      if (!(type instanceof ParameterizedType parameterizedType)) {
        continue;
      }
      if (parameterizedType.getRawType() != AutoLinearLayoutCellConfigurator.class) {
        continue;
      }
      Type actualType = parameterizedType.getActualTypeArguments()[0];
      if (actualType instanceof Class<?> && ((Class<?>) actualType).isAssignableFrom(widgetClass)) {
        return Optional.of((AutoLinearLayoutCellConfigurator<T>) widget);
      }
    }

    return Optional.empty();
  }
}
