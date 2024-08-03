package me.roundaround.testmod.client.screen.demo;

import me.roundaround.roundalib.asset.icon.BuiltinIcon;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.linear.LinearLayoutWidget;
import me.roundaround.roundalib.client.gui.layout.screen.ThreeSectionLayoutWidget;
import me.roundaround.roundalib.client.gui.util.Axis;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.drawable.FrameWidget;
import me.roundaround.roundalib.client.gui.widget.drawable.LabelWidget;
import me.roundaround.testmod.TestMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class FrameWidgetDemoScreen extends Screen implements DemoScreen {
  private static final Text TITLE_TEXT = Text.translatable("testmod.framewidgetdemoscreen.title");

  private final Screen parent;
  private final ThreeSectionLayoutWidget layout = new ThreeSectionLayoutWidget(this);

  private ButtonWidget activeButton;
  private FrameWidget frameWidget;
  private int overflow = FrameWidget.DEFAULT_OVERFLOW;
  private LabelWidget overflowLabel;
  private IconButtonWidget overflowMinusButton;
  private IconButtonWidget overflowPlusButton;

  public FrameWidgetDemoScreen(Screen parent) {
    super(TITLE_TEXT);
    this.parent = parent;
  }

  @Override
  protected void init() {
    this.layout.addHeader(this.textRenderer, this.getTitle());

    LinearLayoutWidget buttonRow = LinearLayoutWidget.horizontal()
        .spacing(GuiUtil.PADDING)
        .defaultOffAxisContentAlignCenter();
    this.overflowLabel = buttonRow.add(LabelWidget.builder(this.textRenderer, this.getOverflowLabel()).build());
    this.overflowMinusButton = buttonRow.add(IconButtonWidget.builder(BuiltinIcon.MINUS_18, TestMod.MOD_ID)
        .onPress((button) -> this.onOverflowChange(this.overflow - 1))
        .build());
    this.overflowPlusButton = buttonRow.add(IconButtonWidget.builder(BuiltinIcon.PLUS_18, TestMod.MOD_ID)
        .onPress((button) -> this.onOverflowChange(this.overflow + 1))
        .build());
    this.layout.addHeader(buttonRow);

    this.activeButton = IconButtonWidget.builder(BuiltinIcon.HELP_18, TestMod.MOD_ID)
        .vanillaSize()
        .onPress(this::setActive)
        .build();

    this.layout.getBody().flowAxis(Axis.HORIZONTAL);
    this.layout.addBody(this.activeButton);
    this.layout.addBody(ButtonWidget.builder(Text.of("Wide"), this::setActive).size(80, 20).build());
    this.layout.addBody(ButtonWidget.builder(Text.of("Tall"), this::setActive).size(40, 80).build());

    this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.close()).build());

    this.frameWidget = this.layout.addNonPositioned(new FrameWidget(this.activeButton));

    this.layout.forEachChild(this::addDrawableChild);
    this.initTabNavigation();
  }

  private void setActive(ButtonWidget button) {
    this.activeButton = button;
    this.frameWidget.frame(this.activeButton);
  }

  @Override
  protected void initTabNavigation() {
    this.layout.refreshPositions();
  }

  @Override
  public void close() {
    Objects.requireNonNull(this.client).setScreen(this.parent);
  }

  private void onOverflowChange(int overflow) {
    this.overflow = overflow;
    this.frameWidget.setOverflow(this.overflow);
    this.overflowMinusButton.active = this.overflow > 0;
    this.overflowPlusButton.active = this.overflow < 8;
    this.overflowLabel.setText(this.getOverflowLabel());
  }

  private Text getOverflowLabel() {
    return Text.of(String.format("Overflow (%s):", this.overflow));
  }
}
