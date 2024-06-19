package me.roundaround.testmod.client.screen.demo;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.*;
import me.roundaround.testmod.TestMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.Divider;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class AdvancedLayoutDemoScreen extends Screen implements DemoScreen {
  private static final Text TITLE_TEXT =
      Text.translatable("testmod.advancedlayoutdemoscreen.title");
  private static final int BUTTON_HEIGHT = 20;

  private final Screen parent;
  private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this,
      GuiUtil.COMPACT_HEADER_HEIGHT,
      GuiUtil.DEFAULT_HEADER_FOOTER_HEIGHT);

  public AdvancedLayoutDemoScreen(Screen parent) {
    super(TITLE_TEXT);
    this.parent = parent;
  }

  @Override
  protected void init() {
    this.layout.addHeader(this.title, this.textRenderer);

    LinearLayoutWidget body = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING);
    this.layout.addBody(new FullBodyWrapperWidget(body, this.layout));

    LinearLayoutWidget leftPane =
        body.add(LinearLayoutWidget.vertical().spacing(GuiUtil.PADDING), (parent, self) -> {
          Divider divider = new Divider(parent.getWidth() - parent.getSpacing(), 2);
          self.setWidth(divider.nextInt());
          self.setHeight(parent.getHeight());
        });

    LinearLayoutWidget searchRow =
        leftPane.add(LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING),
            (parent, self) -> self.setDimensions(parent.getWidth(), BUTTON_HEIGHT),
            Positioner::alignVerticalCenter);

    searchRow.add(new TextFieldWidget(this.textRenderer, 0, BUTTON_HEIGHT, Text.of("Search")),
        (parent, self) -> self.setWidth(
            parent.getWidth() - parent.getSpacing() - IconButtonWidget.SIZE_V),
        (positioner -> positioner.marginLeft(GuiUtil.PADDING)));

    searchRow.add(IconButtonWidget.builder(IconButtonWidget.BuiltinIcon.FILTER_18, TestMod.MOD_ID)
        .vanillaSize()
        .messageAndTooltip(Text.of("Filter"))
        .onPress((button) -> GuiUtil.playClickSound())
        .build());

    PlaceholderListWidget listWidget = leftPane.add(new PlaceholderListWidget(this.client),
        (parent, self) -> self.setDimensions(parent.getWidth(),
            parent.getHeight() - parent.getSpacing() - BUTTON_HEIGHT));
    listWidget.addRows(15);
    listWidget.selectFirst();

    LinearLayoutWidget rightPane =
        body.add(LinearLayoutWidget.vertical().spacing(GuiUtil.PADDING), (parent, self) -> {
          Divider divider = new Divider(parent.getWidth() - parent.getSpacing(), 2);
          divider.skip(1);
          self.setWidth(divider.nextInt());
          self.setHeight(parent.getHeight());
        }, Positioner::alignHorizontalCenter);

    LabelWidget label = rightPane.add(LabelWidget.builder(this.textRenderer, Text.of("Label"), 0, 0)
        .hideBackground()
        .showShadow()
        .build());

    FillerWidget paintingPlaceholder = rightPane.add(FillerWidget.empty(),
        (parent, self) -> self.setDimensions(parent.getWidth(),
            parent.getHeight() - label.getHeight() - IconButtonWidget.SIZE_V -
                2 * parent.getSpacing()));

    this.addDrawable((context, mouseX, mouseY, delta) -> {
      int left = paintingPlaceholder.getX();
      int top = paintingPlaceholder.getY();
      int width = paintingPlaceholder.getWidth();
      int height = paintingPlaceholder.getHeight();
      context.fill(left, top, left + width, top + height, GuiUtil.genColorInt(0, 0, 0));
      context.drawBorder(left, top, width, height, GuiUtil.genColorInt(0.8f, 0.2f, 0.6f));
    });

    LinearLayoutWidget controlsRow =
        rightPane.add(LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING),
            (parent, self) -> self.setDimensions(parent.getWidth(), IconButtonWidget.SIZE_V),
            Positioner::alignVerticalCenter);

    controlsRow.add(IconButtonWidget.builder(IconButtonWidget.BuiltinIcon.PREV_18, TestMod.MOD_ID)
        .vanillaSize()
        .messageAndTooltip(Text.of("Previous"))
        .build());

    controlsRow.add(LabelWidget.builder(this.textRenderer,
        Text.of(String.format("%s total items", listWidget.getEntryCount())),
        0,
        0).alignedMiddle().justifiedCenter().build());

    controlsRow.add(IconButtonWidget.builder(IconButtonWidget.BuiltinIcon.NEXT_18, TestMod.MOD_ID)
        .vanillaSize()
        .messageAndTooltip(Text.of("Next"))
        .build());

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

  @Environment(value = EnvType.CLIENT)
  public static class PlaceholderListWidget
      extends AlwaysSelectedFlowListWidget<PlaceholderListWidget.Entry> {
    public PlaceholderListWidget(MinecraftClient client) {
      super(client, 0, 0, 0, 0);
    }

    public void addRows(int num) {
      for (int i = 0; i < num; i++) {
        this.addEntry((index, left, top, width) -> new Entry(this.client.textRenderer,
            index,
            left,
            top,
            width));
      }
    }

    public void selectFirst() {
      if (this.getEntryCount() > 0) {
        this.setSelected(this.getEntry(0));
      }
    }

    @Environment(value = EnvType.CLIENT)
    public static class Entry extends AlwaysSelectedFlowListWidget.Entry {
      private final LabelWidget label;

      public Entry(TextRenderer textRenderer, int index, int left, int top, int width) {
        super(index, left, top, width, 20);

        this.label = LabelWidget.builder(textRenderer,
                Text.of(String.format("Row #%s", index)),
                this.getContentCenterX(),
                this.getContentCenterY())
            .justifiedCenter()
            .alignedMiddle()
            .hideBackground()
            .showShadow()
            .build();
      }

      @Override
      public Text getNarration() {
        return ScreenTexts.EMPTY;
      }

      @Override
      public void refreshPositions() {
        this.label.setPosition(this.getContentCenterX(), this.getContentCenterY());
      }

      @Override
      public void renderDecorations(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawBorder(this.getX(),
            this.getY(),
            this.getWidth(),
            this.getHeight(),
            GuiUtil.genColorInt(0.8f, 0.2f, 0.6f));
      }
    }
  }
}
