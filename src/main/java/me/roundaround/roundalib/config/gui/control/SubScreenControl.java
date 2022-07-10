package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.GuiUtil;
import me.roundaround.roundalib.config.gui.widget.OptionRowWidget;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;

public class SubScreenControl<D, C extends ConfigOption<D, ?>> extends ButtonControl<C> {
  protected final SubScreenFactory<D, C> subScreenFactory;

  public SubScreenControl(
      SubScreenFactory<D, C> subScreenFactory,
      C configOption,
      OptionRowWidget parent,
      int top,
      int left,
      int height,
      int width) {
    super(configOption, parent, top, left, height, width);
    this.subScreenFactory = subScreenFactory;
  }

  public static <D, C extends ConfigOption<D, ?>> ControlFactory<C> getControlFactory(
      SubScreenFactory<D, C> subScreenFactory) {
    return (
        C configOption,
        OptionRowWidget parent,
        int top,
        int left,
        int height,
        int width) -> new SubScreenControl<>(
            subScreenFactory,
            configOption,
            parent,
            top,
            left,
            height,
            width);
  }

  @Override
  protected void onPress(int button) {
    super.onPress(button);
    GuiUtil.setScreen(subScreenFactory.apply(parent.getConfigScreen(), configOption));
  }

  @Override
  protected Text getCurrentText() {
    return Text.translatable("roundalib.subscreen.label");
  }

  @Override
  public void appendNarrations(NarrationMessageBuilder builder) {
  }

  @FunctionalInterface
  public interface SubScreenFactory<D, C extends ConfigOption<D, ?>> {
    public Screen apply(Screen parent, C configOption);
  }
}
