package me.roundaround.testmod.client.screen.demo;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.IntRect;
import me.roundaround.roundalib.client.gui.layout.TextAlignment;
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
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.Divider;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Objects;

import static me.roundaround.roundalib.client.gui.widget.LabelWidget.OverflowBehavior;

@Environment(EnvType.CLIENT)
public class AbsPosLabelDemoScreen extends Screen implements DemoScreen {
  private static final Text TITLE_TEXT = Text.translatable("testmod.refposlabeldemoscreen.title");

  private final Screen parent;
  private final ThreeSectionLayoutWidget layout = new ThreeSectionLayoutWidget(this);
  private final ArrayList<LabelWidget> labels = new ArrayList<>();

  private boolean debug = false;

  public AbsPosLabelDemoScreen(Screen parent) {
    super(TITLE_TEXT);
    this.parent = parent;
  }

  @Override
  protected void init() {
    this.layout.addHeader(new TextWidget(this.getTitle(), this.textRenderer).alignCenter());
    this.layout.addHeader(
        new CyclingButtonWidget.Builder<OverflowBehavior>((value) -> value.getDisplayText(TestMod.MOD_ID)).values(
                OverflowBehavior.values())
            .initially(OverflowBehavior.SHOW)
            .omitKeyText()
            .build(Text.empty(), this::onOverflowBehaviorChange));
    this.layout.setHeaderHeight(this.layout.getHeader().getPaddedContentHeight());

    this.layout.getBody().flowAxis(LinearLayoutWidget.FlowAxis.HORIZONTAL);
    this.layout.setBodyLayoutHook((parent, self) -> {
      int totalWidth = parent.getWidth() - 2 * GuiUtil.PADDING;
      int contentWidth = 3 * columnWidth(totalWidth) + 2 * columnSpacing(totalWidth);
      self.setX((parent.getWidth() - contentWidth) / 2);
      self.setWidth(contentWidth);
      self.spacing(columnSpacing(totalWidth));
    });

    for (TextAlignment alignmentX : TextAlignment.values()) {
      LinearLayoutWidget column = this.layout.addBody(LinearLayoutWidget.vertical().spacing(20),
          (parent, self) -> self.setDimensions(columnWidth(parent.getWidth()), parent.getHeight())
      );

      for (TextAlignment alignmentY : TextAlignment.values()) {
        this.addLabel(column, alignmentX, alignmentY);
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
      this.highlightSection(context, this.layout.getHeader(), GuiUtil.genColorInt(1f, 0f, 0f));
      this.highlightSection(context, this.layout.getBody(), GuiUtil.genColorInt(0f, 1f, 0f));
      this.highlightSection(context, this.layout.getFooter(), GuiUtil.genColorInt(0f, 0f, 1f));

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

  private void highlightSection(DrawContext context, LinearLayoutWidget section, int color) {
    context.fill(
        section.getX(), section.getY(), section.getX() + section.getWidth(), section.getY() + section.getHeight(),
        color
    );
  }

  private void addLabel(
      LinearLayoutWidget column, TextAlignment alignmentX, TextAlignment alignmentY
  ) {
    LabelWidget label = LabelWidget.builder(this.textRenderer,
            Text.of(String.format("== == %s/%s == ==", nameX(alignmentX), nameY(alignmentY)))
        )
        .justifiedHorizontally(alignmentX)
        .alignedVertically(alignmentY)
        .overflowBehavior(OverflowBehavior.SHOW)
        .maxLines(3)
        .tooltip(Text.of("WOW"))
        .build();

    this.labels.add(label);

    int index = column.getChildren().size();
    column.add(label, (parent, self) -> self.setDimensions(parent.getWidth(), rowHeight(parent, index)));
  }

  @Override
  protected void initTabNavigation() {
    this.layout.refreshPositions();
  }

  @Override
  public void close() {
    Objects.requireNonNull(this.client).setScreen(this.parent);
  }

  private void onOverflowBehaviorChange(
      CyclingButtonWidget<OverflowBehavior> button, OverflowBehavior value
  ) {
    this.labels.forEach((label) -> label.setOverflowBehavior(value));
    this.layout.refreshPositions();
  }

  private static int columnWidth(int parentWidth) {
    return Math.min((parentWidth - 4 * GuiUtil.PADDING) / 3, 70);
  }

  private static int columnSpacing(int parentWidth) {
    int totalSpacing = parentWidth - 3 * columnWidth(parentWidth);
    return totalSpacing / 2;
  }

  private static int rowHeight(LinearLayoutWidget parent, int index) {
    Divider divider = new Divider(parent.getHeight() - 2 * parent.getSpacing(), 3);
    divider.skip(index);
    return divider.nextInt();
  }

  private static String nameX(TextAlignment alignmentX) {
    return switch (alignmentX) {
      case START -> "Left";
      case CENTER -> "Center";
      case END -> "Right";
    };
  }

  private static String nameY(TextAlignment alignmentY) {
    return switch (alignmentY) {
      case START -> "top";
      case CENTER -> "middle";
      case END -> "bottom";
    };
  }
}
