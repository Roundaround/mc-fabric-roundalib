package me.roundaround.roundalib.client.gui.screen;

import me.roundaround.roundalib.asset.icon.BuiltinIcon;
import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.layout.NonPositioningLayoutWidget;
import me.roundaround.roundalib.client.gui.layout.linear.LinearLayoutWidget;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.client.gui.widget.drawable.LabelWidget;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.util.Observable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ConfigOptionSubScreen<D, O extends ConfigOption<D>> extends Screen {
  protected final ConfigScreen parent;
  protected final O option;
  protected final String modId;
  protected final Text helpShortText;
  protected final Text helpCloseText;
  protected final Text helpResetText;
  protected Observable<Boolean> shiftState;
  protected final NonPositioningLayoutWidget nonPositioningRoot = new NonPositioningLayoutWidget();
  protected final List<Observable.Subscription> subscriptions = new ArrayList<>();

  protected LabelWidget titleLabel;
  protected LabelWidget helpLabel;
  protected LinearLayoutWidget actionRow;

  protected ConfigOptionSubScreen(Text title, ConfigScreen parent, O option) {
    super(title);
    this.parent = parent;
    this.option = option;
    this.modId = option.getModId();

    this.helpShortText = Text.translatable(this.modId + ".roundalib.help.short");
    this.helpCloseText = Text.translatable(this.modId + ".roundalib.help.close");
    this.helpResetText = MinecraftClient.IS_SYSTEM_MAC ?
        Text.translatable(this.modId + ".roundalib.help.reset.mac") :
        Text.translatable(this.modId + ".roundalib.help.reset.win");

    this.shiftState = Observable.of(hasShiftDown());
  }

  @Override
  protected void init() {
    this.initElements();
    this.nonPositioningRoot.forEachChild(this::addDrawableChild);
    this.initTabNavigation();
  }

  protected void initElements() {
    this.titleLabel = this.createTitleLabel();
    this.placeTitleLabel(this.titleLabel);

    this.helpLabel = this.createHelpLabel();
    this.placeHelpLabel(this.helpLabel);

    this.actionRow = this.createActionRow();
    this.placeActionRow(this.actionRow);
  }

  @Override
  protected void initTabNavigation() {
    this.nonPositioningRoot.setPositionAndDimensions(0, 0, this.width, this.height);
    this.nonPositioningRoot.refreshPositions();
  }

  @Override
  public void close() {
    this.subscriptions.forEach(Observable.Subscription::unsubscribe);
    this.subscriptions.clear();

    Objects.requireNonNull(this.client).setScreen(this.parent.copy());
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    this.shiftState.set(hasShiftDown());

    if (keyCode == GLFW.GLFW_KEY_R) {
      if (Screen.hasControlDown()) {
        this.resetToDefault();
        return true;
      }
    }

    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
    this.shiftState.set(hasShiftDown());
    return super.keyReleased(keyCode, scanCode, modifiers);
  }

  protected List<Text> getCurrentHelp() {
    return this.getCurrentHelp(this.shiftState.get());
  }

  protected List<Text> getCurrentHelp(boolean shiftDown) {
    return this.shiftState.get() ? this.getHelpLong() : this.getHelpShort();
  }

  protected List<Text> getHelpShort() {
    return List.of(this.helpShortText);
  }

  protected List<Text> getHelpLong() {
    return List.of(this.helpCloseText, this.helpResetText);
  }

  protected O getOption() {
    return this.option;
  }

  protected void setValue(D value) {
    this.getOption().setValue(value);
  }

  protected D getValue() {
    return this.getOption().getPendingValue();
  }

  protected String getValueAsString() {
    return this.getOption().getPendingValueAsString();
  }

  protected void resetToDefault() {
    this.getOption().setDefault();
  }

  protected boolean isDirty() {
    return this.getOption().isDirty();
  }

  protected LabelWidget createTitleLabel() {
    return this.createTitleLabel(this.getTitle());
  }

  protected LabelWidget createTitleLabel(Text text) {
    return LabelWidget.builder(this.textRenderer, text)
        .alignTextCenterX()
        .alignTextCenterY()
        .overflowBehavior(LabelWidget.OverflowBehavior.SCROLL)
        .hideBackground()
        .showShadow()
        .build();
  }

  protected void placeTitleLabel(LabelWidget titleLabel) {
    this.nonPositioningRoot.add(titleLabel, (parent, self) -> {
      self.setDimensionsAndPosition(
          parent.getWidth(), ThreePartsLayoutWidget.DEFAULT_HEADER_FOOTER_HEIGHT, parent.getX(), parent.getY());
    });
  }

  protected LabelWidget createHelpLabel() {
    LabelWidget helpLabel = LabelWidget.builder(this.textRenderer, this.getCurrentHelp())
        .alignTextLeft()
        .alignTextBottom()
        .lineSpacing(2)
        .hideBackground()
        .showShadow()
        .build();
    this.subscriptions.add(this.shiftState.subscribe((shiftDown) -> {
      helpLabel.setText(this.getCurrentHelp(shiftDown));
    }));
    return helpLabel;
  }

  protected void placeHelpLabel(LabelWidget helpLabel) {
    this.nonPositioningRoot.add(helpLabel, (parent, self) -> {
      self.setDimensionsAndPosition(parent.getWidth() - 2 * GuiUtil.PADDING, parent.getHeight() - 2 * GuiUtil.PADDING,
          parent.getX() + GuiUtil.PADDING, parent.getY() + GuiUtil.PADDING
      );
    });
  }

  protected LinearLayoutWidget createActionRow() {
    LinearLayoutWidget actionRow = LinearLayoutWidget.horizontal()
        .spacing(GuiUtil.PADDING)
        .defaultOffAxisContentAlignCenter();

    actionRow.add(this.createBackButton());
    actionRow.add(this.createResetButton());

    return actionRow;
  }

  protected IconButtonWidget createBackButton() {
    return IconButtonWidget.builder(BuiltinIcon.BACK_18, this.modId)
        .onPress((button) -> this.close())
        .messageAndTooltip(Text.translatable(this.modId + ".roundalib.back.tooltip"))
        .build();
  }

  protected IconButtonWidget createResetButton() {
    IconButtonWidget resetButton = IconButtonWidget.builder(BuiltinIcon.UNDO_18, this.modId)
        .onPress((button) -> this.resetToDefault())
        .messageAndTooltip(Text.translatable(this.modId + ".roundalib.reset.tooltip"))
        .build();
    this.subscriptions.add(this.getOption().isPendingDefault.subscribe((isPendingDefault) -> {
      resetButton.active = !isPendingDefault;
    }));
    return resetButton;
  }

  protected void placeActionRow(LinearLayoutWidget actionRow) {
    this.nonPositioningRoot.add(actionRow.alignSelfRight().alignSelfBottom(), (parent, self) -> {
      self.setPosition(parent.getX() + parent.getWidth() - GuiUtil.PADDING,
          parent.getY() + parent.getHeight() - GuiUtil.PADDING
      );
    });
  }
}
