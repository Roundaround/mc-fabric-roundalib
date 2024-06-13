package me.roundaround.testmod.client.screen;

import me.roundaround.roundalib.client.gui.LabelElement;
import me.roundaround.roundalib.client.gui.screen.PositionEditScreen;
import me.roundaround.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ExamplePositionEditScreen extends PositionEditScreen {
  private LabelElement valueLabel;

  private ExamplePositionEditScreen(Screen parent, PositionConfigOption configOption) {
    super(Text.literal("Edit position"), parent, configOption);
  }

  @Override
  protected void init() {
    int centerX = this.width / 2;
    int centerY = this.height / 2;

    this.valueLabel = this.addDrawable(
        LabelElement.builder(this.textRenderer, Text.of(this.getValueAsString()), centerX, centerY)
            .justifiedCenter()
            .alignedMiddle()
            .build());
    this.addDrawable(this.valueLabel);

    super.init();
  }

  @Override
  public void onValueChanged(Position value) {
    this.valueLabel.setText(Text.of(value.toString()));
  }

  public static SubScreenControl.SubScreenFactory<Position, PositionConfigOption> getSubScreenFactory() {
    return ExamplePositionEditScreen::new;
  }
}
