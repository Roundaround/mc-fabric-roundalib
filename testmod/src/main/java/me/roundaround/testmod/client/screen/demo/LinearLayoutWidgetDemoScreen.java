package me.roundaround.testmod.client.screen.demo;

import me.roundaround.roundalib.asset.icon.BuiltinIcon;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.FillerWidget;
import me.roundaround.roundalib.client.gui.layout.WrapperLayoutWidget;
import me.roundaround.roundalib.client.gui.layout.linear.LinearLayoutWidget;
import me.roundaround.roundalib.client.gui.layout.screen.ThreeSectionLayoutWidget;
import me.roundaround.roundalib.client.gui.util.Alignment;
import me.roundaround.roundalib.client.gui.util.Axis;
import me.roundaround.roundalib.client.gui.util.IntRect;
import me.roundaround.roundalib.client.gui.widget.drawable.CrosshairWidget;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.drawable.LabelWidget;
import me.roundaround.testmod.TestMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class LinearLayoutWidgetDemoScreen extends Screen implements DemoScreen {
  private static final Text TITLE_TEXT = Text.translatable("testmod.linearlayoutdemoscreen.title");

  private final Screen parent;
  private final ThreeSectionLayoutWidget layout = new ThreeSectionLayoutWidget(this);

  private LinearLayoutWidget demoLayout;
  private IconButtonWidget spacingMinusButton;
  private IconButtonWidget spacingPlusButton;
  private int spacing = GuiUtil.PADDING;
  private CrosshairWidget crosshair;

  public LinearLayoutWidgetDemoScreen(Screen parent) {
    super(TITLE_TEXT);
    this.parent = parent;
  }

  @Override
  protected void init() {
    this.layout.addHeader(this.textRenderer, this.getTitle());

    LinearLayoutWidget firstRow = LinearLayoutWidget.horizontal()
        .spacing(GuiUtil.PADDING)
        .defaultOffAxisContentAlignCenter();
    firstRow.add(
        new CyclingButtonWidget.Builder<Axis>((value) -> value.getDisplayText(TestMod.MOD_ID)).values(Axis.values())
            .initially(Axis.HORIZONTAL)
            .build(0, 0, 100, 20, Text.of("Axis"), this::onFlowAxisChange));
    firstRow.add(new CyclingButtonWidget.Builder<Alignment>((value) -> value.getDisplayText(TestMod.MOD_ID)).values(
            Alignment.values())
        .initially(Alignment.START)
        .build(0, 0, 100, 20, Text.of("X"), this::onAlignmentXChange));
    firstRow.add(new CyclingButtonWidget.Builder<Alignment>((value) -> value.getDisplayText(TestMod.MOD_ID)).values(
            Alignment.values())
        .initially(Alignment.START)
        .build(0, 0, 100, 20, Text.of("Y"), this::onAlignmentYChange));
    this.layout.addHeader(firstRow);

    LinearLayoutWidget secondRow = LinearLayoutWidget.horizontal()
        .spacing(GuiUtil.PADDING)
        .defaultOffAxisContentAlignCenter();
    secondRow.add(new CyclingButtonWidget.Builder<Alignment>((value) -> value.getDisplayText(TestMod.MOD_ID)).values(
            Alignment.values())
        .initially(Alignment.CENTER)
        .build(0, 0, 100, 20, Text.of("Content"), this::onContentAlignmentChange));
    secondRow.add(FillerWidget.ofWidth(2 * GuiUtil.PADDING));
    secondRow.add(LabelWidget.builder(this.textRenderer, Text.of("Spacing:")).build());
    this.spacingMinusButton = secondRow.add(IconButtonWidget.builder(BuiltinIcon.MINUS_18, TestMod.MOD_ID)
        .onPress((button) -> this.onSpacingChange(this.spacing - 1))
        .build());
    this.spacingPlusButton = secondRow.add(IconButtonWidget.builder(BuiltinIcon.PLUS_18, TestMod.MOD_ID)
        .onPress((button) -> this.onSpacingChange(this.spacing + 1))
        .build());
    this.layout.addHeader(secondRow);

    this.layout.setHeaderHeight(this.layout.getHeader().getContentHeight() + 2 * GuiUtil.PADDING);

    this.crosshair = this.addDrawable(new CrosshairWidget(this.layout.getBody().getBounds()));
    this.layout.setBodyLayoutHook((parent, self) -> {
      this.crosshair.centerOn(this.layout.getBody().getBounds());
    });

    this.demoLayout = LinearLayoutWidget.horizontal().spacing(this.spacing);
    this.demoLayout.add(IconButtonWidget.builder(BuiltinIcon.MINUS_13, TestMod.MOD_ID).medium().build());
    this.demoLayout.add(IconButtonWidget.builder(BuiltinIcon.CHECKMARK_18, TestMod.MOD_ID).vanillaSize().build());
    this.demoLayout.add(IconButtonWidget.builder(BuiltinIcon.PLUS_13, TestMod.MOD_ID).medium().build());
    this.demoLayout.add(LabelWidget.builder(this.textRenderer, Text.of("Label")).build());
    this.layout.addBody(new WrapperLayoutWidget<>(this.demoLayout, (parent, self) -> {
      self.setPosition(this.crosshair.getX() + 1, this.crosshair.getY() + 1);
    }));

    this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.close()).build());

    this.addDrawable((context, mouseX, mouseY, delta) -> {
      IntRect bounds = LinearLayoutWidgetDemoScreen.this.demoLayout.getBounds();
      GuiUtil.fill(context, bounds, GuiUtil.genColorInt(0, 0.4f, 0.9f, 0.3f));
    });

    this.layout.forEachChild(this::addDrawableChild);
    this.initTabNavigation();
  }

  @Override
  protected void initTabNavigation() {
    this.layout.refreshPositions();
  }

  @Override
  public void close() {
    Objects.requireNonNull(this.client).setScreen(this.parent);
  }

  private void onFlowAxisChange(CyclingButtonWidget<Axis> button, Axis value) {
    this.demoLayout.flowAxis(value);
    this.layout.refreshPositions();
  }

  private void onAlignmentXChange(CyclingButtonWidget<Alignment> button, Alignment value) {
    switch (value) {
      case START -> this.demoLayout.alignSelfLeft();
      case CENTER -> this.demoLayout.alignSelfCenterX();
      case END -> this.demoLayout.alignSelfRight();
    }
    this.layout.refreshPositions();
  }

  private void onAlignmentYChange(CyclingButtonWidget<Alignment> button, Alignment value) {
    switch (value) {
      case START -> this.demoLayout.alignSelfTop();
      case CENTER -> this.demoLayout.alignSelfCenterY();
      case END -> this.demoLayout.alignSelfBottom();
    }
    this.layout.refreshPositions();
  }

  private void onContentAlignmentChange(CyclingButtonWidget<Alignment> button, Alignment value) {
    switch (value) {
      case START -> this.demoLayout.defaultOffAxisContentAlignStart();
      case CENTER -> this.demoLayout.defaultOffAxisContentAlignCenter();
      case END -> this.demoLayout.defaultOffAxisContentAlignEnd();
    }
    this.layout.refreshPositions();
  }

  private void onSpacingChange(int spacing) {
    this.spacing = spacing;
    this.demoLayout.spacing(this.spacing);
    this.spacingMinusButton.active = this.spacing > 0;
    this.spacingPlusButton.active = this.spacing < 8;
    this.layout.refreshPositions();
  }
}
