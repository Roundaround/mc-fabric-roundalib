package me.roundaround.testmod.client.screen.demo;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.util.Alignment;
import me.roundaround.roundalib.client.gui.util.IntRect;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import me.roundaround.roundalib.client.gui.layout.LayoutCollectionWidget;
import me.roundaround.roundalib.client.gui.layout.screen.ThreeSectionLayoutWidget;
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
public class RefPosLabelDemoScreen extends Screen implements DemoScreen {
  private static final Text TITLE_TEXT = Text.translatable("testmod.refposlabeldemoscreen.title");

  private final Screen parent;
  private final ThreeSectionLayoutWidget layout = new ThreeSectionLayoutWidget(this);
  private final ArrayList<LabelWidget> labels = new ArrayList<>();

  private boolean debug = false;

  public RefPosLabelDemoScreen(Screen parent) {
    super(TITLE_TEXT);
    this.parent = parent;
  }

  @Override
  protected void init() {
    this.layout.addHeader(this.textRenderer, this.getTitle());
    this.layout.addHeader(
        new CyclingButtonWidget.Builder<OverflowBehavior>((value) -> value.getDisplayText(TestMod.MOD_ID)).values(
                OverflowBehavior.values())
            .initially(OverflowBehavior.SHOW)
            .omitKeyText()
            .build(Text.empty(), this::onOverflowBehaviorChange));
    this.layout.setHeaderHeight(this.layout.getHeader().getContentHeight() + 2 * GuiUtil.PADDING);

    LayoutCollectionWidget labelsContainer = this.layout.addBody(LayoutCollectionWidget.create());
    for (Alignment alignmentX : Alignment.values()) {
      for (Alignment alignmentY : Alignment.values()) {
        this.addLabel(labelsContainer, alignmentX, alignmentY);
      }
    }

    this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.close()).build());

    this.layout.forEachChild(this::addDrawableChild);
    this.initTabNavigation();
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (keyCode == GLFW.GLFW_KEY_D && hasControlDown()) {
      this.debug = !this.debug;
      this.labels.forEach((label) -> label.setShowBackground(!this.debug));
      GuiUtil.playClickSound();
      return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    super.renderBackground(context, mouseX, mouseY, delta);

    if (this.debug) {
      for (LabelWidget label : this.labels) {
        context.fill(label.getX(), label.getY(), label.getRight(), label.getBottom(),
            GuiUtil.genColorInt(0.3f, 0, 0.1f, 0.5f)
        );
        context.drawBorder(label.getX(), label.getY(), label.getWidth(), label.getHeight(),
            GuiUtil.genColorInt(1f, 1f, 1f, 0.3f)
        );

        IntRect textBounds = label.getTextBounds();
        context.fill(textBounds.left(), textBounds.top(), textBounds.right(), textBounds.bottom(),
            GuiUtil.genColorInt(0, 0.4f, 0.9f)
        );
      }
    }
  }


  private void addLabel(
      LayoutCollectionWidget labelsContainer, Alignment alignmentX, Alignment alignmentY
  ) {
    float relativeX = relative(alignmentX);
    float relativeY = relative(alignmentY);

    LabelWidget label = LabelWidget.builder(this.textRenderer,
            Text.of(String.format("== == %s/%s == ==", nameX(alignmentX), nameY(alignmentY)))
        )
        .alignX(alignmentX)
        .alignY(alignmentY)
        .positionMode(LabelWidget.PositionMode.REFERENCE)
        .overflowBehavior(OverflowBehavior.SHOW)
        .maxLines(3)
        .build();

    this.labels.add(label);

    labelsContainer.add(label, (parent, self) -> {
      label.batchUpdates(() -> {
        self.setPosition(this.relativeX(relativeX), this.relativeY(relativeY));
        self.setDimensions(this.columnWidth(), this.rowHeight());
      });
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
    int paddedContentHeight = this.layout.getBodyHeight() - 2 * GuiUtil.PADDING;
    return this.layout.getHeaderHeight() + GuiUtil.PADDING + (int) (paddedContentHeight * scale);
  }

  private int columnWidth() {
    return Math.min((this.width - 4 * GuiUtil.PADDING) / 3, 70);
  }

  private int rowHeight() {
    return (this.layout.getBodyHeight() - 4 * GuiUtil.PADDING) / 3;
  }

  private void onOverflowBehaviorChange(
      CyclingButtonWidget<OverflowBehavior> button, OverflowBehavior value
  ) {
    this.labels.forEach((label) -> label.setOverflowBehavior(value));
    this.initTabNavigation();
  }

  private static float relative(Alignment alignment) {
    return switch (alignment) {
      case START -> 0f;
      case CENTER -> 0.5f;
      case END -> 1f;
    };
  }

  private static String nameX(Alignment alignmentX) {
    return switch (alignmentX) {
      case START -> "Left";
      case CENTER -> "Center";
      case END -> "Right";
    };
  }

  private static String nameY(Alignment alignmentY) {
    return switch (alignmentY) {
      case START -> "top";
      case CENTER -> "middle";
      case END -> "bottom";
    };
  }
}
