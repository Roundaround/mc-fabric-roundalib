package me.roundaround.testmod.client.screen.demo;

import me.roundaround.roundalib.asset.icon.BuiltinIcon;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.layout.LinearLayoutWidget;
import me.roundaround.roundalib.client.gui.widget.layout.WrapperLayoutWidget;
import me.roundaround.roundalib.client.gui.widget.layout.screen.ThreeSectionLayoutWidget;
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

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class LinearLayoutWidgetDemoScreen extends Screen implements DemoScreen {
  private static final Text TITLE_TEXT = Text.translatable("testmod.linearlayoutdemoscreen.title");

  private final Screen parent;
  private final ThreeSectionLayoutWidget layout = new ThreeSectionLayoutWidget(this);

  private LinearLayoutWidget demoLayout;
  private boolean debug = false;

  public LinearLayoutWidgetDemoScreen(Screen parent) {
    super(TITLE_TEXT);
    this.parent = parent;
  }

  @Override
  protected void init() {
    this.layout.addHeader(this.textRenderer, this.getTitle());

    LinearLayoutWidget buttonRow = LinearLayoutWidget.horizontal()
        .spacing(GuiUtil.PADDING)
        .defaultOffAxisContentAlignCenter();
    buttonRow.add(new CyclingButtonWidget.Builder<LinearLayoutWidget.FlowAxis>(
        (value) -> Text.of("Axis: " + value.name())).values(LinearLayoutWidget.FlowAxis.values())
        .initially(LinearLayoutWidget.FlowAxis.HORIZONTAL)
        .omitKeyText()
        .build(0, 0, 100, 20, Text.empty(), this::onFlowAxisChange));
    buttonRow.add(new CyclingButtonWidget.Builder<AlignmentX>((value) -> Text.of("X: " + value.name())).values(
            AlignmentX.values())
        .initially(AlignmentX.LEFT)
        .omitKeyText()
        .build(0, 0, 100, 20, Text.empty(), this::onAlignmentXChange));
    buttonRow.add(new CyclingButtonWidget.Builder<AlignmentY>((value) -> Text.of("Y: " + value.name())).values(
            AlignmentY.values())
        .initially(AlignmentY.TOP)
        .omitKeyText()
        .build(0, 0, 100, 20, Text.empty(), this::onAlignmentYChange));
    buttonRow.add(
        new CyclingButtonWidget.Builder<ContentAlignment>((value) -> Text.of("Content: " + value.name())).values(
                ContentAlignment.values())
            .initially(ContentAlignment.CENTER)
            .omitKeyText()
            .build(0, 0, 100, 20, Text.empty(), this::onContentAlignmentChange));
    this.layout.addHeader(buttonRow);

    this.layout.setHeaderHeight(this.layout.getHeader().getContentHeight() + 2 * GuiUtil.PADDING);

    this.demoLayout = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING);
    this.demoLayout.add(IconButtonWidget.builder(BuiltinIcon.MINUS_13, TestMod.MOD_ID).medium().build());
    this.demoLayout.add(IconButtonWidget.builder(BuiltinIcon.CHECKMARK_18, TestMod.MOD_ID).vanillaSize().build());
    this.demoLayout.add(IconButtonWidget.builder(BuiltinIcon.PLUS_13, TestMod.MOD_ID).medium().build());
    WrapperLayoutWidget<LinearLayoutWidget> wrapper = WrapperLayoutWidget.builder(this.demoLayout)
        .setDimensions(2, 2)
        .setLayoutHook((parent, self) -> {
          self.setPosition(parent.getX() + 1, parent.getY() + 1);
        })
        .build();
    this.layout.addBody(wrapper);

    this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.close()).build());

    this.addDrawable((context, mouseX, mouseY, delta) -> {
      LinearLayoutWidget layout = LinearLayoutWidgetDemoScreen.this.demoLayout;
      context.fill(layout.getX(), layout.getY(), layout.getX() + layout.getWidth(), layout.getY() + layout.getHeight(),
          GuiUtil.genColorInt(0, 0.4f, 0.9f, 0.2f)
      );
    });
    this.addDrawable((context, mouseX, mouseY, delta) -> {
      int color = GuiUtil.genColorInt(1f, 1f, 1f, 0.7f);
      int left = wrapper.getX();
      int right = wrapper.getX() + wrapper.getWidth();
      int top = wrapper.getY();
      int bottom = wrapper.getY() + wrapper.getHeight();

      // Left
      context.fill(left - 5 * GuiUtil.PADDING, top, left - GuiUtil.PADDING, bottom, color);
      // Right
      context.fill(right + GuiUtil.PADDING, top, right + 5 * GuiUtil.PADDING, bottom, color);
      // Top
      context.fill(left, top - 5 * GuiUtil.PADDING, right, top - GuiUtil.PADDING, color);
      // Bottom
      context.fill(left, bottom + GuiUtil.PADDING, right, bottom + 5 * GuiUtil.PADDING, color);
      // Center
      context.fill(left, top, right, bottom, color);
    });
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
    }
  }

  private void highlightSection(DrawContext context, LinearLayoutWidget section, int color) {
    context.fill(section.getX(), section.getY(), section.getX() + section.getWidth(),
        section.getY() + section.getHeight(), color
    );
  }

  @Override
  public void close() {
    Objects.requireNonNull(this.client).setScreen(this.parent);
  }

  private void onFlowAxisChange(
      CyclingButtonWidget<LinearLayoutWidget.FlowAxis> button, LinearLayoutWidget.FlowAxis value
  ) {
    this.demoLayout.flowAxis(value);
    this.layout.refreshPositions();
  }

  private void onAlignmentXChange(CyclingButtonWidget<AlignmentX> button, AlignmentX value) {
    value.set(this.demoLayout);
    this.layout.refreshPositions();
  }

  private void onAlignmentYChange(CyclingButtonWidget<AlignmentY> button, AlignmentY value) {
    value.set(this.demoLayout);
    this.layout.refreshPositions();
  }

  private void onContentAlignmentChange(CyclingButtonWidget<ContentAlignment> button, ContentAlignment value) {
    value.set(this.demoLayout);
    this.layout.refreshPositions();
  }

  private enum AlignmentX {
    LEFT, CENTER, RIGHT;

    public void set(LinearLayoutWidget layout) {
      switch (this) {
        case LEFT -> layout.alignSelfLeft();
        case CENTER -> layout.alignSelfCenterX();
        case RIGHT -> layout.alignSelfRight();
      }
    }
  }

  private enum AlignmentY {
    TOP, CENTER, BOTTOM;

    public void set(LinearLayoutWidget layout) {
      switch (this) {
        case TOP -> layout.alignSelfTop();
        case CENTER -> layout.alignSelfCenterY();
        case BOTTOM -> layout.alignSelfBottom();
      }
    }
  }

  private enum ContentAlignment {
    START, CENTER, END;

    public void set(LinearLayoutWidget layout) {
      switch (this) {
        case START -> layout.defaultOffAxisContentAlignStart();
        case CENTER -> layout.defaultOffAxisContentAlignCenter();
        case END -> layout.defaultOffAxisContentAlignEnd();
      }
    }
  }
}
