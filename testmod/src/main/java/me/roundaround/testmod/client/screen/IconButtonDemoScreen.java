package me.roundaround.testmod.client.screen;

import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.ResponsiveGridWidget;
import me.roundaround.testmod.TestMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class IconButtonDemoScreen extends Screen {
  private static final Text TITLE_TEXT = Text.translatable("testmod.iconbuttondemoscreen.title");

  private final Screen parent;
  private ThreePartsLayoutWidget layout;

  private int size = IconButtonWidget.SIZE_V;

  public IconButtonDemoScreen(Screen parent) {
    super(TITLE_TEXT);
    this.parent = parent;
  }

  @Override
  protected void init() {
    this.layout = new ThreePartsLayoutWidget(this);

    DirectionalLayoutWidget header = DirectionalLayoutWidget.vertical().spacing(2);
    this.layout.addHeader(header);

    header.getMainPositioner().alignHorizontalCenter();
    header.add(new EmptyWidget(0, 0));
    header.add(new TextWidget(this.getTitle(), this.textRenderer).alignCenter());
    header.add(new CyclingButtonWidget.Builder<Integer>((value) -> Text.of(String.format("%sx", value))).values(
            List.of(IconButtonWidget.SIZE_V, IconButtonWidget.SIZE_L, IconButtonWidget.SIZE_M, IconButtonWidget.SIZE_S))
        .initially(this.size)
        .omitKeyText()
        .build(0, 0, ButtonWidget.DEFAULT_WIDTH, ButtonWidget.DEFAULT_HEIGHT, Text.empty(), this::onSizeChange));

    int iconSize = switch (this.size) {
      case IconButtonWidget.SIZE_S -> IconButtonWidget.SIZE_S;
      case IconButtonWidget.SIZE_M -> IconButtonWidget.SIZE_M;
      default -> IconButtonWidget.SIZE_L;
    };
    List<IconButtonWidget.BuiltinIcon> icons = IconButtonWidget.BuiltinIcon.valuesOfSize(iconSize);

    ResponsiveGridWidget grid = new ResponsiveGridWidget(
        this.width, this.layout.getContentHeight(), this.size, this.size);
    this.layout.addBody(grid);

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
  public void close() {
    Objects.requireNonNull(this.client).setScreen(this.parent);
  }

  private void onSizeChange(CyclingButtonWidget<Integer> button, int size) {
    this.size = size;

    this.clearChildren();
    this.init();
  }
}
