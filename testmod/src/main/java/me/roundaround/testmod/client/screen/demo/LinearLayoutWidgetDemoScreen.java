package me.roundaround.testmod.client.screen.demo;

import me.roundaround.roundalib.asset.icon.BuiltinIcon;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.DrawableWidget;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.layout.LinearLayoutWidget;
import me.roundaround.roundalib.client.gui.widget.layout.screen.ThreeSectionLayoutWidget;
import me.roundaround.testmod.TestMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class LinearLayoutWidgetDemoScreen extends Screen implements DemoScreen {
  private static final Text TITLE_TEXT = Text.translatable("testmod.linearlayoutdemoscreen.title");

  private final Screen parent;
  private final ThreeSectionLayoutWidget layout = new ThreeSectionLayoutWidget(this);

  private boolean debug = false;

  public LinearLayoutWidgetDemoScreen(Screen parent) {
    super(TITLE_TEXT);
    this.parent = parent;
  }

  @Override
  protected void init() {
    this.layout.addHeader(this.textRenderer, this.getTitle());

    LinearLayoutWidget linearLayoutWidget = LinearLayoutWidget.horizontal()
        .alignCenterX()
        .alignCenterY()
        .spacing(GuiUtil.PADDING);
    linearLayoutWidget.add(IconButtonWidget.builder(BuiltinIcon.MINUS_18, TestMod.MOD_ID).vanillaSize().build());
    linearLayoutWidget.add(IconButtonWidget.builder(BuiltinIcon.PLUS_18, TestMod.MOD_ID).vanillaSize().build());
    this.layout.addBody(linearLayoutWidget);

    this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.close()).build());

    this.addDrawable(new DrawableWidget() {
      @Override
      protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(linearLayoutWidget.getX(), linearLayoutWidget.getY(),
            linearLayoutWidget.getX() + linearLayoutWidget.getWidth(),
            linearLayoutWidget.getY() + linearLayoutWidget.getHeight(), GuiUtil.genColorInt(0, 0.4f, 0.9f)
        );
      }
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
}
