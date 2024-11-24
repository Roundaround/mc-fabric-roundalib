package me.roundaround.testmod.client.screen.demo;

import me.roundaround.roundalib.asset.icon.BuiltinIcon;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.FillerWidget;
import me.roundaround.roundalib.client.gui.layout.linear.LinearLayoutWidget;
import me.roundaround.roundalib.client.gui.layout.screen.ThreeSectionLayoutWidget;
import me.roundaround.roundalib.client.gui.util.Alignment;
import me.roundaround.roundalib.client.gui.util.Axis;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.drawable.CrosshairWidget;
import me.roundaround.roundalib.client.gui.widget.drawable.LabelWidget;
import me.roundaround.testmod.TestMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Objects;

import static me.roundaround.roundalib.client.gui.widget.drawable.LabelWidget.OverflowBehavior;

@Environment(EnvType.CLIENT)
public class MultilineLabelDemoScreen extends Screen implements DemoScreen {
  private static final Text TITLE_TEXT = Text.translatable("testmod.multilinelabeldemoscreen.title");

  private final Screen parent;

  private ThreeSectionLayoutWidget layout;
  private LabelWidget label;
  private Size size = Size.AUTO;
  private IconButtonWidget linesMinusButton;
  private IconButtonWidget linesPlusButton;
  private int lineCount = 3;
  private IconButtonWidget spacingMinusButton;
  private IconButtonWidget spacingPlusButton;
  private int spacing = 1;

  public MultilineLabelDemoScreen(Screen parent) {
    super(TITLE_TEXT);
    this.parent = parent;
  }

  @Override
  protected void init() {
    this.layout = new ThreeSectionLayoutWidget(this);

    this.layout.addHeader(this.textRenderer, this.getTitle());

    LinearLayoutWidget firstRow = LinearLayoutWidget.horizontal()
        .spacing(GuiUtil.PADDING)
        .defaultOffAxisContentAlignCenter();
    firstRow.add(new CyclingButtonWidget.Builder<Size>((value) -> Text.of(value.name())).values(Size.values())
        .initially(this.size)
        .build(0, 0, 80, 20, Text.of("Size"), this::onSizeChange));
    firstRow.add(new CyclingButtonWidget.Builder<Alignment>(
        (value) -> value.getDisplayText(TestMod.MOD_ID, Axis.HORIZONTAL)).values(Alignment.values())
        .initially(Alignment.START)
        .build(0, 0, 80, 20, Text.of("Self X"), this::onAlignSelfXChange));
    firstRow.add(new CyclingButtonWidget.Builder<Alignment>(
        (value) -> value.getDisplayText(TestMod.MOD_ID, Axis.VERTICAL)).values(Alignment.values())
        .initially(Alignment.START)
        .build(0, 0, 80, 20, Text.of("Self Y"), this::onAlignSelfYChange));
    this.layout.addHeader(firstRow);

    LinearLayoutWidget secondRow = LinearLayoutWidget.horizontal()
        .spacing(GuiUtil.PADDING)
        .defaultOffAxisContentAlignCenter();
    secondRow.add(
        new CyclingButtonWidget.Builder<OverflowBehavior>((value) -> value.getDisplayText(TestMod.MOD_ID)).values(
                OverflowBehavior.values())
            .initially(OverflowBehavior.SHOW)
            .build(0, 0, 80, 20, Text.of("Over"), this::onOverflowBehaviorChange));
    secondRow.add(new CyclingButtonWidget.Builder<Alignment>(
        (value) -> value.getDisplayText(TestMod.MOD_ID, Axis.HORIZONTAL)).values(Alignment.values())
        .initially(Alignment.START)
        .build(0, 0, 80, 20, Text.of("Text X"), this::onAlignTextXChange));
    secondRow.add(new CyclingButtonWidget.Builder<Alignment>(
        (value) -> value.getDisplayText(TestMod.MOD_ID, Axis.VERTICAL)).values(Alignment.values())
        .initially(Alignment.START)
        .build(0, 0, 80, 20, Text.of("Text Y"), this::onAlignTextYChange));
    this.layout.addHeader(secondRow);

    LinearLayoutWidget thirdRow = LinearLayoutWidget.horizontal()
        .spacing(GuiUtil.PADDING)
        .defaultOffAxisContentAlignCenter();
    thirdRow.add(CyclingButtonWidget.onOffBuilder(ScreenTexts.YES, ScreenTexts.NO)
        .initially(false)
        .build(0, 0, 80, 20, Text.of("Shadow"), this::onShadowChange));
    thirdRow.add(FillerWidget.ofWidth(GuiUtil.PADDING));
    thirdRow.add(LabelWidget.builder(this.textRenderer, Text.of("Lines:")).build());
    this.linesMinusButton = thirdRow.add(IconButtonWidget.builder(BuiltinIcon.MINUS_18, TestMod.MOD_ID)
        .onPress((button) -> this.onLineCountChange(this.lineCount - 1))
        .build());
    this.linesPlusButton = thirdRow.add(IconButtonWidget.builder(BuiltinIcon.PLUS_18, TestMod.MOD_ID)
        .onPress((button) -> this.onLineCountChange(this.lineCount + 1))
        .build());
    thirdRow.add(FillerWidget.ofWidth(GuiUtil.PADDING));
    thirdRow.add(LabelWidget.builder(this.textRenderer, Text.of("Spacing:")).build());
    this.spacingMinusButton = thirdRow.add(IconButtonWidget.builder(BuiltinIcon.MINUS_18, TestMod.MOD_ID)
        .onPress((button) -> this.onSpacingChange(this.spacing - 1))
        .build());
    this.spacingPlusButton = thirdRow.add(IconButtonWidget.builder(BuiltinIcon.PLUS_18, TestMod.MOD_ID)
        .onPress((button) -> this.onSpacingChange(this.spacing + 1))
        .build());
    this.layout.addHeader(thirdRow);

    this.layout.setHeaderHeight(this.layout.getHeader().getContentHeight() + 2 * GuiUtil.PADDING);

    CrosshairWidget crosshairWidget = this.layout.addBody(new CrosshairWidget());

    this.label = this.layout.addNonPositioned(LabelWidget.builder(this.textRenderer, this.generateLines())
        .alignSelfLeft()
        .alignSelfTop()
        .alignTextLeft()
        .alignTextTop()
        .lineSpacing(this.spacing)
        .build(), (parent, self) -> {
      if (this.size == Size.FULL) {
        self.setPosition(this.layout.getBody().getX(), this.layout.getBody().getY());
      } else {
        self.setPosition(
            crosshairWidget.getX() + crosshairWidget.getWidth() / 2,
            crosshairWidget.getY() + crosshairWidget.getHeight() / 2
        );
      }
    });

    this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.close()).build());

    this.addDrawable(((context, mouseX, mouseY, delta) -> {
      GuiUtil.fill(context, this.label.getWidgetBounds(), GuiUtil.genColorInt(0, 0.4f, 0.9f, 0.3f));
    }));

    this.layout.forEachChild(this::addDrawableChild);
    this.refreshWidgetPositions();
  }

  @Override
  protected void refreshWidgetPositions() {
    this.layout.refreshPositions();
  }

  @Override
  public void close() {
    Objects.requireNonNull(this.client).setScreen(this.parent);
  }

  private void onOverflowBehaviorChange(
      CyclingButtonWidget<OverflowBehavior> button, OverflowBehavior value
  ) {
    this.label.setOverflowBehavior(value);
    this.layout.refreshPositions();
  }

  private void onSizeChange(CyclingButtonWidget<Size> button, Size value) {
    this.size = value;

    int width = switch (value) {
      case AUTO -> 0;
      case SIXTY -> 60;
      case FULL -> this.layout.getBody().getWidth();
    };
    int height = switch (value) {
      case AUTO -> 0;
      case SIXTY -> 60;
      case FULL -> this.layout.getBody().getHeight();
    };

    this.label.setDimensions(width, height);
    this.layout.refreshPositions();
  }

  private void onAlignSelfXChange(CyclingButtonWidget<Alignment> button, Alignment value) {
    this.label.setAlignSelfX(value);
    this.layout.refreshPositions();
  }

  private void onAlignSelfYChange(CyclingButtonWidget<Alignment> button, Alignment value) {
    this.label.setAlignSelfY(value);
    this.layout.refreshPositions();
  }

  private void onAlignTextXChange(CyclingButtonWidget<Alignment> button, Alignment value) {
    this.label.setAlignTextX(value);
    this.layout.refreshPositions();
  }

  private void onAlignTextYChange(CyclingButtonWidget<Alignment> button, Alignment value) {
    this.label.setAlignTextY(value);
    this.layout.refreshPositions();
  }

  private void onShadowChange(CyclingButtonWidget<Boolean> button, boolean value) {
    this.label.setShadow(value);
    this.layout.refreshPositions();
  }

  private void onLineCountChange(int lineCount) {
    this.lineCount = lineCount;
    this.label.setText(this.generateLines());
    this.linesMinusButton.active = this.lineCount > 1;
    this.linesPlusButton.active = this.lineCount < 5;
    this.layout.refreshPositions();
  }

  private ArrayList<Text> generateLines() {
    ArrayList<Text> lines = new ArrayList<>(this.lineCount);
    for (int i = 0; i < this.lineCount; i++) {
      int line = i + 1;
      String border = "=".repeat(line);
      lines.add(Text.literal(String.format("%s %s %s", border, line, border)));
    }
    return lines;
  }

  private void onSpacingChange(int spacing) {
    this.spacing = spacing;
    this.label.setLineSpacing(this.spacing);
    this.spacingMinusButton.active = this.spacing > 0;
    this.spacingPlusButton.active = this.spacing < 8;
    this.layout.refreshPositions();
  }

  private enum Size {
    AUTO, SIXTY, FULL
  }
}
