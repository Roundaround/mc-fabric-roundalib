package me.roundaround.testmod.client.screen.demo;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.screen.ThreeSectionLayoutWidget;
import me.roundaround.roundalib.client.gui.widget.NarratableEntryListWidget;
import me.roundaround.roundalib.client.gui.widget.drawable.LabelWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class DemoSelectScreen extends Screen implements DemoScreen {
  private static final Text TITLE_TEXT = Text.translatable("testmod.demoselectscreen.title");

  private final Screen parent;
  private final ThreeSectionLayoutWidget layout = new ThreeSectionLayoutWidget(this);

  public DemoSelectScreen(Screen parent) {
    super(TITLE_TEXT);
    this.parent = parent;
  }

  @Override
  protected void init() {
    this.layout.addHeader(this.textRenderer, this.getTitle());

    DemoSelectListWidget list = this.layout.addBody(new DemoSelectListWidget(this.client, this.layout));

    list.addEntry((index, left, top, width) -> new DemoSelectListWidget.Entry(this.textRenderer,
        Text.translatable("testmod.iconbuttondemoscreen.title"), () -> {
      GuiUtil.playClickSound();
      this.navigate(new IconButtonDemoScreen(this));
    }, index, left, top, width
    ));
    list.addEntry((index, left, top, width) -> new DemoSelectListWidget.Entry(this.textRenderer,
        Text.translatable("testmod.labeldemoscreen.title"), () -> {
      GuiUtil.playClickSound();
      this.navigate(new LabelDemoScreen(this));
    }, index, left, top, width
    ));
    list.addEntry((index, left, top, width) -> new DemoSelectListWidget.Entry(this.textRenderer,
        Text.translatable("testmod.multilinelabeldemoscreen.title"), () -> {
      GuiUtil.playClickSound();
      this.navigate(new MultilineLabelDemoScreen(this));
    }, index, left, top, width
    ));
    list.addEntry((index, left, top, width) -> new DemoSelectListWidget.Entry(this.textRenderer,
        Text.translatable("testmod.linearlayoutdemoscreen.title"), () -> {
      GuiUtil.playClickSound();
      this.navigate(new LinearLayoutWidgetDemoScreen(this));
    }, index, left, top, width
    ));
    list.addEntry((index, left, top, width) -> new DemoSelectListWidget.Entry(this.textRenderer,
        Text.translatable("testmod.advancedlayoutdemoscreen.title"), () -> {
      GuiUtil.playClickSound();
      this.navigate(new AdvancedLayoutDemoScreen(this));
    }, index, left, top, width
    ));
    list.addEntry((index, left, top, width) -> new DemoSelectListWidget.Entry(this.textRenderer,
        Text.translatable("testmod.framewidgetdemoscreen.title"), () -> {
      GuiUtil.playClickSound();
      this.navigate(new FrameWidgetDemoScreen(this));
    }, index, left, top, width
    ));

    for (int i = 0; i < 15; i++) {
      int num = i + 1;
      list.addEntry((index, left, top, width) -> new DemoSelectListWidget.Entry(this.textRenderer,
          Text.translatable("testmod.demoselectscreen.filler", num), () -> {
      }, index, left, top, width
      ));
    }

    this.layout.addFooter(ButtonWidget.builder(ScreenTexts.BACK, (button) -> this.close()).build());

    this.layout.forEachChild(this::addDrawableChild);
    this.initTabNavigation();
  }

  @Override
  protected void initTabNavigation() {
    this.layout.refreshPositions();
  }

  @Override
  public void close() {
    this.navigate(this.parent);
  }

  private void navigate(Screen screen) {
    Objects.requireNonNull(this.client).setScreen(screen);
  }

  public static class DemoSelectListWidget extends NarratableEntryListWidget<DemoSelectListWidget.Entry> {
    public DemoSelectListWidget(MinecraftClient client, ThreeSectionLayoutWidget layout) {
      super(client, layout);
      this.setAlternatingRowShading(true);
    }

    public static class Entry extends NarratableEntryListWidget.Entry {
      private final Text text;
      private final DemoSelectAction action;
      private final LabelWidget label;

      public Entry(
          TextRenderer textRenderer, Text text, DemoSelectAction action, int index, int left, int top, int width
      ) {
        super(index, left, top, width, textRenderer.fontHeight + GuiUtil.PADDING);
        this.text = text;
        this.action = action;

        this.label = LabelWidget.builder(textRenderer, this.text)
            .alignTextCenterX()
            .alignTextCenterY()
            .overflowBehavior(LabelWidget.OverflowBehavior.SCROLL)
            .showShadow()
            .hideBackground()
            .build();

        this.addDrawable(this.label);
      }

      @Override
      public void refreshPositions() {
        this.label.batchUpdates(() -> {
          this.label.setPosition(this.getContentLeft(), this.getContentTop());
          this.label.setDimensions(this.getContentWidth(), this.getContentHeight());
        });

        super.refreshPositions();
      }

      @Override
      public Text getNarration() {
        return this.text;
      }

      @Override
      public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.press();
        return true;
      }

      @Override
      public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return switch (keyCode) {
          case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
            this.press();
            yield true;
          }
          default -> false;
        };
      }

      public void press() {
        this.action.run();
      }
    }
  }

  @FunctionalInterface
  public interface DemoSelectAction {
    void run();
  }
}
