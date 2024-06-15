package me.roundaround.roundalib.client.gui.screen;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.config.PendingValueListener;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public abstract class ConfigOptionSubScreen<D, O extends ConfigOption<D>> extends Screen implements PendingValueListener<D> {
  protected final Screen parent;
  protected final O option;
  protected final String modId;
  protected final Text helpShortText;
  protected final Text helpCloseText;
  protected final Text helpResetText;
  protected final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
  protected final SimplePositioningWidget footer = new SimplePositioningWidget();
  protected final SimplePositioningWidget body = new SimplePositioningWidget();
  protected final DirectionalLayoutWidget buttonRow = DirectionalLayoutWidget.horizontal().spacing(GuiUtil.PADDING);

  protected IconButtonWidget resetButton;

  protected ConfigOptionSubScreen(Text title, Screen parent, O option) {
    super(title);
    this.parent = parent;
    this.option = option;
    this.modId = option.getModId();

    this.helpShortText = Text.translatable(this.modId + ".roundalib.help.short");
    this.helpCloseText = Text.translatable(this.modId + ".roundalib.help.close");
    this.helpResetText = MinecraftClient.IS_SYSTEM_MAC ?
        Text.translatable(this.modId + ".roundalib.help.reset.mac") :
        Text.translatable(this.modId + ".roundalib.help.reset.win");

    this.footer.getMainPositioner()
        .alignBottom()
        .alignRight()
        .marginBottom(GuiUtil.PADDING)
        .marginRight(GuiUtil.PADDING);
  }

  @Override
  protected void init() {
    this.initHeader();
    this.initBody();
    this.initFooter();

    this.layout.forEachChild(this::addDrawableChild);
    this.initTabNavigation();

    this.getOption().subscribePending(this);
    this.onPendingValueChange(this.getValue());
  }

  protected void initHeader() {
    this.layout.addHeader(this.title, this.textRenderer);
  }

  protected void initBody() {
    this.layout.addBody(this.body);
  }

  protected void initFooter() {
    this.layout.addFooter(this.footer);
    this.footer.add(this.buttonRow);
    this.buttonRow.add(IconButtonWidget.builder(IconButtonWidget.BuiltinIcon.BACK_18, this.modId)
        .onPress((button) -> this.close())
        .messageAndTooltip(Text.translatable(this.modId + ".roundalib.back.tooltip"))
        .build());
    this.resetButton = this.buttonRow.add(IconButtonWidget.builder(IconButtonWidget.BuiltinIcon.UNDO_18, this.modId)
        .onPress((button) -> this.resetToDefault())
        .messageAndTooltip(Text.translatable(this.modId + ".roundalib.reset.tooltip"))
        .build());
  }

  @Override
  protected void initTabNavigation() {
    this.prepLayoutForRefresh();
    this.layout.refreshPositions();
  }

  protected void prepLayoutForRefresh() {
    this.footer.setDimensions(this.layout.getWidth(), this.layout.getFooterHeight());
    this.body.setDimensions(this.layout.getWidth(), this.layout.getContentHeight());
  }

  @Override
  public void close() {
    this.getOption().unsubscribePending(this);
    if (this.client == null) {
      return;
    }
    this.client.setScreen(this.parent);
  }

  @Override
  public void onPendingValueChange(D value) {
    this.resetButton.active = !this.isDefault();
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    switch (keyCode) {
      case GLFW.GLFW_KEY_ESCAPE -> {
        this.close();
        return true;
      }
      case GLFW.GLFW_KEY_R -> {
        if (Screen.hasControlDown()) {
          this.resetToDefault();
          return true;
        }
      }
    }

    return super.keyPressed(keyCode, scanCode, modifiers);
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    super.render(context, mouseX, mouseY, delta);
    this.renderHelp(context, mouseX, mouseY, delta);
  }

  protected void renderHelp(DrawContext context, int mouseX, int mouseY, float delta) {
    if (hasShiftDown()) {
      this.renderHelpExpanded(context, mouseX, mouseY, delta);
    } else {
      this.renderHelpPrompt(context, mouseX, mouseY, delta);
    }
  }

  protected void renderHelpPrompt(
      DrawContext context, int mouseX, int mouseY, float delta
  ) {
    this.renderHelpLines(context, this.getHelpShort(mouseX, mouseY, delta));
  }

  protected void renderHelpExpanded(
      DrawContext context, int mouseX, int mouseY, float delta
  ) {
    this.renderHelpLines(context, this.getHelpLong(mouseX, mouseY, delta));
  }

  private void renderHelpLines(DrawContext context, List<Text> lines) {
    int startingOffset =
        this.height - 4 - this.textRenderer.fontHeight - (lines.size() - 1) * (this.textRenderer.fontHeight + 2);

    for (int i = 0; i < lines.size(); i++) {
      context.drawTextWithShadow(this.textRenderer, lines.get(i), 4,
          startingOffset + i * (this.textRenderer.fontHeight + 2), GuiUtil.LABEL_COLOR
      );
    }
  }

  protected List<Text> getHelpShort(int mouseX, int mouseY, float delta) {
    return List.of(this.helpShortText);
  }

  protected List<Text> getHelpLong(int mouseX, int mouseY, float delta) {
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

  protected boolean isDefault() {
    return this.getOption().isPendingDefault();
  }

  protected boolean isDirty() {
    return this.getOption().isDirty();
  }
}
