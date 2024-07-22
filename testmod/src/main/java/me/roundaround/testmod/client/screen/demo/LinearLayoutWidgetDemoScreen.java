package me.roundaround.testmod.client.screen.demo;

import me.roundaround.roundalib.asset.icon.BuiltinIcon;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.WrapperLayoutWidget;
import me.roundaround.roundalib.client.gui.layout.linear.LinearLayoutWidget;
import me.roundaround.roundalib.client.gui.layout.screen.ThreeSectionLayoutWidget;
import me.roundaround.roundalib.client.gui.util.Alignment;
import me.roundaround.roundalib.client.gui.util.Axis;
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

    LinearLayoutWidget firstRow = LinearLayoutWidget.horizontal()
        .spacing(GuiUtil.PADDING)
        .defaultOffAxisContentAlignCenter();
    firstRow.add(
        new CyclingButtonWidget.Builder<Axis>((value) -> Text.of("Axis: " + value.name())).values(Axis.values())
            .initially(Axis.HORIZONTAL)
            .omitKeyText()
            .build(0, 0, 100, 20, Text.empty(), this::onFlowAxisChange));
    firstRow.add(
        new CyclingButtonWidget.Builder<Alignment>((value) -> Text.of("X: " + value.name())).values(Alignment.values())
            .initially(Alignment.START)
            .omitKeyText()
            .build(0, 0, 100, 20, Text.empty(), this::onAlignmentXChange));
    firstRow.add(
        new CyclingButtonWidget.Builder<Alignment>((value) -> Text.of("Y: " + value.name())).values(Alignment.values())
            .initially(Alignment.START)
            .omitKeyText()
            .build(0, 0, 100, 20, Text.empty(), this::onAlignmentYChange));
    this.layout.addHeader(firstRow);

    LinearLayoutWidget secondRow = LinearLayoutWidget.horizontal()
        .spacing(GuiUtil.PADDING)
        .defaultOffAxisContentAlignCenter();
    secondRow.add(
        new CyclingButtonWidget.Builder<ContentAlignment>((value) -> Text.of("Content: " + value.name())).values(
                ContentAlignment.values())
            .initially(ContentAlignment.CENTER)
            .omitKeyText()
            .build(0, 0, 100, 20, Text.empty(), this::onContentAlignmentChange));
    this.layout.addHeader(secondRow);

    this.layout.setHeaderHeight(this.layout.getHeader().getContentHeight() + 2 * GuiUtil.PADDING);

    this.demoLayout = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING);
    this.demoLayout.add(IconButtonWidget.builder(BuiltinIcon.MINUS_13, TestMod.MOD_ID).medium().build());
    this.demoLayout.add(IconButtonWidget.builder(BuiltinIcon.CHECKMARK_18, TestMod.MOD_ID).vanillaSize().build());
    this.demoLayout.add(IconButtonWidget.builder(BuiltinIcon.PLUS_13, TestMod.MOD_ID).medium().build());
    this.demoLayout.add(LabelWidget.builder(this.textRenderer, Text.of("Label")).build());
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

  private void onContentAlignmentChange(CyclingButtonWidget<ContentAlignment> button, ContentAlignment value) {
    value.set(this.demoLayout);
    this.layout.refreshPositions();
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
