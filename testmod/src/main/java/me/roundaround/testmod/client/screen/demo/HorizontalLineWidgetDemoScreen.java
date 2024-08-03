package me.roundaround.testmod.client.screen.demo;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.FillerWidget;
import me.roundaround.roundalib.client.gui.layout.screen.ThreeSectionLayoutWidget;
import me.roundaround.roundalib.client.gui.widget.drawable.HorizontalLineWidget;
import me.roundaround.roundalib.client.gui.widget.drawable.LabelWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class HorizontalLineWidgetDemoScreen extends Screen implements DemoScreen {
  private static final Text TITLE_TEXT = Text.translatable("testmod.horizontallinewidgetdemoscreen.title");

  private final Screen parent;
  private final ThreeSectionLayoutWidget layout = new ThreeSectionLayoutWidget(this);

  public HorizontalLineWidgetDemoScreen(Screen parent) {
    super(TITLE_TEXT);
    this.parent = parent;
  }

  @Override
  protected void init() {
    this.layout.addHeader(this.textRenderer, this.getTitle());

    this.layout.addBody(LabelWidget.builder(this.textRenderer, Text.of("Top, 16px")).build());
    this.layout.addBody(HorizontalLineWidget.ofWidth(18));

    this.layout.addBody(FillerWidget.ofHeight(2 * GuiUtil.PADDING));

    this.layout.addBody(LabelWidget.builder(this.textRenderer, Text.of("Top, 80px")).build());
    this.layout.addBody(HorizontalLineWidget.ofWidth(80));

    this.layout.addBody(FillerWidget.ofHeight(2 * GuiUtil.PADDING));

    this.layout.addBody(LabelWidget.builder(this.textRenderer, Text.of("Bottom, 16px")).build());
    this.layout.addBody(HorizontalLineWidget.ofWidthBottom(18));

    this.layout.addBody(FillerWidget.ofHeight(2 * GuiUtil.PADDING));

    this.layout.addBody(LabelWidget.builder(this.textRenderer, Text.of("Bottom, 80px")).build());
    this.layout.addBody(HorizontalLineWidget.ofWidthBottom(80));

    this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.close()).build());

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
}
