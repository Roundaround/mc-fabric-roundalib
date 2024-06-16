package me.roundaround.roundalib.client.gui.screen;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.FullBodyWrapperWidget;
import me.roundaround.roundalib.client.gui.widget.config.ConfigListWidget;
import me.roundaround.roundalib.config.ModConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
  protected final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(
      this, GuiUtil.COMPACT_HEADER_HEIGHT, GuiUtil.DEFAULT_HEADER_FOOTER_HEIGHT);

  private final Screen parent;
  private final ModConfig modConfig;
  private ConfigListWidget configListWidget;
  private CloseAction closeAction = CloseAction.NOOP;

  public ConfigScreen(Screen parent, ModConfig modConfig) {
    super(Text.translatable(modConfig.getConfigScreenI18nKey()));
    this.parent = parent;
    this.modConfig = modConfig;
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
    this.configListWidget = new ConfigListWidget(this.client, this.layout, this.modConfig);
    this.layout.addBody(new FullBodyWrapperWidget(this.configListWidget, this.layout));
    this.modConfig.subscribe(this::update);
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
    this.modConfig.unsubscribe(this::update);
    this.closeAction.run();
  }

  @Override
  public void tick() {
    this.configListWidget.tick();
  }

  protected void update(ModConfig modConfig) {
    this.configListWidget.update();
  }

  private void cancel(ButtonWidget button) {
    this.closeAction = this.modConfig::loadFromFile;
    this.close();
  }

  private void done(ButtonWidget button) {
    this.closeAction = this.modConfig::saveToFile;
    this.close();
  }

  @FunctionalInterface
  private interface CloseAction {
    CloseAction NOOP = () -> {
    };

    void run();
  }
}
