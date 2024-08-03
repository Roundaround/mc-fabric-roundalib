package me.roundaround.roundalib.client.gui.widget.config;

import me.roundaround.roundalib.asset.icon.BuiltinIcon;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.linear.LinearLayoutWidget;
import me.roundaround.roundalib.client.gui.layout.screen.ThreeSectionLayoutWidget;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.drawable.LabelWidget;
import me.roundaround.roundalib.client.gui.widget.ParentElementEntryListWidget;
import me.roundaround.roundalib.client.gui.widget.TooltipWidget;
import me.roundaround.roundalib.config.manage.ModConfig;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.config.panic.IllegalStatePanic;
import me.roundaround.roundalib.config.panic.Panic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigListWidget extends ParentElementEntryListWidget<ConfigListWidget.Entry> {
  protected final String modId;

  public ConfigListWidget(
      MinecraftClient client, ThreeSectionLayoutWidget layout, String modId, List<ModConfig> configs
  ) {
    super(client, layout);

    this.modId = modId;

    List<UnwrappedModConfig> validConfigs = configs.stream()
        .map(UnwrappedModConfig::unwrap)
        .filter(UnwrappedModConfig::hasOptions)
        .toList();

    validConfigs.forEach((config) -> {
      if (validConfigs.size() > 1) {
        this.addConfigEntry(config.label());
      }

      config.optionsByGroup().forEach((group, options) -> {
        if (group != null && !group.isBlank()) {
          this.addGroupEntry(group);
        }
        options.forEach(this::addOptionEntry);
      });
    });
  }

  protected void addConfigEntry(Text label) {
    this.addEntry(
        (index, left, top, width) -> new ConfigEntry(this.client.textRenderer, label, index, left, top, width));
  }

  protected void addGroupEntry(String group) {
    this.addEntry((index, left, top, width) -> new GroupEntry(this.client.textRenderer,
        Text.translatable(this.modId + "." + group + ".title"), index, left, top, width
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
        Panic.panic(new IllegalStatePanic(String.format("Failed to create control for config option: %s", option), e));
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

  public abstract static class Entry extends ParentElementEntryListWidget.Entry {
    protected Entry(int index, int left, int top, int width, int contentHeight) {
      super(index, left, top, width, contentHeight);
    }

    public void tick() {
    }

    public void update() {
    }
  }

  public static class ConfigEntry extends Entry {
    protected static final int HEIGHT = 20 + GuiUtil.PADDING;

    protected final LabelWidget label;

    protected ConfigEntry(
        TextRenderer textRenderer, Text label, int index, int left, int top, int width
    ) {
      super(index, left, top, width, HEIGHT);

      this.setMarginY(DEFAULT_MARGIN.getVertical() + GuiUtil.PADDING);

      this.label = LabelWidget.builder(textRenderer, label)
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
    protected void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
      renderRowShade(context, this.getContentLeft(), this.getContentTop(), this.getContentRight(),
          this.getContentBottom(), DEFAULT_SHADE_FADE_WIDTH * 3, DEFAULT_SHADE_STRENGTH_STRONG
      );
    }
  }

  public static class GroupEntry extends Entry {
    protected static final int HEIGHT = 20 + GuiUtil.PADDING / 2;

    protected final LabelWidget label;

    protected GroupEntry(
        TextRenderer textRenderer, Text label, int index, int left, int top, int width
    ) {
      super(index, left, top, width, HEIGHT);

      this.setMarginY(DEFAULT_MARGIN.getVertical());

      this.label = LabelWidget.builder(textRenderer, label)
          .alignTextCenterX()
          .alignTextCenterY()
          .overflowBehavior(LabelWidget.OverflowBehavior.SCROLL)
          .showShadow()
          .hideBackground()
          .build();

      this.addDrawableChild(this.label);
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
    protected void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
      renderRowShade(context, this.getContentLeft(), this.getContentTop(), this.getContentRight(),
          this.getContentBottom(), DEFAULT_SHADE_FADE_WIDTH, DEFAULT_SHADE_STRENGTH
      );
    }
  }

  public static class OptionEntry<D, O extends ConfigOption<D>> extends Entry {
    protected static final int HEIGHT = 20;
    protected static final int CONTROL_MIN_WIDTH = 100;

    protected final O option;
    protected final TooltipWidget tooltip;
    protected final LinearLayoutWidget layout;
    protected final LabelWidget label;
    protected final Control<D, O> control;
    protected final IconButtonWidget resetButton;

    protected OptionEntry(MinecraftClient client, O option, int index, int left, int top, int width)
        throws ControlRegistry.NotRegisteredException {
      super(index, left, top, width, HEIGHT);

      this.option = option;

      ArrayList<Text> tooltipLines = new ArrayList<>();
      tooltipLines.add(option.getLabel());
      tooltipLines.add(Text.literal(option.getPath().toString()).formatted(Formatting.GRAY));
      this.tooltip = this.addDrawable(new TooltipWidget(tooltipLines));

      this.layout = LinearLayoutWidget.horizontal(
              this.getContentLeft(), this.getContentTop(), this.getContentWidth(), this.getContentHeight())
          .spacing(GuiUtil.PADDING)
          .defaultOffAxisContentAlignCenter();

      this.label = LabelWidget.builder(client.textRenderer, option.getLabel())
          .alignTextLeft()
          .alignTextCenterY()
          .overflowBehavior(LabelWidget.OverflowBehavior.WRAP)
          .maxLines(2)
          .showShadow()
          .hideBackground()
          .build();
      this.layout.add(this.label, (parent, self) -> {
        self.setDimensions(this.getLabelWidth(parent), this.getContentHeight());
      });

      this.control = ControlRegistry.getControlFactory(option)
          .create(client, option, this.getControlWidth(this.layout), this.getContentHeight());
      this.layout.add(this.control, (parent, self) -> {
        self.setDimensions(this.getControlWidth(parent), this.getContentHeight());
      });

      this.resetButton = IconButtonWidget.builder(BuiltinIcon.UNDO_18, this.getOption().getModId())
          .vanillaSize()
          .messageAndTooltip(Text.translatable(this.getOption().getModId() + ".roundalib.reset.tooltip"))
          .onPress((button) -> this.getOption().setDefault())
          .build();
      this.layout.add(this.resetButton);

      this.addLayout(this.layout, (self) -> {
        self.setPositionAndDimensions(
            this.getContentLeft(), this.getContentTop(), this.getContentWidth(), this.getContentHeight());
      });
      this.layout.forEachChild(this::addDrawableChild);

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
      this.tooltip.setDimensionsAndPosition(this.label.getWidth(), this.label.getHeight(), this.label.getX(),
          this.label.getY()
      );
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

  private record UnwrappedModConfig(Text label, Map<String, List<ConfigOption<?>>> optionsByGroup) {
    static UnwrappedModConfig unwrap(ModConfig config) {
      return new UnwrappedModConfig(config.getLabel(), config.getByGroupWithGuiControl());
    }

    boolean hasOptions() {
      return !this.optionsByGroup.isEmpty();
    }
  }
}
