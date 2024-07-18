package me.roundaround.testmod.client.screen.demo;

import me.roundaround.roundalib.asset.icon.BuiltinIcon;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.IntRect;
import me.roundaround.roundalib.client.gui.layout.TextAlignment;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import me.roundaround.roundalib.client.gui.widget.layout.LinearLayoutWidget;
import me.roundaround.roundalib.client.gui.widget.layout.screen.ThreeSectionLayoutWidget;
import me.roundaround.testmod.TestMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
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

    LinearLayoutWidget buttonRow = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING).defaultOffAxisContentAlignCenter();
    buttonRow.add(
        new CyclingButtonWidget.Builder<OverflowBehavior>((value) -> value.getDisplayText(TestMod.MOD_ID)).values(
                OverflowBehavior.values())
            .initially(OverflowBehavior.SHOW)
            .omitKeyText()
            .build(0, 0, 100, 20, Text.empty(), this::onOverflowBehaviorChange));
    buttonRow.add(new CyclingButtonWidget.Builder<TextAlignment>((value) -> Text.of("X: " + value.name())).values(
            TextAlignment.values())
        .initially(TextAlignment.CENTER)
        .omitKeyText()
        .build(0, 0, 100, 20, Text.empty(), this::onAlignmentXChange));
    buttonRow.add(new CyclingButtonWidget.Builder<TextAlignment>((value) -> Text.of("Y: " + value.name())).values(
            TextAlignment.values())
        .initially(TextAlignment.CENTER)
        .omitKeyText()
        .build(0, 0, 100, 20, Text.empty(), this::onAlignmentYChange));
    this.minusButton = buttonRow.add(IconButtonWidget.builder(BuiltinIcon.MINUS_18, TestMod.MOD_ID)
        .onPress((button) -> this.onLineCountChange(this.lineCount - 1))
        .build());
    this.plusButton = buttonRow.add(IconButtonWidget.builder(BuiltinIcon.PLUS_18, TestMod.MOD_ID)
        .onPress((button) -> this.onLineCountChange(this.lineCount + 1))
        .build());
    this.layout.addHeader(buttonRow);

    this.layout.setHeaderHeight(this.layout.getHeader().getContentHeight() + 2 * GuiUtil.PADDING);

    SimplePositioningWidget anchor = new SimplePositioningWidget();
    this.label = anchor.add(LabelWidget.builder(this.textRenderer, this.generateLines())
        .width(60)
        .justifiedCenter()
        .alignedMiddle()
        .lineSpacing(1)
        .build());
    this.layout.addBody(anchor);

    this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.close()).build());

    this.layout.forEachChild(this::addDrawableChild);
    this.initTabNavigation();
  }

  @Override
  protected void initTabNavigation() {
    this.layout.refreshPositions();
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == GLFW.GLFW_KEY_D && hasControlDown()) {
      this.debug = !this.debug;
      this.label.setShowBackground(!this.debug);
      GuiUtil.playClickSound();
      return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    super.renderBackground(context, mouseX, mouseY, delta);

    if (this.debug) {
      context.fill(this.label.getX(), this.label.getY(), this.label.getRight(), this.label.getBottom(),
          GuiUtil.genColorInt(0.3f, 0, 0.1f, 0.5f)
      );
      context.drawBorder(this.label.getX(), this.label.getY(), this.label.getWidth(), this.label.getHeight(),
          GuiUtil.genColorInt(1f, 1f, 1f, 0.3f)
      );

      IntRect textBounds = this.label.getTextBounds();
      context.fill(textBounds.left(), textBounds.top(), textBounds.right(), textBounds.bottom(),
          GuiUtil.genColorInt(0, 0.4f, 0.9f)
      );
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

  private void onAlignmentXChange(CyclingButtonWidget<TextAlignment> button, TextAlignment value) {
    this.label.setAlignmentX(value);
    this.layout.refreshPositions();
  }

  private void onAlignmentYChange(CyclingButtonWidget<TextAlignment> button, TextAlignment value) {
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
