package me.roundaround.testmod.client.screen.demo;

import me.roundaround.roundalib.asset.icon.BuiltinIcon;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.WrapperLayoutWidget;
import me.roundaround.roundalib.client.gui.layout.linear.LinearLayoutWidget;
import me.roundaround.roundalib.client.gui.layout.screen.ThreeSectionLayoutWidget;
import me.roundaround.roundalib.client.gui.util.Alignment;
import me.roundaround.roundalib.client.gui.util.IntRect;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import me.roundaround.testmod.TestMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Objects;

import static me.roundaround.roundalib.client.gui.widget.LabelWidget.OverflowBehavior;

@Environment(EnvType.CLIENT)
public class MultilineLabelDemoScreen extends Screen implements DemoScreen {
  private static final Text TITLE_TEXT = Text.translatable("testmod.multilinelabeldemoscreen.title");

  private final Screen parent;
  private final ThreeSectionLayoutWidget layout = new ThreeSectionLayoutWidget(this);

  private LabelWidget label;
  private IconButtonWidget minusButton;
  private IconButtonWidget plusButton;
  private int lineCount = 3;
  private boolean debug = false;

  public MultilineLabelDemoScreen(Screen parent) {
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
        new CyclingButtonWidget.Builder<OverflowBehavior>((value) -> value.getDisplayText(TestMod.MOD_ID)).values(
                OverflowBehavior.values())
            .initially(OverflowBehavior.SHOW)
            .omitKeyText()
            .build(0, 0, 100, 20, Text.empty(), this::onOverflowBehaviorChange));
    this.minusButton = firstRow.add(IconButtonWidget.builder(BuiltinIcon.MINUS_18, TestMod.MOD_ID)
        .onPress((button) -> this.onLineCountChange(this.lineCount - 1))
        .build());
    this.plusButton = firstRow.add(IconButtonWidget.builder(BuiltinIcon.PLUS_18, TestMod.MOD_ID)
        .onPress((button) -> this.onLineCountChange(this.lineCount + 1))
        .build());
    this.layout.addHeader(firstRow);

    LinearLayoutWidget secondRow = LinearLayoutWidget.horizontal()
        .spacing(GuiUtil.PADDING)
        .defaultOffAxisContentAlignCenter();
    secondRow.add(
        new CyclingButtonWidget.Builder<Alignment>((value) -> Text.of("X: " + value.name())).values(Alignment.values())
            .initially(Alignment.CENTER)
            .omitKeyText()
            .build(0, 0, 100, 20, Text.empty(), this::onAlignmentXChange));
    secondRow.add(
        new CyclingButtonWidget.Builder<Alignment>((value) -> Text.of("Y: " + value.name())).values(Alignment.values())
            .initially(Alignment.CENTER)
            .omitKeyText()
            .build(0, 0, 100, 20, Text.empty(), this::onAlignmentYChange));
    this.layout.addHeader(secondRow);

    this.layout.setHeaderHeight(this.layout.getHeader().getContentHeight() + 2 * GuiUtil.PADDING);

    this.label = LabelWidget.builder(this.textRenderer, this.generateLines())
        .width(60)
        .alignCenterX()
        .alignCenterY()
        .lineSpacing(1)
        .build();
    this.layout.addBody(new WrapperLayoutWidget<>(this.label, (parent, self) -> {
      self.setPosition(parent.getX(), parent.getY());
    }));

    this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.close()).build());

    this.layout.forEachChild(this::addDrawableChild);
    this.initTabNavigation();
  }

  @Override
  protected void initTabNavigation() {
    this.layout.refreshPositions();
  }

  @Override
  protected void clearChildren() {
    super.clearChildren();
    this.layout.clearChildren();
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == GLFW.GLFW_KEY_D && hasControlDown()) {
      this.debug = !this.debug;
      this.label.setBgColor(this.debug ? GuiUtil.TRANSPARENT_COLOR : GuiUtil.BACKGROUND_COLOR);
      GuiUtil.playClickSound();
      return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    super.renderBackground(context, mouseX, mouseY, delta);

    if (this.debug) {
      IntRect bounds = this.label.getBounds();
      context.fill(bounds.left(), bounds.top(), bounds.right(), bounds.bottom(), GuiUtil.genColorInt(0, 0.4f, 0.9f));
    }
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

  private void onAlignmentXChange(CyclingButtonWidget<Alignment> button, Alignment value) {
    this.label.setAlignmentX(value);
    this.layout.refreshPositions();
  }

  private void onAlignmentYChange(CyclingButtonWidget<Alignment> button, Alignment value) {
    this.label.setAlignmentY(value);
    this.layout.refreshPositions();
  }

  private void onLineCountChange(int lineCount) {
    this.lineCount = lineCount;
    this.label.batchUpdates(() -> {
      this.label.setText(this.generateLines());
      this.label.setHeight(this.label.getDefaultHeight());
    });
    this.minusButton.active = lineCount > 1;
    this.plusButton.active = lineCount < 5;
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
}
