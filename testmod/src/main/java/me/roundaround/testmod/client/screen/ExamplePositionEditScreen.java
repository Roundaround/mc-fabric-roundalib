package me.roundaround.testmod.client.screen;

import me.roundaround.roundalib.client.gui.GuiUtil;
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

    int row1Y = centerY - 60;
    int row2Y = centerY - 40;
    int row3Y = centerY - 20;
    int row4Y = centerY;
    int row5Y = centerY + 20;
    int row6Y = centerY + 40;

    this.addDrawable((context, mouseX, mouseY, delta) -> {
      context.fill(centerX - 40, row1Y - 5, centerX + 40, row1Y + 5, GuiUtil.genColorInt(0, 0, 0));
      context.drawBorder(centerX - 41, row1Y - 6, 82, 12, GuiUtil.genColorInt(0, 0.3f, 0.8f));
    });
    this.addDrawable(
        LabelElement.builder(this.textRenderer, Text.of("This is a very long label that needs to scroll"), centerX,
                row1Y
            )
            .maxWidth(80)
            .justifiedCenter()
            .alignedMiddle()
            .overflowBehavior(LabelElement.OverflowBehavior.SCROLL)
            .hideBackground()
            .build());

    this.addDrawable((context, mouseX, mouseY, delta) -> {
      context.fill(centerX, row2Y - 5, centerX + 80, row2Y + 5, GuiUtil.genColorInt(0, 0, 0));
      context.drawBorder(centerX - 1, row2Y - 6, 82, 12, GuiUtil.genColorInt(0, 0.3f, 0.8f));
    });
    this.addDrawable(
        LabelElement.builder(this.textRenderer, Text.of("This is a very long label that needs to scroll"), centerX,
                row2Y
            )
            .maxWidth(80)
            .justifiedLeft()
            .alignedMiddle()
            .overflowBehavior(LabelElement.OverflowBehavior.SCROLL)
            .hideBackground()
            .build());

    this.addDrawable((context, mouseX, mouseY, delta) -> {
      context.fill(centerX - 80, row3Y - 5, centerX, row3Y + 5, GuiUtil.genColorInt(0, 0, 0));
      context.drawBorder(centerX - 81, row3Y - 6, 82, 12, GuiUtil.genColorInt(0, 0.3f, 0.8f));
    });
    this.addDrawable(
        LabelElement.builder(this.textRenderer, Text.of("This is a very long label that needs to scroll"), centerX,
                row3Y
            )
            .maxWidth(80)
            .justifiedRight()
            .alignedMiddle()
            .overflowBehavior(LabelElement.OverflowBehavior.SCROLL)
            .hideBackground()
            .build());

    this.addDrawable((context, mouseX, mouseY, delta) -> {
      context.fill(centerX - 40, row4Y - 5, centerX + 40, row4Y + 5, GuiUtil.genColorInt(0, 0, 0));
      context.drawBorder(centerX - 41, row4Y - 6, 82, 12, GuiUtil.genColorInt(0, 0.3f, 0.8f));
    });
    this.addDrawable(
        LabelElement.builder(this.textRenderer, Text.of("This is a very long label that will be clipped"), centerX,
                row4Y
            )
            .maxWidth(80)
            .justifiedCenter()
            .alignedMiddle()
            .overflowBehavior(LabelElement.OverflowBehavior.CLIP)
            .hideBackground()
            .build());

    this.addDrawable((context, mouseX, mouseY, delta) -> {
      context.fill(centerX - 40, row5Y - 5, centerX + 40, row5Y + 5, GuiUtil.genColorInt(0, 0, 0));
      context.drawBorder(centerX - 41, row5Y - 6, 82, 12, GuiUtil.genColorInt(0, 0.3f, 0.8f));
    });
    this.addDrawable(
        LabelElement.builder(this.textRenderer, Text.of("This is a very long label that will be truncated"), centerX,
                row5Y
            )
            .maxWidth(80)
            .justifiedCenter()
            .alignedMiddle()
            .overflowBehavior(LabelElement.OverflowBehavior.TRUNCATE)
            .hideBackground()
            .build());

    this.addDrawable((context, mouseX, mouseY, delta) -> {
      context.fill(centerX - 40, row6Y - 5, centerX + 40, row6Y + 5 + this.textRenderer.fontHeight,
          GuiUtil.genColorInt(0, 0, 0)
      );
      context.drawBorder(
          centerX - 41, row6Y - 6, 82, 12 + this.textRenderer.fontHeight, GuiUtil.genColorInt(0, 0.3f, 0.8f));
    });
    this.addDrawable(
        LabelElement.builder(this.textRenderer, Text.of("This is a very long label that will be wrapped"), centerX,
                row6Y
            )
            .maxWidth(80)
            .justifiedCenter()
            .alignedMiddle()
            .overflowBehavior(LabelElement.OverflowBehavior.WRAP)
            .maxLines(2)
            .hideBackground()
            .build());

    this.valueLabel = this.addDrawable(
        LabelElement.builder(this.textRenderer, Text.of(getValue().toString()), centerX, GuiUtil.DEFAULT_HEADER_HEIGHT)
            .justifiedCenter()
            .alignedTop()
            .build());
    this.addDrawable(this.valueLabel);

    super.init();
  }

  @Override
  protected void onValueChanged(Position prev, Position curr) {
    this.valueLabel.setText(Text.of(curr.toString()));
  }

  public static SubScreenControl.SubScreenFactory<Position, PositionConfigOption> getSubScreenFactory() {
    return ExamplePositionEditScreen::new;
  }
}
