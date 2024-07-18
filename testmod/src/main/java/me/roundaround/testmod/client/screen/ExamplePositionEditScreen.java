package me.roundaround.testmod.client.screen;

import me.roundaround.roundalib.client.gui.screen.PositionEditScreen;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import me.roundaround.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ExamplePositionEditScreen extends PositionEditScreen {
  private LabelWidget valueLabel;

  private ExamplePositionEditScreen(Screen parent, PositionConfigOption configOption) {
    super(Text.literal("Edit position"), parent, configOption);
  }

  @Override
  protected void initBody() {
    this.valueLabel = this.addDrawableChild(LabelWidget.builder(this.textRenderer, Text.of(this.getValueAsString()))
        .refPosition(this.getCenterX(), this.getCenterY())
        .dimensions(this.width, this.layout.getContentHeight())
        .alignCenterX()
        .alignCenterY()
        .build());

    super.initBody();
  }

  @Override
  protected void initTabNavigation() {
    super.initTabNavigation();
    this.valueLabel.batchUpdates(() -> {
      this.valueLabel.setPosition(this.getCenterX(), this.getCenterY());
      this.valueLabel.setDimensions(this.width, this.layout.getContentHeight());
    });
  }

  @Override
  public void onPendingValueChange(Position value) {
    super.onPendingValueChange(value);
    this.valueLabel.setText(Text.of(value.toString()));
  }

  private int getCenterX() {
    return this.width / 2;
  }

  private int getCenterY() {
    return this.layout.getHeaderHeight() + this.layout.getContentHeight() / 2;
  }

  public static SubScreenControl.SubScreenFactory<Position, PositionConfigOption> getSubScreenFactory() {
    return ExamplePositionEditScreen::new;
  }
}
