package me.roundaround.roundalib.client.gui.widget.config;

import com.google.common.collect.ImmutableList;
import me.roundaround.roundalib.RoundaLib;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import me.roundaround.roundalib.client.gui.widget.VariableHeightListWidget;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.stream.Stream;

public class ConfigListWidget extends VariableHeightListWidget<ConfigListWidget.Entry> {
  public ConfigListWidget(
      MinecraftClient client, ThreePartsLayoutWidget layout, ModConfig modConfig
  ) {
    super(client, layout.getX(), layout.getHeaderHeight(), layout.getWidth(), layout.getContentHeight());

    for (var entry : modConfig.getConfigOptions().entrySet()) {
      if (entry.getValue().stream().noneMatch(ConfigOption::shouldShowInConfigScreen)) {
        continue;
      }

      String modId = modConfig.getModId();
      String category = entry.getKey();
      if (modConfig.getShowGroupTitles() && !category.equals(modId)) {
        this.addEntry(
            (index, x, y, width) -> new CategoryEntry(this.client, Text.translatable(entry.getKey() + ".title"), index,
                x, y, width
            ));
      }

      for (var option : entry.getValue()) {
        if (!option.shouldShowInConfigScreen()) {
          continue;
        }

        this.addEntry((index, x, y, width) -> {
          try {
            return new OptionEntry<>(this.client, option, index, x, y, width);
          } catch (ControlRegistry.NotRegisteredException e) {
            RoundaLib.LOGGER.error("Failed to create control for config option: {}", option, e);
            return null;
          }
        });
      }
    }
  }

  public void updatePosition(ThreePartsLayoutWidget layout) {
    this.updatePosition(layout.getX(), layout.getHeaderHeight(), layout.getWidth(), layout.getContentHeight());
  }

  public void tick() {
    this.forEachEntry(Entry::tick);
  }

  public abstract static class Entry extends VariableHeightListWidget.Entry {
    protected Entry(int index, int x, int y, int width, int height) {
      super(index, x, y, width, height);
    }

    public void tick() {
    }
  }

  public static class CategoryEntry extends Entry {
    protected static final int HEIGHT = 20;

    protected final LabelWidget labelWidget;

    protected CategoryEntry(
        MinecraftClient client, Text label, int index, int x, int y, int width
    ) {
      super(index, x, y, width, HEIGHT);

      this.labelWidget = LabelWidget.builder(client, label, (this.getX() + this.getRight()) / 2,
              this.getY() + this.getHeight() / 2
          )
          .justifiedCenter()
          .alignedMiddle()
          .shiftForPadding()
          .showTextShadow()
          .hideBackground()
          .build();
    }

    @Override
    public void renderContent(DrawContext drawContext, int mouseX, int mouseY, float delta) {
      this.labelWidget.setPosY(this.getY() + this.getHeight() / 2 - (int) this.getScrollAmount());
      this.labelWidget.render(drawContext, mouseX, mouseY, delta);
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
      return ImmutableList.of(new Selectable() {
        public Selectable.SelectionType getType() {
          return Selectable.SelectionType.HOVERED;
        }

        public void appendNarrations(NarrationMessageBuilder builder) {
          builder.put(NarrationPart.TITLE, CategoryEntry.this.labelWidget.getText());
        }
      });
    }
  }

  public static class OptionEntry<D, O extends ConfigOption<D, ?>> extends Entry {
    protected static final int HEIGHT = 20;

    protected final O option;
    protected final Control<D, O> control;
    protected final LabelWidget labelWidget;
    protected final IconButtonWidget resetButton;

    protected OptionEntry(
        MinecraftClient client, O option, int index, int x, int y, int width
    ) throws ControlRegistry.NotRegisteredException {
      super(index, x, y, width, HEIGHT);

      this.option = option;
      this.control = ControlRegistry.getControlFactory(option).create(client, option);
      this.labelWidget = LabelWidget.builder(client, option.getLabel(), this.getX() + GuiUtil.PADDING,
              this.getY() + this.getHeight() / 2
          )
          .justifiedLeft()
          .alignedMiddle()
          .shiftForPadding()
          .showTextShadow()
          .hideBackground()
          .build();
      this.resetButton = RoundaLibIconButtons.resetButton(this.getControlRight() + GuiUtil.PADDING,
          this.getY() + (this.getHeight() - RoundaLibIconButtons.SIZE_L) / 2, this.option, RoundaLibIconButtons.SIZE_L
      );
    }

    public O getOption() {
      return this.option;
    }

    public int getControlRight() {
      return this.getRight() - RoundaLibIconButtons.SIZE_L - 2 * GuiUtil.PADDING;
    }

    @Override
    public List<? extends Element> children() {
      return Stream.of(this.control.children(), List.of(this.resetButton)).flatMap(List::stream).toList();
    }

    public List<? extends Selectable> selectableChildren() {
      Selectable label = new Selectable() {
        public Selectable.SelectionType getType() {
          return Selectable.SelectionType.HOVERED;
        }

        public void appendNarrations(NarrationMessageBuilder builder) {
          builder.put(NarrationPart.TITLE, OptionEntry.this.labelWidget.getText());
        }
      };
      return Stream.of(List.of(label), this.control.selectableChildren(), List.of(this.resetButton))
          .flatMap(List::stream)
          .toList();
    }

    @Override
    public void tick() {
      this.control.tick();
    }

    @Override
    public void renderContent(DrawContext drawContext, int mouseX, int mouseY, float delta) {
      this.labelWidget.setPosY(this.getY() + this.getHeight() / 2 - (int) this.getScrollAmount());
      this.labelWidget.render(drawContext, mouseX, mouseY, delta);

      this.control.setBounds(
          this.getControlRight(), this.getY(), this.getWidth(), this.getHeight(), this.getScrollAmount());
      this.control.renderWidget(drawContext, mouseX, mouseY, delta);

      this.resetButton.setY(
          this.getY() + (this.getHeight() - RoundaLibIconButtons.SIZE_L) / 2 - (int) this.getScrollAmount());
      this.resetButton.render(drawContext, mouseX, mouseY, delta);
    }
  }
}
