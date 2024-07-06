package me.roundaround.roundalib.client.gui.screen;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.FullBodyWrapperWidget;
import me.roundaround.roundalib.client.gui.widget.config.ConfigListWidget;
import me.roundaround.roundalib.config.manage.ModConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ConfigScreen extends Screen {
  protected final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

  private final Screen parent;
  private final String modId;
  private final ArrayList<ModConfig> configs = new ArrayList<>();
  private ConfigListWidget configListWidget;
  private CloseAction closeAction = CloseAction.NOOP;

  public ConfigScreen(Screen parent, String modId, ModConfig... configs) {
    this(Text.translatable(modId + ".config.title"), parent, modId, configs);
  }

  public ConfigScreen(Screen parent, String modId, Iterable<ModConfig> configs) {
    this(Text.translatable(modId + ".config.title"), parent, modId, configs);
  }

  public ConfigScreen(Text title, Screen parent, String modId, ModConfig... configs) {
    this(title, parent, modId, List.of(configs));
  }

  public ConfigScreen(Text title, Screen parent, String modId, Iterable<ModConfig> configs) {
    super(title);
    this.parent = parent;
    this.modId = modId;

    for (ModConfig config : configs) {
      if (config.isReady()) {
        this.configs.add(config);
      }
    }
  }

  public String getModId() {
    return this.modId;
  }

  @Override
  protected void init() {
    this.initHeader();
    this.initBody();
    this.initFooter();

    this.layout.forEachChild(this::addDrawableChild);
    this.initTabNavigation();
  }

  protected void initHeader() {
    this.layout.addHeader(this.title, this.textRenderer);
  }

  protected void initBody() {
    this.configListWidget = new ConfigListWidget(this.client, this.layout, this.modId, this.configs);
    this.layout.addBody(new FullBodyWrapperWidget(this.configListWidget, this.layout));
    this.configs.forEach((config) -> config.subscribe(this::update));
  }

  protected void initFooter() {
    DirectionalLayoutWidget row = DirectionalLayoutWidget.horizontal().spacing(GuiUtil.PADDING * 2);
    this.layout.addFooter(row);

    row.add(ButtonWidget.builder(ScreenTexts.CANCEL, this::cancel).build());
    row.add(ButtonWidget.builder(ScreenTexts.DONE, this::done).build());
  }

  @Override
  protected void initTabNavigation() {
    this.layout.refreshPositions();
  }

  @Override
  public void close() {
    assert this.client != null;
    this.client.setScreen(this.parent);
  }

  @Override
  public void removed() {
    this.configs.forEach((config) -> {
      config.unsubscribe(this::update);
      this.closeAction.run(config);
    });
  }

  @Override
  public void tick() {
    this.configListWidget.tick();
  }

  protected void update(ModConfig config) {
    this.configListWidget.update();
  }

  private void cancel(ButtonWidget button) {
    this.closeAction = ModConfig::readFromStore;
    this.close();
  }

  private void done(ButtonWidget button) {
    this.closeAction = ModConfig::writeToStore;
    this.close();
  }

  @FunctionalInterface
  private interface CloseAction {
    CloseAction NOOP = (config) -> {
    };

    void run(ModConfig config);
  }
}
