package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.*;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.panic.IllegalStatePanic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;

public class ConfigListWidget extends ParentElementEntryListWidget<ConfigListWidget.Entry> {
  protected final ModConfig modConfig;

  public ConfigListWidget(MinecraftClient client, ThreePartsLayoutWidget layout, ModConfig modConfig) {
    super(client, layout);

    this.modConfig = modConfig;

    this.modConfig.getConfigOptions().forEach((group, options) -> {
      if (options.stream().noneMatch(ConfigOption::hasGuiControl)) {
        return;
      }

      this.addGroupEntry(group);
      options.forEach(this::addOptionEntry);
    });
  }

  protected void addGroupEntry(String group) {
    if (!this.modConfig.getShowGroupTitles() || group.equals(this.modConfig.getModId())) {
      return;
    }

    this.addEntry(
        (index, left, top, width) -> new GroupEntry(this.client.textRenderer, Text.translatable(group + ".title"),
            index, left, top, width
        ));
  }

  protected void addOptionEntry(ConfigOption<?> option) {
    if (!option.hasGuiControl()) {
      return;
    }

    this.addEntry((index, left, top, width) -> {
      try {
        return new OptionEntry<>(this.client, option, index, left, top, width);
      } catch (ControlRegistry.NotRegisteredException e) {
        this.modConfig.panic(
            new IllegalStatePanic(String.format("Failed to create control for config option: %s", option), e));
        return null;
      }
    });
  }

  public void tick() {
    this.forEachEntry(Entry::tick);
  }

  public void update() {
    this.forEachEntry(Entry::update);
  }

  @Override
  protected int getPreferredContentWidth() {
    return this.getWidth();
  }

  public abstract static class Entry extends ParentElementEntryListWidget.Entry {
    protected Entry(int index, int left, int top, int width, int contentHeight) {
      super(index, left, top, width, contentHeight);
    }

    public void tick() {
    }

    public void update() {
    }
  }

  public static class GroupEntry extends Entry {
    protected static final int HEIGHT = 20;

    protected final LabelWidget label;

    protected GroupEntry(
        TextRenderer textRenderer, Text label, int index, int left, int top, int width
    ) {
      super(index, left, top, width, HEIGHT);

      this.setForceRowShading(true);
      this.setRowShadeStrength(DEFAULT_SHADE_STRENGTH_STRONG);

      this.label = LabelWidget.builder(textRenderer, label)
          .refPosition(this.getContentCenterX(), this.getContentCenterY())
          .dimensions(this.getContentWidth(), this.getContentHeight())
          .justifiedCenter()
          .alignedMiddle()
          .overflowBehavior(LabelWidget.OverflowBehavior.SCROLL)
          .showShadow()
          .hideBackground()
          .build();

      this.addDrawableChild(this.label);
    }

    @Override
    public void refreshPositions() {
      this.label.batchUpdates(() -> {
        this.label.setPosition(this.getContentCenterX(), this.getContentCenterY());
        this.label.setDimensions(this.getContentWidth(), this.getContentHeight());
      });
    }
  }

  public static class OptionEntry<D, O extends ConfigOption<D>> extends Entry {
    protected static final int HEIGHT = 20;
    protected static final int CONTROL_MIN_WIDTH = 100;

    protected final O option;
    protected final TooltipWidget tooltip;
    protected final LabelWidget label;
    protected final Control<D, O> control;
    protected final IconButtonWidget resetButton;

    protected OptionEntry(MinecraftClient client, O option, int index, int left, int top, int width)
        throws ControlRegistry.NotRegisteredException {
      super(index, left, top, width, HEIGHT);

      this.option = option;

      ArrayList<Text> tooltipLines = new ArrayList<>();
      tooltipLines.add(option.getLabel());
      tooltipLines.add(Text.literal(option.getPath()).formatted(Formatting.GRAY));
      this.tooltip = this.addDrawable(new TooltipWidget(tooltipLines));

      LinearLayoutWidget layout = LinearLayoutWidget.horizontal().spacing(GuiUtil.PADDING);
      layout.getMainPositioner().alignVerticalCenter();

      this.label = LabelWidget.builder(client.textRenderer, option.getLabel())
          .justifiedLeft()
          .alignedMiddle()
          .overflowBehavior(LabelWidget.OverflowBehavior.WRAP)
          .maxLines(2)
          .showShadow()
          .hideBackground()
          .build();
      layout.add(this.label, (parent, self) -> {
        self.setDimensions(this.getLabelWidth(parent), this.getContentHeight());
      });

      this.control = ControlRegistry.getControlFactory(option)
          .create(client, option, this.getControlWidth(layout), this.getContentHeight());
      layout.add(this.control, (parent, self) -> {
        self.setDimensions(this.getControlWidth(parent), this.getContentHeight());
      });

      this.resetButton = IconButtonWidget.builder(IconButtonWidget.BuiltinIcon.UNDO_18, this.getOption().getModId())
          .vanillaSize()
          .messageAndTooltip(Text.translatable(this.getOption().getModId() + ".roundalib.reset.tooltip"))
          .onPress((button) -> this.getOption().setDefault())
          .build();
      layout.add(this.resetButton);

      this.addLayout(layout, (self) -> {
        self.setDimensionsAndPosition(
            this.getContentWidth(), this.getContentHeight(), this.getContentLeft(), this.getContentTop());
      });
      layout.forEachChild(this::addDrawableChild);

      this.update();
    }

    public O getOption() {
      return this.option;
    }

    private int getLabelWidth(LinearLayoutWidget layout) {
      return layout.getWidth() - 2 * layout.getSpacing() - this.getControlWidth(layout) - IconButtonWidget.SIZE_V;
    }

    private int getControlWidth(LinearLayoutWidget layout) {
      return Math.max(CONTROL_MIN_WIDTH, Math.round(layout.getWidth() * 0.3f));
    }

    @Override
    public void refreshPositions() {
      super.refreshPositions();
      this.tooltip.setDimensionsAndPosition(
          this.label.getWidth(), this.label.getHeight(), this.label.getX(), this.label.getY());
    }

    @Override
    public void tick() {
      this.control.tick();
    }

    @Override
    public void update() {
      this.control.update();
      this.resetButton.active = !this.option.isPendingDefault() && !this.option.isDisabled();
    }
  }
}
