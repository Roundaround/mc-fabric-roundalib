package me.roundaround.roundalib.config.gui.widget;

import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;

public abstract class ResetButtonWidget<T> extends IconButtonWidget<T> {
  protected static final Text TOOLTIP = Text.translatable("roundalib.reset.tooltip");

  public ResetButtonWidget(T parent, ModConfig config, int top, int left) {
    this(parent, config, top, left, (button) -> {
      ((ResetButtonWidget<T>) button).performReset();
    });
  }

  public ResetButtonWidget(T parent, ModConfig config, int top, int left, PressAction<T> pressAction) {
    super(parent, config, top, left, true, IconButtonWidget.UV_LG_UNDO, TOOLTIP, pressAction);
  }

  protected void performReset() {
    getConfigOption().resetToDefault();
  }

  @Override
  protected boolean isDisabled() {
    return !getConfigOption().isModified();
  }

  @Override
  public void appendTitleNarration(NarrationMessageBuilder builder) {
    builder.put(NarrationPart.TITLE, Text.translatable("narrator.controls.reset", getConfigOption().getLabel()));
  }

  protected abstract ConfigOption<?, ?> getConfigOption();
}
