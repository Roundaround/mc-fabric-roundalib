package me.roundaround.testmod.client.screen;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.screen.PositionEditScreen;
import me.roundaround.roundalib.client.gui.widget.config.SubScreenControl;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ExamplePositionEditScreen extends PositionEditScreen {
  private ExamplePositionEditScreen(Screen parent, PositionConfigOption configOption) {
    super(Text.literal("Edit position"), parent, configOption);
  }

  public static SubScreenControl.SubScreenFactory<Position, PositionConfigOption> getSubScreenFactory() {
    return ExamplePositionEditScreen::new;
  }

  @Override
  protected void renderContent(
      DrawContext drawContext, int mouseX, int mouseY, float partialTicks) {
    super.renderContent(drawContext, mouseX, mouseY, partialTicks);
    drawContext.drawCenteredTextWithShadow(textRenderer,
        Text.literal(getValue().toString()).asOrderedText(),
        width / 2,
        height / 2,
        GuiUtil.LABEL_COLOR);
  }
}
