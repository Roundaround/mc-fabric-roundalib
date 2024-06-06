package me.roundaround.roundalib.client.gui.screen;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.LabelElement;
import me.roundaround.roundalib.client.gui.RoundaLibIconButtons;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public abstract class ConfigOptionSubScreen<D, O extends ConfigOption<D>> extends Screen {
  protected final Screen parent;
  protected final O option;
  protected final String modId;
  protected final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);

  protected ConfigOptionSubScreen(Text title, Screen parent, O option) {
    super(title);
    this.parent = parent;
    this.option = option;
    this.modId = option.getModId();

    this.option.subscribePending(this::onValueChanged);
  }

  @Override
  protected void init() {
    this.layout.addHeader(this.title, this.textRenderer);

    DirectionalLayoutWidget row = DirectionalLayoutWidget.horizontal().spacing(8);
    this.layout.addFooter(row);

    row.add(ButtonWidget.builder(ScreenTexts.DONE, this::close).size(150, 20).build());
    row.add(RoundaLibIconButtons.resetButton(0, 0, this.option));

    this.layout.forEachChild(this::addDrawableChild);
    this.initTabNavigation();
  }

  @Override
  protected void initTabNavigation() {
    this.layout.refreshPositions();
  }

  protected void close(ButtonWidget button) {
    this.close();
  }

  @Override
  public void close() {
    this.option.unsubscribePending(this::onValueChanged);
    if (this.client == null) {
      return;
    }
    this.client.setScreen(this.parent);
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
    this.renderHelpLines(context, getHelpShort(mouseX, mouseY, delta));
  }

  protected void renderHelpExpanded(
      DrawContext context, int mouseX, int mouseY, float delta
  ) {
    this.renderHelpLines(context, getHelpLong(mouseX, mouseY, delta));
  }

  private void renderHelpLines(DrawContext context, List<Text> lines) {
    this.renderHelpLines(context, lines, false);
  }

  private void renderHelpLines(DrawContext context, List<Text> lines, boolean offsetForIcon) {
    int startingOffset = height - 4 - textRenderer.fontHeight - (lines.size() - 1) * (textRenderer.fontHeight + 2);

    for (int i = 0; i < lines.size(); i++) {
      context.drawTextWithShadow(textRenderer, lines.get(i), 4, startingOffset + i * (textRenderer.fontHeight + 2),
          GuiUtil.LABEL_COLOR
      );
    }
  }

  protected List<Text> getHelpShort(int mouseX, int mouseY, float delta) {
    return List.of(Text.translatable(this.modId + ".roundalib.help.short"));
  }

  protected List<Text> getHelpLong(int mouseX, int mouseY, float delta) {
    return List.of(
        Text.translatable(this.modId + ".roundalib.help.cancel"), (MinecraftClient.IS_SYSTEM_MAC ?
            Text.translatable(this.modId + ".roundalib.help.save.mac") :
            Text.translatable(this.modId + ".roundalib.help.save.win")), (MinecraftClient.IS_SYSTEM_MAC ?
            Text.translatable(this.modId + ".roundalib.help.reset.mac") :
            Text.translatable(this.modId + ".roundalib.help.reset.win")));
  }

  protected void setValue(D value) {
    this.option.setValue(value);
  }

  protected D getValue() {
    return this.option.getPendingValue();
  }

  protected void resetToDefault() {
    this.option.setDefault();
  }

  protected boolean isDefault() {
    return this.option.isPendingDefault();
  }

  protected boolean isDirty() {
    return this.option.isDirty();
  }

  protected void onValueChanged(D value) {
  }
}
