package me.roundaround.roundalib.client.gui.screen;

import me.roundaround.roundalib.client.gui.GuiUtil;
import me.roundaround.roundalib.client.gui.LabelElement;
import me.roundaround.roundalib.client.gui.RoundaLibIconButtons;
import me.roundaround.roundalib.config.option.ConfigOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public abstract class ConfigOptionSubScreen<D, O extends ConfigOption<D>> extends Screen {
  protected final Screen parent;
  protected final O configOption;
  protected final O workingCopy;
  protected final String modId;

  @SuppressWarnings("unchecked")
  protected ConfigOptionSubScreen(Text title, Screen parent, O configOption) {
    super(title);
    this.parent = parent;
    this.configOption = configOption;
    this.workingCopy = (O) configOption.createWorkingCopy();
    this.modId = configOption.getModId();

    this.workingCopy.subscribeToValueChanges(this.hashCode(), this::onValueChanged);
  }

  @Override
  protected void init() {
    this.addDrawable(LabelElement.screenTitle(this.textRenderer, this.title, this));

    this.addDrawableChild(
        RoundaLibIconButtons.resetButton(this.width - 3 * (GuiUtil.PADDING + RoundaLibIconButtons.SIZE_M),
            this.height - GuiUtil.PADDING - RoundaLibIconButtons.SIZE_M, this.workingCopy
        ));

    this.addDrawableChild(
        RoundaLibIconButtons.discardButton(this.width - 2 * (GuiUtil.PADDING + RoundaLibIconButtons.SIZE_M),
            this.height - GuiUtil.PADDING - RoundaLibIconButtons.SIZE_M, this.configOption.getModId(),
            (button) -> this.discardAndExit()
        ));

    this.addDrawableChild(RoundaLibIconButtons.saveButton(this.width - GuiUtil.PADDING - RoundaLibIconButtons.SIZE_M,
        this.height - GuiUtil.PADDING - RoundaLibIconButtons.SIZE_M, this.configOption.getModId(),
        (button) -> this.saveAndExit()
    ));
  }

  @Override
  public void close() {
    this.workingCopy.clearValueChangeListeners(this.hashCode());
    if (this.client == null) {
      return;
    }
    this.client.setScreen(this.parent);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    switch (keyCode) {
      case GLFW.GLFW_KEY_ESCAPE:
        this.discardAndExit();
        return true;
      case GLFW.GLFW_KEY_S:
        if (Screen.hasControlDown()) {
          this.saveAndExit();
          return true;
        }
      case GLFW.GLFW_KEY_R:
        if (Screen.hasControlDown()) {
          this.resetToDefault();
          return true;
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
    this.workingCopy.setValue(value);
  }

  protected D getValue() {
    return this.workingCopy.getValue();
  }

  protected void resetToDefault() {
    this.workingCopy.resetToDefault();
  }

  protected boolean isDirty() {
    return this.workingCopy.isDirty();
  }

  protected void commitValueToConfig() {
    this.configOption.setValue(this.getValue());
  }

  protected void discardAndExit() {
    this.close();
  }

  protected void saveAndExit() {
    this.commitValueToConfig();
    this.close();
  }

  protected void onValueChanged(D prev, D curr) {
  }
}
