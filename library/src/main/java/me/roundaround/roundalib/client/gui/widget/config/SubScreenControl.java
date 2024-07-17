package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.Function;

public class SubScreenControl<D, O extends ConfigOption<D>> extends Control<D, O> {
  private final Function<O, Text> messageFactory;
  private final SubScreenFactory<D, O> subScreenFactory;
  private final ButtonWidget button;

  public SubScreenControl(
      MinecraftClient client, O option, int width, int height, SubScreenFactory<D, O> subScreenFactory
  ) {
    this(client, option, width, height, (value) -> Text.translatable(option.getModId() + ".roundalib.subscreen.label"),
        subScreenFactory
    );
  }

  public SubScreenControl(
      MinecraftClient client,
      O option,
      int width,
      int height,
      Function<O, Text> messageFactory,
      SubScreenFactory<D, O> subScreenFactory
  ) {
    super(client, option, width, height);
    this.messageFactory = messageFactory;
    this.subScreenFactory = subScreenFactory;

    this.button = this.add(ButtonWidget.builder(this.messageFactory.apply(this.getOption()),
        (button) -> GuiUtil.setScreen(this.subScreenFactory.create(this.client.currentScreen, this.getOption()))
    ).build(), (parent, self) -> {
      self.setDimensions(parent.getWidth(), parent.getHeight());
    });

    this.update();
  }

  @Override
  protected void update() {
    this.button.active = !this.getOption().isDisabled();
    this.button.setMessage(this.messageFactory.apply(this.getOption()));
  }

  public static <D, O extends ConfigOption<D>> Function<O, Text> getValueDisplayMessageFactory() {
    return (option) -> Text.of(option.getPendingValueAsString());
  }

  @FunctionalInterface
  public interface SubScreenFactory<D, O extends ConfigOption<D>> {
    Screen create(Screen screen, O option);
  }
}
