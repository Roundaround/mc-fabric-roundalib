package me.roundaround.testmod.client.screen.demo;

import me.roundaround.roundalib.asset.icon.BuiltinIcon;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.ResponsiveGridWidget;
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

import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class IconButtonDemoScreen extends Screen implements DemoScreen {
  private static final Text TITLE_TEXT = Text.translatable("testmod.iconbuttondemoscreen.title");

  private final Screen parent;
  private final ThreeSectionLayoutWidget layout = new ThreeSectionLayoutWidget(this);

  private int size = IconButtonWidget.SIZE_V;

  public IconButtonDemoScreen(Screen parent) {
    super(TITLE_TEXT);
    this.parent = parent;
  }

  @Override
  protected void init() {
    this.layout.addHeader(this.textRenderer, this.getTitle());
    this.layout.addHeader(
        new CyclingButtonWidget.Builder<Integer>((value) -> Text.of(String.format("%sx", value))).values(
                List.of(IconButtonWidget.SIZE_V, IconButtonWidget.SIZE_L, IconButtonWidget.SIZE_M,
                    IconButtonWidget.SIZE_S))
            .initially(this.size)
            .omitKeyText()
            .build(Text.empty(), this::onSizeChange));
    this.layout.setHeaderHeight(this.layout.getHeader().getContentHeight() + 2 * GuiUtil.PADDING);

    int iconSize = switch (this.size) {
      case IconButtonWidget.SIZE_S -> IconButtonWidget.SIZE_S;
      case IconButtonWidget.SIZE_M -> IconButtonWidget.SIZE_M;
      default -> IconButtonWidget.SIZE_L;
    };
    List<BuiltinIcon> icons = BuiltinIcon.valuesOfSize(iconSize);

    ResponsiveGridWidget grid = this.layout.addBody(
        new ResponsiveGridWidget(this.width - 2 * GuiUtil.PADDING, this.layout.getBodyHeight(), this.size,
            this.size
        ).spacing(GuiUtil.PADDING * 2).centered(), (parent, self) -> {
          self.setDimensions(this.width - 2 * GuiUtil.PADDING, this.layout.getBodyHeight());
        });

    for (BuiltinIcon icon : icons) {
      grid.add(IconButtonWidget.builder(icon, TestMod.MOD_ID)
          .dimensions(this.size)
          .tooltip(icon.getDisplayText(TestMod.MOD_ID))
          .build());
    }

    this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.close()).build());

    this.layout.forEachChild(this::addDrawableChild);
    this.refreshWidgetPositions();
  }

  @Override
  protected void refreshWidgetPositions() {
    this.layout.refreshPositions();
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);
  }

  @Override
  protected void clearChildren() {
    super.clearChildren();
    this.layout.clearChildren();
  }

  @Override
  public void close() {
    Objects.requireNonNull(this.client).setScreen(this.parent);
  }

  private void onSizeChange(CyclingButtonWidget<Integer> button, int size) {
    this.size = size;

    this.clearChildren();
    this.init();
  }
}
