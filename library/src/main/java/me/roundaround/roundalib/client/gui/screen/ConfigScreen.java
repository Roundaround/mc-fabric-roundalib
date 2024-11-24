package me.roundaround.roundalib.client.gui.screen;

import me.roundaround.roundalib.client.gui.layout.screen.ThreeSectionLayoutWidget;
import me.roundaround.roundalib.client.gui.widget.config.ConfigListWidget;
import me.roundaround.roundalib.config.manage.ModConfig;
import me.roundaround.roundalib.util.Observable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigScreen extends Screen {
  protected final ThreeSectionLayoutWidget layout = new ThreeSectionLayoutWidget(this);

  protected final Screen parent;
  protected final String modId;
  protected final double prevScrollAmount;
  protected final ArrayList<ModConfig> configs = new ArrayList<>();
  protected final ArrayList<Observable.Subscription> subscriptions = new ArrayList<>();

  protected ConfigListWidget configListWidget;
  protected CloseAction closeAction = CloseAction.NOOP;

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
    this(title, parent, modId, configs, 0);
  }

  protected ConfigScreen(Text title, Screen parent, String modId, Iterable<ModConfig> configs, double scrollAmount) {
    super(title);
    this.parent = parent;
    this.modId = modId;
    this.prevScrollAmount = scrollAmount;

    for (ModConfig config : configs) {
      if (config.isReady()) {
        this.configs.add(config);
      }
    }
  }

  public String getModId() {
    return this.modId;
  }

  public ConfigScreen copy() {
    return new ConfigScreen(
        this.getTitle(), this.parent, this.modId, this.configs, this.configListWidget.getScrollAmount());
  }

  @Override
  protected void init() {
    this.initHeader();
    this.initBody();
    this.initFooter();

    this.layout.forEachChild(this::addDrawableChild);
    this.refreshWidgetPositions();
  }

  protected void initHeader() {
    this.layout.addHeader(this.textRenderer, this.title);
  }

  protected void initBody() {
    this.configListWidget = this.layout.addBody(
        new ConfigListWidget(this.client, this.layout, this.modId, this.configs), (parent, self) -> {
          self.setDimensionsAndPosition(parent.getWidth(), parent.getHeight(), parent.getX(), parent.getY());
        });
    this.configListWidget.setScrollAmount(this.prevScrollAmount);
    this.subscriptions.addAll(this.configListWidget.collectSubscriptions());
  }

  protected void initFooter() {
    this.layout.addFooter(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> this.cancel()).build());
    this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.done()).build());
  }

  @Override
  protected void refreshWidgetPositions() {
    this.layout.refreshPositions();
  }

  @Override
  public void close() {
    Objects.requireNonNull(this.client).setScreen(this.parent);
  }

  @Override
  public void removed() {
    this.subscriptions.forEach(Observable.Subscription::unsubscribe);
    this.subscriptions.clear();

    this.configs.forEach((config) -> {
      this.closeAction.run(config);
    });
  }

  @Override
  public void tick() {
    this.configListWidget.tick();
  }

  protected void cancel() {
    this.closeAction = ModConfig::readFromStore;
    this.close();
  }

  protected void done() {
    this.closeAction = ModConfig::writeToStore;
    this.close();
  }

  @FunctionalInterface
  protected interface CloseAction {
    CloseAction NOOP = (config) -> {
    };

    void run(ModConfig config);
  }
}
