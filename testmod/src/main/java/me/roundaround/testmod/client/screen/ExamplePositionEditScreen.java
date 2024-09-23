package me.roundaround.testmod.client.screen;

import me.roundaround.roundalib.client.gui.screen.ConfigScreen;
import me.roundaround.roundalib.client.gui.screen.PositionEditScreen;
import me.roundaround.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.roundalib.client.gui.widget.drawable.LabelWidget;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.text.Text;

public class ExamplePositionEditScreen extends PositionEditScreen {
  private LabelWidget valueLabel;

  private ExamplePositionEditScreen(ConfigScreen parent, PositionConfigOption configOption) {
    super(Text.literal("Edit position"), parent, configOption);
  }

  @Override
  protected void initElements() {
    this.valueLabel = this.nonPositioningRoot.add(
        LabelWidget.builder(this.textRenderer, Text.of(this.getValueAsString()))
            .alignSelfCenterX()
            .alignSelfCenterY()
            .build(), (parent, self) -> {
          self.setPosition(parent.getX() + parent.getWidth() / 2, parent.getY() + parent.getHeight() / 2);
        });
    this.subscriptions.add(this.getOption().pendingValue.subscribe((pendingValue) -> {
      this.valueLabel.setText(Text.of(pendingValue.toString()));
    }));

    super.initElements();
  }

  public static SubScreenControl.SubScreenFactory<Position, PositionConfigOption> getSubScreenFactory() {
    return ExamplePositionEditScreen::new;
  }
}
