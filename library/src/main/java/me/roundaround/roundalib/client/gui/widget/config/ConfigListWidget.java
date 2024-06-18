package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.FlowListWidget;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.LabelWidget;
import me.roundaround.roundalib.client.gui.widget.TooltipWidget;
import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.panic.IllegalStatePanic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;

public class ConfigListWidget extends FlowListWidget<ConfigListWidget.Entry> {
  protected final ModConfig modConfig;

  public ConfigListWidget(MinecraftClient client, ThreePartsLayoutWidget layout, ModConfig modConfig) {
    super(client, layout);

    this.alternatingRowShading(true);
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

  public abstract static class Entry extends FlowListWidget.Entry {
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

      this.label = LabelWidget.builder(textRenderer, label, this.getContentCenterX(), this.getContentCenterY())
          .justifiedCenter()
          .alignedMiddle()
          .maxWidth(this.getContentWidth())
          .overflowBehavior(LabelWidget.OverflowBehavior.SCROLL)
          .showShadow()
          .hideBackground()
          .build();

      this.addDrawableChild(this.label);
      this.addSelectable(this.label.createSelectable());
    }

    @Override
    public void refreshPositions() {
      this.label.setPosition(this.getContentCenterX(), this.getContentCenterY());
      this.label.setMaxWidth(this.getContentWidth());
      super.refreshPositions();
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
      this.tooltip = new TooltipWidget(this.getContentLeft(), this.getContentTop(), this.getLabelWidth(),
          this.getContentHeight(), tooltipLines
      );

      this.addDrawable(this.tooltip);

      this.label = LabelWidget.builder(
              client.textRenderer, option.getLabel(), this.getContentLeft(), this.getContentCenterY())
          .justifiedLeft()
          .alignedMiddle()
          .maxWidth(this.getLabelWidth())
          .overflowBehavior(LabelWidget.OverflowBehavior.WRAP)
          .maxLines(2)
          .showShadow()
          .hideBackground()
          .build();

      this.addDrawableChild(this.label);
      this.addSelectable(this.label.createSelectable());

      this.control = ControlRegistry.getControlFactory(option)
          .create(client, option, this.getControlLeft(), this.getContentTop(), this.getControlWidth(),
              this.getContentHeight()
          );
      this.control.children().forEach(this::addDetectedCapabilityChild);

      this.resetButton = IconButtonWidget.builder(IconButtonWidget.BuiltinIcon.UNDO_18, this.getOption().getModId())
          .vanillaSize()
          .position(this.getResetButtonLeft(), this.getResetButtonTop())
          .messageAndTooltip(Text.translatable(this.getOption().getModId() + ".roundalib.reset.tooltip"))
          .onPress((button) -> this.getOption().setDefault())
          .build();
      this.addDrawableAndSelectableChild(this.resetButton);

      this.update();
    }

    public O getOption() {
      return this.option;
    }

    private int getLabelWidth() {
      return this.getControlLeft() - this.getContentLeft() - GuiUtil.PADDING;
    }

    private int getControlWidth() {
      return Math.max(CONTROL_MIN_WIDTH, Math.round(this.getContentWidth() * 0.3f));
    }

    private int getControlLeft() {
      return this.getResetButtonLeft() - GuiUtil.PADDING - this.getControlWidth();
    }

    private int getResetButtonLeft() {
      return this.getContentRight() - IconButtonWidget.SIZE_V;
    }

    private int getResetButtonTop() {
      return this.getContentTop() + (this.getContentHeight() - IconButtonWidget.SIZE_V) / 2;
    }

    @Override
    public void refreshPositions() {
      this.tooltip.setDimensionsAndPosition(this.getLabelWidth(), this.getContentHeight(), this.getContentLeft(),
          this.getContentTop()
      );

      this.label.setPosition(this.getContentLeft(), this.getContentCenterY());
      this.label.setMaxWidth(this.getLabelWidth());

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
      this.control.update();
      this.resetButton.active = !this.option.isPendingDefault() && !this.option.isDisabled();
    }
  }
}
