package me.roundaround.roundalib.config.gui.control;

import java.util.List;
import java.util.stream.Collectors;

import me.roundaround.roundalib.config.gui.GuiUtil;
import me.roundaround.roundalib.config.gui.SelectableElement;
import me.roundaround.roundalib.config.gui.widget.IconButtonWidget;
import me.roundaround.roundalib.config.gui.widget.IntStepButtonWidget;
import me.roundaround.roundalib.config.gui.widget.OptionRowWidget;
import me.roundaround.roundalib.config.gui.widget.TextFieldWidget;
import me.roundaround.roundalib.config.gui.widget.Widget;
import me.roundaround.roundalib.config.option.IntConfigOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class IntInputControl extends AbstractControlWidget<IntConfigOption> {
  private TextFieldWidget textBox;
  private IntStepButtonWidget incrementButton;
  private IntStepButtonWidget decrementButton;

  public IntInputControl(
      IntConfigOption configOption,
      OptionRowWidget parent,
      int top,
      int left,
      int height,
      int width) {
    super(configOption, parent, top, left, height, width);

    textBox = new TextFieldWidget(
        this,
        GuiUtil.getTextRenderer(),
        left + 1,
        top + 1,
        width - 2 - (configOption.showStepButtons() ? 10 : 0),
        height - 2,
        configOption.getLabel()) {
      @Override
      protected boolean isCharAllowed(char chr) {
        return Character.isDigit(chr) || Character.valueOf('-').equals(chr);
      }
    };
    textBox.setMaxLength(12);
    textBox.setChangedListener(this::onTextFieldValueChange);
    textBox.setText(configOption.getValue().toString());
    textBox.setFocusChangedListener((textBoxFocused) -> {
      if (textBoxFocused) {
        getConfigScreen().declareFocused(textBox);
      }
    });

    if (configOption.showStepButtons()) {
      incrementButton = new IntStepButtonWidget(this, true, top, right - IconButtonWidget.WIDTH_SMALL + 1);
      decrementButton = new IntStepButtonWidget(this, false, bottom - IconButtonWidget.HEIGHT_SMALL + 1,
          right - IconButtonWidget.WIDTH_SMALL + 1);
    }

    configOption.subscribeToValueChanges(this::onConfigValueChange);
  }

  @Override
  public List<SelectableElement> getSelectableElements() {
    if (configOption.showStepButtons()) {
      return List.of(textBox, incrementButton, decrementButton);
    }
    return List.of(textBox);
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    super.render(matrices, mouseX, mouseY, delta);
    textBox.render(matrices, mouseX, mouseY, delta);
    if (configOption.showStepButtons()) {
      incrementButton.render(matrices, mouseX, mouseY, delta);
      decrementButton.render(matrices, mouseX, mouseY, delta);
    }
  }

  @Override
  public void tick() {
    textBox.tick();
    if (configOption.showStepButtons()) {
      incrementButton.tick();
      decrementButton.tick();
    }
  }

  @Override
  public List<Text> getTooltip(int mouseX, int mouseY, float delta) {
    return getSelectableElements().stream()
        .map((element) -> {
          if (element instanceof Widget) {
            return ((Widget) element).getTooltip(mouseX, mouseY, delta);
          }
          return null;
        })
        .filter((tooltip) -> tooltip != null)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    return getSelectableElements().stream().anyMatch((element) -> {
      return element.keyPressed(keyCode, scanCode, modifiers);
    });
  }

  @Override
  public boolean charTyped(char chr, int modifiers) {
    return getSelectableElements().stream().anyMatch((element) -> {
      return element.charTyped(chr, modifiers);
    });
  }

  @Override
  public boolean onMouseClicked(double mouseX, double mouseY, int button) {
    return getSelectableElements().stream().anyMatch((element) -> {
      return element.mouseClicked(mouseX, mouseY, button);
    });
  }

  @Override
  public boolean onMouseReleased(double mouseX, double mouseY, int button) {
    return getSelectableElements().stream().anyMatch((element) -> {
      return element.mouseReleased(mouseX, mouseY, button);
    });
  }

  @Override
  public boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
    return getSelectableElements().stream().anyMatch((element) -> {
      return element.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    });
  }

  private void onTextFieldValueChange(String value) {
    try {
      int parsed = Integer.parseInt(value);
      if (configOption.validateInput(parsed)) {
        configOption.setValue(parsed);
        markValid();
      } else {
        markInvalid();
      }
    } catch (Exception e) {
      markInvalid();
    }
  }

  private void onConfigValueChange(Integer prev, Integer curr) {
    if (!curr.toString().equals(textBox.getText())) {
      textBox.setText(curr.toString());
    }
  }

  @Override
  public void markInvalid() {
    textBox.setEditableColor(GuiUtil.ERROR_COLOR);
    super.markInvalid();
  }

  @Override
  public void markValid() {
    textBox.setEditableColor(GuiUtil.LABEL_COLOR);
    super.markValid();
  }

  @Override
  public void moveTop(int top) {
    super.moveTop(top);
    textBox.y = top + 1;

    if (configOption.showStepButtons()) {
      incrementButton.moveTop(top);
      decrementButton.moveTop(bottom - IconButtonWidget.HEIGHT_SMALL + 1);
    }
  }
}
