package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.RoundaLib;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import me.roundaround.roundalib.client.gui.widget.VariableHeightListWidget;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.text.Text;

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
            (index, left, top, width) -> new CategoryEntry(this.client, Text.translatable(entry.getKey() + ".title"),
                index, left, top, width
            ));
      }

      for (var option : entry.getValue()) {
        if (!option.shouldShowInConfigScreen()) {
          continue;
        }

        this.addEntry((index, left, top, width) -> {
          try {
            return new OptionEntry<>(this.client, option, index, left, top, width);
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
    protected Entry(int index, int left, int top, int width, int contentHeight) {
      super(index, left, top, width, contentHeight);
    }

    public void tick() {
    }
  }

  public static class CategoryEntry extends Entry {
    protected static final int HEIGHT = 20;

    protected final LabelWidget label;

    protected CategoryEntry(
        MinecraftClient client, Text label, int index, int left, int top, int width
    ) {
      super(index, left, top, width, HEIGHT);

      this.label = LabelWidget.centered(client, label, this.getContentLeft(), this.getContentWidth(),
              this.getContentTop(), this.getContentHeight()
          )
          .shiftForPadding()
          .showTextShadow()
          .hideBackground()
          .build();

      this.addChild(this.label);
      this.addSelectableChild(new Selectable() {
        public Selectable.SelectionType getType() {
          return Selectable.SelectionType.HOVERED;
        }

        public void appendNarrations(NarrationMessageBuilder builder) {
          builder.put(NarrationPart.TITLE, CategoryEntry.this.label.getText());
        }
      });
    }

    @Override
    public void refreshPositions() {
      this.label.setPosition(this.getContentLeft(), this.getContentTop() + this.getHeight() / 2);
      super.refreshPositions();
    }
  }

  public static class OptionEntry<D, O extends ConfigOption<D, ?>> extends Entry {
    protected static final int HEIGHT = 20;
    protected static final int CONTROL_MIN_WIDTH = 100;

    protected final O option;
    protected final Control<D, O> control;
    protected final LabelWidget label;
    protected final IconButtonWidget resetButton;

    protected OptionEntry(
        MinecraftClient client, O option, int index, int left, int top, int width
    ) throws ControlRegistry.NotRegisteredException {
      super(index, left, top, width, HEIGHT);

      int controlLeft = this.getControlLeft();
      int controlRight = this.getControlRight();
      int controlWidth = controlRight - controlLeft;

      this.option = option;

      this.label = LabelWidget.builder(client, option.getLabel(), this.getContentLeft() + GuiUtil.PADDING,
          this.getContentTop() + this.getContentHeight() / 2
      ).justifiedLeft().alignedMiddle().shiftForPadding().showTextShadow().hideBackground().build();

      this.addChild(this.label);
      this.addSelectableChild(new Selectable() {
        public Selectable.SelectionType getType() {
          return Selectable.SelectionType.HOVERED;
        }

        public void appendNarrations(NarrationMessageBuilder builder) {
          builder.put(NarrationPart.TITLE, OptionEntry.this.label.getText());
        }
      });

      this.control = ControlRegistry.getControlFactory(option)
          .create(client, option, controlLeft, this.getContentTop(), controlWidth, HEIGHT);
      this.control.children().forEach(this::addChild);
      this.control.selectableChildren().forEach(this::addSelectableChild);

      this.resetButton = RoundaLibIconButtons.resetButton(controlRight + GuiUtil.PADDING,
          this.getContentTop() + (this.getContentHeight() - RoundaLibIconButtons.SIZE_L) / 2, this.option,
          RoundaLibIconButtons.SIZE_L
      );
      this.addChild(this.resetButton);
      this.addSelectableChild(this.resetButton);
    }

    public O getOption() {
      return this.option;
    }

    private int getControlLeft() {
      return Math.max(CONTROL_MIN_WIDTH,
          this.getContentLeft() + Math.round((this.getContentLeft() + this.getControlRight()) * 0.3f)
      );
    }

    private int getControlRight() {
      return this.getContentRight() - RoundaLibIconButtons.SIZE_L - 2 * GuiUtil.PADDING;
    }

    @Override
    public void refreshPositions() {
      int controlLeft = this.getControlLeft();
      int controlRight = this.getControlRight();

      this.label.setPosition(
          this.getContentLeft() + GuiUtil.PADDING, this.getContentTop() + this.getContentHeight() / 2);

      this.control.setPosition(controlLeft, this.getContentTop());
      this.control.setDimensions(controlRight - controlLeft, this.getContentHeight());

      this.resetButton.setPosition(controlRight + GuiUtil.PADDING,
          this.getContentTop() + (this.getContentHeight() - RoundaLibIconButtons.SIZE_L) / 2
      );

      super.refreshPositions();
    }

    @Override
    public void tick() {
      this.control.tick();
    }
  }
}
