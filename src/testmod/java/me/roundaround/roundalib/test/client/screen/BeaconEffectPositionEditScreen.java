package me.roundaround.roundalib.test.client.screen;

import me.roundaround.roundalib.config.gui.GuiUtil;
import me.roundaround.roundalib.config.gui.control.SubScreenControl.SubScreenFactory;
import me.roundaround.roundalib.config.gui.screen.PositionEditScreen;
import me.roundaround.roundalib.config.option.PositionConfigOption;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class BeaconEffectPositionEditScreen extends PositionEditScreen {
  private BeaconEffectPositionEditScreen(Screen parent, PositionConfigOption configOption) {
    super(Text.literal("Edit beacon effect position"), parent, configOption);
  }

  public static SubScreenFactory<Position, PositionConfigOption> getSubScreenFactory() {
    return BeaconEffectPositionEditScreen::new;
  }

  @Override
  protected void renderContent(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    drawCenteredTextWithShadow(
        matrixStack,
        textRenderer,
        Text.literal("Testing").asOrderedText(),
        width / 2,
        height / 2,
        GuiUtil.LABEL_COLOR);
  }
}
