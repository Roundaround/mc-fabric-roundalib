package me.roundaround.roundalib.config.gui.widget;

import me.roundaround.roundalib.config.gui.control.IntInputControl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class IntStepButtonWidget extends IconButtonWidget<IntInputControl> {
  private boolean increment;

  public IntStepButtonWidget(IntInputControl parent, boolean increment, int top, int left) {
    super(
        parent,
        top,
        left,
        false,
        increment ? 0 : 1,
        Text.translatable(increment ? "roundalib.step_up.tooltip" : "roundalib.step_down.tooltip",
            parent.getConfigOption().getStep()),
        IntStepButtonWidget::onPress);
    this.increment = increment;
  }

  @Override
  public void tick() {
    if (isDisabled() && isFocused()) {
      getOptionRow().focusPrimaryElement();
    }
  }

  @Override
  public boolean setIsFocused(boolean focused) {
    if (focused && isDisabled()) {
      return false;
    }
    return super.setIsFocused(focused);
  }

  @Override
  protected boolean isDisabled() {
    return increment && !parent.getConfigOption().canIncrement()
        || !increment && !parent.getConfigOption().canDecrement();
  }

  @Override
  protected void appendTitleNarration(NarrationMessageBuilder builder) {
    builder.put(NarrationPart.TITLE,
        Text.translatable("roundalib.step.narration", parent.getConfigOption().getLabel()));
  }

  public OptionRowWidget getOptionRow() {
    return getParent().getOptionRow();
  }

  private static <T> void onPress(IconButtonWidget<T> rawButton) {
    if (!(rawButton instanceof IntStepButtonWidget)) {
      return;
    }

    IntStepButtonWidget button = (IntStepButtonWidget) rawButton;
    if (button.increment) {
      button.parent.getConfigOption().increment();
    } else {
      button.parent.getConfigOption().decrement();
    }
    SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
    soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1));
  }
}
