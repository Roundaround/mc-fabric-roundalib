package me.roundaround.testmod.client.screen.demo;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.Spacing;
import me.roundaround.roundalib.client.gui.widget.*;
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

import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class IconButtonDemoScreen extends Screen implements DemoScreen {
  private static final Text TITLE_TEXT = Text.translatable("testmod.iconbuttondemoscreen.title");

  private final Screen parent;
  private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

  private int size = IconButtonWidget.SIZE_V;

  public IconButtonDemoScreen(Screen parent) {
    super(TITLE_TEXT);
    this.parent = parent;
  }

  @Override
  protected void init() {
    LinearLayoutWidget header = LinearLayoutWidget.vertical()
        .spacing(GuiUtil.PADDING / 2)
        .alignCenterX()
        .alignCenterY();
    this.layout.addHeader(new WrapperLayoutWidget<>(header, (self) -> {
      self.setDimensions(this.width, this.layout.getHeaderHeight());
    }));

    header.add(new TextWidget(this.getTitle(), this.textRenderer).alignCenter());
    header.add(new CyclingButtonWidget.Builder<Integer>((value) -> Text.of(String.format("%sx", value))).values(
            List.of(IconButtonWidget.SIZE_V, IconButtonWidget.SIZE_L, IconButtonWidget.SIZE_M, IconButtonWidget.SIZE_S))
        .initially(this.size)
        .omitKeyText()
        .build(Text.empty(), this::onSizeChange));

    header.refreshPositions();
    this.layout.setHeaderHeight(header.getHeight() + 2 * GuiUtil.PADDING);

    int iconSize = switch (this.size) {
      case IconButtonWidget.SIZE_S -> IconButtonWidget.SIZE_S;
      case IconButtonWidget.SIZE_M -> IconButtonWidget.SIZE_M;
      default -> IconButtonWidget.SIZE_L;
    };
    List<IconButtonWidget.BuiltinIcon> icons = IconButtonWidget.BuiltinIcon.valuesOfSize(iconSize);

    ResponsiveGridWidget grid = new ResponsiveGridWidget(this.width, this.layout.getContentHeight(), this.size,
        this.size
    ).spacing(GuiUtil.PADDING * 2).centered();
    this.layout.addBody(new FullBodyWrapperWidget(grid, this.layout).margin(Spacing.of(GuiUtil.PADDING * 2)));

    for (IconButtonWidget.BuiltinIcon icon : icons) {
      grid.add(IconButtonWidget.builder(icon, TestMod.MOD_ID)
          .dimensions(this.size)
          .tooltip(icon.getDisplayText(TestMod.MOD_ID))
          .build());
    }

    this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.close()).build());

    this.layout.forEachChild(this::addDrawableChild);
    this.initTabNavigation();
  }

  @Override
  protected void initTabNavigation() {
    this.layout.refreshPositions();
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);
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
