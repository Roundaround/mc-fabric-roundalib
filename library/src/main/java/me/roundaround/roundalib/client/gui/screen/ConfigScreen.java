package me.roundaround.roundalib.client.gui.screen;

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
  protected final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

  private static final int FOOTER_BUTTON_WIDTH = 150;
  private static final int FOOTER_BUTTON_HEIGHT = 20;
  private static final int FOOTER_BUTTON_SPACING = 8;
  private final Screen parent;
  private final ModConfig modConfig;
  private ConfigListWidget configListWidget;
  private boolean shouldSave = false;

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
    DirectionalLayoutWidget row = DirectionalLayoutWidget.horizontal().spacing(FOOTER_BUTTON_SPACING);
    this.layout.addFooter(row);

    row.add(
        ButtonWidget.builder(ScreenTexts.CANCEL, this::cancel).size(FOOTER_BUTTON_WIDTH, FOOTER_BUTTON_HEIGHT).build());
    row.add(ButtonWidget.builder(ScreenTexts.DONE, this::done).size(FOOTER_BUTTON_WIDTH, FOOTER_BUTTON_HEIGHT).build());
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

    if (this.shouldSave) {
      this.modConfig.saveToFile();
    } else {
      this.modConfig.loadFromFile();
    }
  }

  @Override
  public void tick() {
    this.configListWidget.tick();
  }

  protected void update(ModConfig modConfig) {
    this.configListWidget.update();
  }

  private void cancel(ButtonWidget button) {
    this.shouldSave = false;
    this.close();
  }

  private void done(ButtonWidget button) {
    this.shouldSave = true;
    this.close();
  }
}
