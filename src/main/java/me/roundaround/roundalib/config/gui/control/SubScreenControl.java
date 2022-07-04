package me.roundaround.roundalib.config.gui.control;

import me.roundaround.roundalib.config.gui.widget.OptionRowWidget;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;

public class SubScreenControl<D, C extends ConfigOption<D, ?>> extends ButtonControl<C> {
  private static MinecraftClient MINECRAFT = MinecraftClient.getInstance();

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
    Screen subScreen = subScreenFactory.apply(parent.getConfigScreen(), configOption);
    MINECRAFT.setScreen(subScreen);
  }

  @Override
  protected Text getCurrentText() {
    return Text.literal("Example");
  }

  @Override
  public void appendNarrations(NarrationMessageBuilder builder) {
  }

  @FunctionalInterface
  public interface SubScreenFactory<D, C extends ConfigOption<D, ?>> {
    public Screen apply(Screen parent, C configOption);
  }
}
