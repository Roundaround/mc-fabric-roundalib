package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.RoundaLib;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.LabelElement;
import me.roundaround.roundalib.client.gui.RoundaLibIconButtons;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.VariableHeightListWidget;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.text.Text;

public class ConfigListWidget extends VariableHeightListWidget<ConfigListWidget.Entry> {
  public ConfigListWidget(MinecraftClient client, ThreePartsLayoutWidget layout, ModConfig modConfig) {
    super(client, layout.getX(), layout.getHeaderHeight(), layout.getWidth(), layout.getContentHeight());

    for (var entry : modConfig.getConfigOptions().entrySet()) {
      if (entry.getValue().stream().noneMatch(ConfigOption::shouldShowInConfigScreen)) {
        continue;
      }

      String modId = modConfig.getModId();
      String category = entry.getKey();
      if (modConfig.getShowGroupTitles() && !category.equals(modId)) {
        this.addEntry((index, left, top, width) -> new CategoryEntry(this.client.textRenderer,
            Text.translatable(entry.getKey() + ".title"), index, left, top, width
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

  public void update() {
    this.forEachEntry(Entry::update);
  }

  public abstract static class Entry extends VariableHeightListWidget.Entry {
    protected Entry(int index, int left, int top, int width, int contentHeight) {
      super(index, left, top, width, contentHeight);
    }

    public void tick() {
    }

    public void update() {
    }
  }

  public static class CategoryEntry extends Entry {
    protected static final int HEIGHT = 20;

    protected final LabelElement label;

    protected CategoryEntry(
        TextRenderer textRenderer, Text label, int index, int left, int top, int width
    ) {
      super(index, left, top, width, HEIGHT);

      this.label = LabelElement.builder(textRenderer, label, this.getContentCenterX(), this.getContentCenterY())
          .justifiedCenter()
          .alignedMiddle()
          .showShadow()
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
      this.label.setPosition(this.getContentCenterX(), this.getContentCenterY());
      super.refreshPositions();
    }
  }

  public static class OptionEntry<D, O extends ConfigOption<D>> extends Entry {
    protected static final int HEIGHT = 20;
    protected static final int CONTROL_MIN_WIDTH = 100;

    protected final O option;
    protected final Control<D, O> control;
    protected final LabelElement label;
    protected final IconButtonWidget resetButton;

    protected OptionEntry(MinecraftClient client, O option, int index, int left, int top, int width)
        throws ControlRegistry.NotRegisteredException {
      super(index, left, top, width, HEIGHT);

      this.setMarginHorizontal(DEFAULT_MARGIN_HORIZONTAL + GuiUtil.PADDING);

      this.option = option;

      this.label = LabelElement.builder(
              client.textRenderer, option.getLabel(), this.getContentLeft(), this.getContentCenterY())
          .justifiedLeft()
          .alignedMiddle()
          .showShadow()
          .hideBackground()
          .build();

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
          .create(client, option, this.getControlLeft(), this.getContentTop(), this.getControlWidth(),
              this.getContentHeight()
          );
      this.control.children().forEach(this::addChild);
      this.control.selectableChildren().forEach(this::addSelectableChild);

      this.resetButton = RoundaLibIconButtons.resetButton(this.getResetButtonLeft(), this.getResetButtonTop(),
          this.option, RoundaLibIconButtons.SIZE_L
      );

      this.addChild(this.resetButton);
      this.addSelectableChild(this.resetButton);

      this.update();
    }

    public O getOption() {
      return this.option;
    }

    private int getControlWidth() {
      return Math.max(CONTROL_MIN_WIDTH, Math.round(this.getContentWidth() * 0.3f));
    }

    private int getControlLeft() {
      return this.getResetButtonLeft() - GuiUtil.PADDING - this.getControlWidth();
    }

    private int getResetButtonLeft() {
      return this.getContentRight() - RoundaLibIconButtons.SIZE_L;
    }

    private int getResetButtonTop() {
      return this.getContentTop() + (this.getContentHeight() - RoundaLibIconButtons.SIZE_L) / 2;
    }

    @Override
    public void refreshPositions() {
      this.label.setPosition(this.getContentLeft(), this.getContentCenterY());

      this.control.setPosition(this.getControlLeft(), this.getContentTop());
      this.control.setDimensions(this.getControlWidth(), this.getContentHeight());
      this.control.refreshPositions();

      this.resetButton.setPosition(this.getResetButtonLeft(), this.getResetButtonTop());

      super.refreshPositions();
    }

    @Override
    public void tick() {
      this.control.tick();
    }

    @Override
    public void update() {
      this.resetButton.active = !option.isPendingDefault() && !option.isDisabled();
    }
  }
}
