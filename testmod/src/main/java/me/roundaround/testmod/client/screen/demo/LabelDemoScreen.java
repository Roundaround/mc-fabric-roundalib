package me.roundaround.testmod.client.screen.demo;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.IntRect;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import me.roundaround.roundalib.client.gui.widget.LayoutHook;
import me.roundaround.roundalib.client.gui.widget.LayoutHookWidget;
import me.roundaround.roundalib.client.gui.widget.NoopContainerLayoutWidget;
import me.roundaround.testmod.TestMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.Objects;

import static me.roundaround.roundalib.client.gui.widget.LabelWidget.OverflowBehavior;

@Environment(EnvType.CLIENT)
public class LabelDemoScreen extends Screen implements DemoScreen {
  private static final Text TITLE_TEXT = Text.translatable("testmod.labeldemoscreen.title");

  private final Screen parent;

  private ThreePartsLayoutWidget layout;
  private NoopContainerLayoutWidget labelsContainer;
  private OverflowBehavior overflowBehavior = OverflowBehavior.SHOW;

  public LabelDemoScreen(Screen parent) {
    super(TITLE_TEXT);
    this.parent = parent;
  }

  @Override
  protected void init() {
    this.layout = new ThreePartsLayoutWidget(this);

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

    this.labelsContainer = this.layout.addBody(NoopContainerLayoutWidget.create());

    this.addLabel(LabelWidget.builder(this.textRenderer, Text.of("== == Left/top == =="))
        .positionMode(LabelWidget.PositionMode.REFERENCE)
        .justifiedLeft()
        .alignedTop()
        .overflowBehavior(this.overflowBehavior)
        .maxLines(3)
        .build(), 0, 0);

    this.addLabel(LabelWidget.builder(this.textRenderer, Text.of("== == Center/top == =="))
        .positionMode(LabelWidget.PositionMode.REFERENCE)
        .justifiedCenter()
        .alignedTop()
        .overflowBehavior(this.overflowBehavior)
        .maxLines(3)
        .build(), 0.5f, 0);

    this.addLabel(LabelWidget.builder(this.textRenderer, Text.of("== == Right/top == =="))
        .positionMode(LabelWidget.PositionMode.REFERENCE)
        .justifiedRight()
        .alignedTop()
        .overflowBehavior(this.overflowBehavior)
        .maxLines(3)
        .build(), 1, 0);

    this.addLabel(LabelWidget.builder(this.textRenderer, Text.of("== == Left/middle == =="))
        .positionMode(LabelWidget.PositionMode.REFERENCE)
        .justifiedLeft()
        .alignedMiddle()
        .overflowBehavior(this.overflowBehavior)
        .maxLines(3)
        .build(), 0, 0.5f);

    this.addLabel(LabelWidget.builder(this.textRenderer, Text.of("== == Center/middle == =="))
        .positionMode(LabelWidget.PositionMode.REFERENCE)
        .justifiedCenter()
        .alignedMiddle()
        .overflowBehavior(this.overflowBehavior)
        .maxLines(3)
        .build(), 0.5f, 0.5f);

    this.addLabel(LabelWidget.builder(this.textRenderer, Text.of("== == Right/middle == =="))
        .positionMode(LabelWidget.PositionMode.REFERENCE)
        .justifiedRight()
        .alignedMiddle()
        .overflowBehavior(this.overflowBehavior)
        .maxLines(3)
        .build(), 1, 0.5f);

    this.addLabel(LabelWidget.builder(this.textRenderer, Text.of("== == Left/bottom == =="))
        .positionMode(LabelWidget.PositionMode.REFERENCE)
        .justifiedLeft()
        .alignedBottom()
        .overflowBehavior(this.overflowBehavior)
        .maxLines(3)
        .build(), 0, 1);

    this.addLabel(LabelWidget.builder(this.textRenderer, Text.of("== == Center/bottom == =="))
        .positionMode(LabelWidget.PositionMode.REFERENCE)
        .justifiedCenter()
        .alignedBottom()
        .overflowBehavior(this.overflowBehavior)
        .maxLines(3)
        .build(), 0.5f, 1);

    this.addLabel(LabelWidget.builder(this.textRenderer, Text.of("== == Right/bottom == =="))
        .positionMode(LabelWidget.PositionMode.REFERENCE)
        .justifiedRight()
        .alignedBottom()
        .overflowBehavior(this.overflowBehavior)
        .maxLines(3)
        .build(), 1, 1);

    this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.close()).build());

    this.layout.forEachChild(this::addDrawableChild);
    this.initTabNavigation();
  }

  private void addLabel(LabelWidget label, float relativeX, float relativeY) {
    LayoutHook layoutHook = () -> {
      label.setPosition(this.relativeX(relativeX), this.relativeY(relativeY));
      label.setDimensions(50, (this.layout.getContentHeight() - 2 * GuiUtil.PADDING) / 3);
    };

    layoutHook.run();

    this.labelsContainer.add(LayoutHookWidget.from(label).withPreLayoutHook(layoutHook));

    this.addDrawable((context, mouseX, mouseY, delta) -> {
      context.fill(label.getX(), label.getY(), label.getRight(), label.getBottom(),
          GuiUtil.genColorInt(0.3f, 0, 0.1f, 0.5f)
      );

      IntRect textBounds = label.getTextBounds();
      context.fill(textBounds.left(), textBounds.top(), textBounds.right(), textBounds.bottom(),
          GuiUtil.genColorInt(0, 0.4f, 0.9f)
      );
    });
  }

  @Override
  protected void initTabNavigation() {
    this.layout.refreshPositions();
  }

  @Override
  public void close() {
    Objects.requireNonNull(this.client).setScreen(this.parent);
  }

  private int relativeX(float scale) {
    int paddedContentWidth = this.width - 2 * GuiUtil.PADDING;
    return GuiUtil.PADDING + (int) (paddedContentWidth * scale);
  }

  private int relativeY(float scale) {
    int paddedContentHeight = this.layout.getContentHeight() - 2 * GuiUtil.PADDING;
    return this.layout.getHeaderHeight() + GuiUtil.PADDING + (int) (paddedContentHeight * scale);
  }

  private void onOverflowBehaviorChange(CyclingButtonWidget<OverflowBehavior> button, OverflowBehavior value) {
    this.overflowBehavior = value;
    this.clearAndInit();
  }
}
