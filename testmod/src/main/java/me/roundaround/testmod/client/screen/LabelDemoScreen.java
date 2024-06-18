package me.roundaround.testmod.client.screen;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.IntRect;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import me.roundaround.testmod.TestMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static me.roundaround.roundalib.client.gui.widget.LabelWidget.OverflowBehavior;

@Environment(EnvType.CLIENT)
public class LabelDemoScreen extends Screen implements DemoScreen {
  private static final Text TITLE_TEXT = Text.translatable("testmod.labeldemoscreen.title");

  private final Screen parent;
  private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
  private final List<LabelRenderer> labelRenderers = new ArrayList<>();

  private OverflowBehavior overflowBehavior = OverflowBehavior.SHOW;

  public LabelDemoScreen(Screen parent) {
    super(TITLE_TEXT);
    this.parent = parent;
  }

  @Override
  protected void init() {
    DirectionalLayoutWidget header = DirectionalLayoutWidget.vertical().spacing(GuiUtil.PADDING / 2);
    this.layout.addHeader(header, (positioner) -> positioner.marginY(GuiUtil.PADDING));

    header.getMainPositioner().alignHorizontalCenter();
    header.add(new EmptyWidget(0, GuiUtil.PADDING / 2));
    header.add(new TextWidget(this.getTitle(), this.textRenderer).alignCenter());
    header.add(
        new CyclingButtonWidget.Builder<OverflowBehavior>((value) -> value.getDisplayText(TestMod.MOD_ID)).values(
                OverflowBehavior.values())
            .initially(this.overflowBehavior)
            .omitKeyText()
            .build(Text.empty(), this::onOverflowBehaviorChange));
    header.add(new EmptyWidget(0, GuiUtil.PADDING / 2));

    header.refreshPositions();
    this.layout.setHeaderHeight(header.getHeight());

    this.labelRenderers.add(this.addDrawable(new LabelRenderer(
        LabelWidget.builder(this.textRenderer, Text.of("== == Left/top == =="), 0, 0)
            .justifiedLeft()
            .alignedTop()
            .maxWidth(50)
            .overflowBehavior(this.overflowBehavior)
            .maxLines(3)
            .build(), (label) -> label.setPosition(this.relativeX(0), this.relativeY(0)))));

    this.labelRenderers.add(this.addDrawable(new LabelRenderer(
        LabelWidget.builder(this.textRenderer, Text.of("== == Center/top == =="), 0, 0)
            .justifiedCenter()
            .alignedTop()
            .maxWidth(50)
            .overflowBehavior(this.overflowBehavior)
            .maxLines(3)
            .build(), (label) -> label.setPosition(this.relativeX(0.5f), this.relativeY(0)))));

    this.labelRenderers.add(this.addDrawable(new LabelRenderer(
        LabelWidget.builder(this.textRenderer, Text.of("== == Right/top == =="), 0, 0)
            .justifiedRight()
            .alignedTop()
            .maxWidth(50)
            .overflowBehavior(this.overflowBehavior)
            .maxLines(3)
            .build(), (label) -> label.setPosition(this.relativeX(1), this.relativeY(0)))));

    this.labelRenderers.add(this.addDrawable(new LabelRenderer(
        LabelWidget.builder(this.textRenderer, Text.of("== == Left/middle == =="), 0, 0)
            .justifiedLeft()
            .alignedMiddle()
            .maxWidth(50)
            .overflowBehavior(this.overflowBehavior)
            .maxLines(3)
            .build(), (label) -> label.setPosition(this.relativeX(0), this.relativeY(0.5f)))));

    this.labelRenderers.add(this.addDrawable(new LabelRenderer(
        LabelWidget.builder(this.textRenderer, Text.of("== == Center/middle == =="), 0, 0)
            .justifiedCenter()
            .alignedMiddle()
            .maxWidth(50)
            .overflowBehavior(this.overflowBehavior)
            .maxLines(3)
            .build(), (label) -> label.setPosition(this.relativeX(0.5f), this.relativeY(0.5f)))));

    this.labelRenderers.add(this.addDrawable(new LabelRenderer(
        LabelWidget.builder(this.textRenderer, Text.of("== == Right/middle == =="), 0, 0)
            .justifiedRight()
            .alignedMiddle()
            .maxWidth(50)
            .overflowBehavior(this.overflowBehavior)
            .maxLines(3)
            .build(), (label) -> label.setPosition(this.relativeX(1), this.relativeY(0.5f)))));

    this.labelRenderers.add(this.addDrawable(new LabelRenderer(
        LabelWidget.builder(this.textRenderer, Text.of("== == Left/bottom == =="), 0, 0)
            .justifiedLeft()
            .alignedBottom()
            .maxWidth(50)
            .overflowBehavior(this.overflowBehavior)
            .maxLines(3)
            .build(), (label) -> label.setPosition(this.relativeX(0), this.relativeY(1)))));

    this.labelRenderers.add(this.addDrawable(new LabelRenderer(
        LabelWidget.builder(this.textRenderer, Text.of("== == Center/bottom == =="), 0, 0)
            .justifiedCenter()
            .alignedBottom()
            .maxWidth(50)
            .overflowBehavior(this.overflowBehavior)
            .maxLines(3)
            .build(), (label) -> label.setPosition(this.relativeX(0.5f), this.relativeY(1)))));

    this.labelRenderers.add(this.addDrawable(new LabelRenderer(
        LabelWidget.builder(this.textRenderer, Text.of("== == Right/bottom == =="), 0, 0)
            .justifiedRight()
            .alignedBottom()
            .maxWidth(50)
            .overflowBehavior(this.overflowBehavior)
            .maxLines(3)
            .build(), (label) -> label.setPosition(this.relativeX(1), this.relativeY(1)))));

    this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.close()).build());

    this.layout.forEachChild(this::addDrawableChild);
    this.initTabNavigation();
  }

  @Override
  protected void initTabNavigation() {
    this.layout.refreshPositions();

    this.labelRenderers.forEach(LabelRenderer::reflow);
  }

  @Override
  public void close() {
    Objects.requireNonNull(this.client).setScreen(this.parent);
  }

  private int relativeX(float scale) {
    int paddedContentWidth = this.width - 2 * GuiUtil.PADDING;
    ;
    return GuiUtil.PADDING + (int) (paddedContentWidth * scale);
  }

  private int relativeY(float scale) {
    int paddedContentHeight = this.layout.getContentHeight() - 2 * GuiUtil.PADDING;
    return this.layout.getHeaderHeight() + GuiUtil.PADDING + (int) (paddedContentHeight * scale);
  }

  private void onOverflowBehaviorChange(CyclingButtonWidget<OverflowBehavior> button, OverflowBehavior value) {
    this.overflowBehavior = value;

    this.labelRenderers.forEach(renderer -> renderer.setLabel(renderer.getLabel()
        .toBuilder()
        .maxWidth(this.overflowBehavior == OverflowBehavior.SHOW ? 0 : 60)
        .overflowBehavior(this.overflowBehavior)
        .build()));

    this.initTabNavigation();
  }

  private static class LabelRenderer implements Drawable {
    private final Consumer<LabelWidget> onReflow;
    private LabelWidget label;

    LabelRenderer(LabelWidget label, Consumer<LabelWidget> onReflow) {
      this.label = label;
      this.onReflow = onReflow;
    }

    public LabelWidget getLabel() {
      return this.label;
    }

    public void setLabel(LabelWidget label) {
      this.label = label;
    }

    public void reflow() {
      this.onReflow.accept(this.getLabel());
      this.getLabel().refreshPositions();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      IntRect textBounds = this.getLabel().getTextBounds();
      context.fill(textBounds.left(), textBounds.top(), textBounds.right(), textBounds.bottom(),
          GuiUtil.genColorInt(0, 0.4f, 0.9f)
      );

      this.label.render(context, mouseX, mouseY, delta);
    }
  }
}
