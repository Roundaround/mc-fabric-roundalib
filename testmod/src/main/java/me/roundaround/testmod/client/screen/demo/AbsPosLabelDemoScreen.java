package me.roundaround.testmod.client.screen.demo;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.Coords;
import me.roundaround.roundalib.client.gui.layout.IntRect;
import me.roundaround.roundalib.client.gui.layout.Spacing;
import me.roundaround.roundalib.client.gui.layout.TextAlignment;
import me.roundaround.roundalib.client.gui.widget.FullBodyWrapperWidget;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import me.roundaround.roundalib.client.gui.widget.LinearLayoutWidget;
import me.roundaround.testmod.TestMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
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
  private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
  private final ArrayList<LabelWidget> labels = new ArrayList<>();

  private boolean debug = false;

  public AbsPosLabelDemoScreen(Screen parent) {
    super(TITLE_TEXT);
    this.parent = parent;
  }

  @Override
  protected void init() {
    LinearLayoutWidget header = LinearLayoutWidget.vertical(
        (self) -> Coords.of(this.width, this.layout.getHeaderHeight())).spacing(GuiUtil.PADDING / 2).centered();
    header.getMainPositioner().alignHorizontalCenter();
    this.layout.addHeader(header);

    header.add(new TextWidget(this.getTitle(), this.textRenderer).alignCenter());
    header.add(
        new CyclingButtonWidget.Builder<OverflowBehavior>((value) -> value.getDisplayText(TestMod.MOD_ID)).values(
                OverflowBehavior.values())
            .initially(OverflowBehavior.SHOW)
            .omitKeyText()
            .build(Text.empty(), this::onOverflowBehaviorChange));

    header.refreshPositions();
    this.layout.setHeaderHeight(header.getContentSize() + 2 * GuiUtil.PADDING);

    LinearLayoutWidget body = LinearLayoutWidget.horizontal().spacing(40);
    this.layout.addBody(new FullBodyWrapperWidget(body, this.layout).margin(Spacing.of(GuiUtil.PADDING)));

    int index = 0;
    for (TextAlignment alignmentX : TextAlignment.values()) {
      int colIndex = index++;
      LinearLayoutWidget column = body.add(LinearLayoutWidget.vertical().spacing(20),
          (parent, self) -> self.setDimensions(columnWidth(parent, colIndex), parent.getHeight())
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


  private void addLabel(LinearLayoutWidget column, TextAlignment alignmentX, TextAlignment alignmentY) {
    LabelWidget label = LabelWidget.builder(
            this.textRenderer, Text.of(String.format("== == %s/%s == ==", nameX(alignmentX), nameY(alignmentY))))
        .justifiedHorizontally(alignmentX)
        .alignedVertically(alignmentY)
        .overflowBehavior(OverflowBehavior.SHOW)
        .maxLines(3)
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

  private void onOverflowBehaviorChange(CyclingButtonWidget<OverflowBehavior> button, OverflowBehavior value) {
    this.labels.forEach((label) -> label.setOverflowBehavior(value));
    this.initTabNavigation();
  }

  private static int columnWidth(LinearLayoutWidget parent, int index) {
    Divider divider = new Divider(parent.getWidth() - 2 * parent.getSpacing(), 3);
    divider.skip(index);
    return divider.nextInt();
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
