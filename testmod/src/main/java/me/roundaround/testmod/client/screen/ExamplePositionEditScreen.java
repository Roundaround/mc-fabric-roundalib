package me.roundaround.testmod.client.screen;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.LabelElement;
import me.roundaround.roundalib.client.gui.layout.IntRect;
import me.roundaround.roundalib.client.gui.screen.PositionEditScreen;
import me.roundaround.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
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

    LabelElement label1 = LabelElement.builder(this.textRenderer,
            Text.of("This is a very long label that needs to scroll"), centerX, row1Y
        )
        .maxWidth(80)
        .justifiedCenter()
        .alignedMiddle()
        .overflowBehavior(LabelElement.OverflowBehavior.SCROLL)
        .hideBackground()
        .build();
    this.addDrawable(new LabelRenderer(label1));

    LabelElement label2 = LabelElement.builder(this.textRenderer,
            Text.of("This is a very long label that needs to scroll"), centerX, row2Y
        )
        .maxWidth(80)
        .justifiedLeft()
        .alignedMiddle()
        .overflowBehavior(LabelElement.OverflowBehavior.SCROLL)
        .hideBackground()
        .build();
    this.addDrawable(new LabelRenderer(label2));

    LabelElement label3 = LabelElement.builder(this.textRenderer,
            Text.of("This is a very long label that needs to scroll"), centerX, row3Y
        )
        .maxWidth(80)
        .justifiedRight()
        .alignedMiddle()
        .overflowBehavior(LabelElement.OverflowBehavior.SCROLL)
        .hideBackground()
        .build();
    this.addDrawable(new LabelRenderer(label3));

    LabelElement label4 = LabelElement.builder(this.textRenderer,
            Text.of("This is a very long label that will be clipped"), centerX, row4Y
        )
        .maxWidth(80)
        .justifiedCenter()
        .alignedMiddle()
        .overflowBehavior(LabelElement.OverflowBehavior.CLIP)
        .hideBackground()
        .build();
    this.addDrawable(new LabelRenderer(label4));

    LabelElement label5 = LabelElement.builder(this.textRenderer,
            Text.of("This is a very long label that will be truncated"), centerX, row5Y
        )
        .maxWidth(80)
        .justifiedCenter()
        .alignedMiddle()
        .overflowBehavior(LabelElement.OverflowBehavior.TRUNCATE)
        .hideBackground()
        .build();
    this.addDrawable(new LabelRenderer(label5));

    LabelElement label6 = LabelElement.builder(this.textRenderer,
            Text.of("This is a very long label that will be wrapped"), centerX, row6Y
        )
        .maxWidth(80)
        .justifiedCenter()
        .alignedMiddle()
        .overflowBehavior(LabelElement.OverflowBehavior.WRAP)
        .maxLines(2)
        .hideBackground()
        .build();
    this.addDrawable(new LabelRenderer(label6));

    this.valueLabel = this.addDrawable(
        LabelElement.builder(this.textRenderer, Text.of(this.getValueAsString()), centerX,
                GuiUtil.DEFAULT_HEADER_HEIGHT
            )
            .justifiedCenter()
            .alignedTop()
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

  private record LabelRenderer(LabelElement label) implements Drawable {
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      IntRect bounds = this.label().getTextBounds();
      context.fill(
          bounds.getLeft(), bounds.getTop(), bounds.getRight(), bounds.getBottom(), GuiUtil.genColorInt(0, 0, 0));
      bounds.expand(1);
      context.drawBorder(
          bounds.getLeft(), bounds.getTop(), bounds.getWidth(), bounds.getHeight(), GuiUtil.genColorInt(0, 0.3f, 0.8f));

      this.label.render(context, mouseX, mouseY, delta);
    }
  }
}
