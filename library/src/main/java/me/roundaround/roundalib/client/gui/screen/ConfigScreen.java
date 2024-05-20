package me.roundaround.roundalib.client.gui.screen;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.config.ConfigListWidget;
import me.roundaround.roundalib.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.Collection;

public class ConfigScreen extends Screen {
  protected final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

  private static final int LIST_MIN_WIDTH = 400;
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
    this.clearConfigOptionListeners();

    this.initHeader();
    this.initFooter();

    this.initBody();

    this.layout.forEachChild(this::addDrawableChild);
    this.initTabNavigation();
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
    if (this.shouldSave) {
      this.modConfig.saveToFile();
    } else {
      this.modConfig.loadFromFile();
    }

    this.clearConfigOptionListeners();
  }

  @Override
  public void tick() {
    this.configListWidget.tick();
  }

  protected void initHeader() {
    this.layout.addHeader(this.title, this.textRenderer);
  }

  protected void initBody() {
    int listWidth = Math.round(Math.min(Math.max(LIST_MIN_WIDTH, width / 1.5f), this.width));
    int listLeft = Math.round((width - listWidth) / 2f);
    int listHeight = this.layout.getContentHeight();
    int listTop = this.layout.getHeaderHeight();

    this.configListWidget = this.addDrawableChild(new ConfigListWidget(this.client,
        this.modConfig,
        listLeft,
        listTop,
        listWidth,
        listHeight));
  }

  protected void initFooter() {
    DirectionalLayoutWidget row = DirectionalLayoutWidget.horizontal().spacing(FOOTER_BUTTON_SPACING);
    this.layout.addFooter(row);

    row.add(ButtonWidget.builder(ScreenTexts.CANCEL, this::cancel)
        .size(FOOTER_BUTTON_WIDTH, FOOTER_BUTTON_HEIGHT)
        .build());
    row.add(ButtonWidget.builder(ScreenTexts.DONE, this::done)
        .size(FOOTER_BUTTON_WIDTH, FOOTER_BUTTON_HEIGHT)
        .build());
  }

  private void clearConfigOptionListeners() {
    this.modConfig.getConfigOptions()
        .values()
        .stream()
        .flatMap(Collection::stream)
        .forEach((configOption) -> configOption.clearValueChangeListeners(this.hashCode()));
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
