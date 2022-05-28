package me.roundaround.roundalib.config.option;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.BiConsumer;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.gui.control.Control;
import me.roundaround.roundalib.config.gui.widget.Widget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public abstract class ConfigOption<D, C extends Widget & Control<?>> {
  private final String id;
  private final String labelI18nKey;
  private final D defaultValue;
  private final Queue<BiConsumer<D, D>> valueChangeListeners = new LinkedList<>();

  private D value;
  private D lastSavedValue;

  public ConfigOption(String id, String labelI18nKey, D defaultValue) {
    this.id = id;
    this.labelI18nKey = labelI18nKey;
    this.defaultValue = defaultValue;
    value = defaultValue;
  }

  public String getId() {
    return id;
  }

  public Text getLabel() {
    return new TranslatableText(labelI18nKey);
  }

  public D getValue() {
    return value;
  }

  public void setValue(D value) {
    D prev = this.value;
    this.value = value;
    valueChangeListeners.forEach((listener) -> listener.accept(prev, this.value));
  }

  public void resetToDefault() {
    this.setValue(this.defaultValue);
  }

  public void markValueAsSaved() {
    lastSavedValue = value;
  }

  public boolean isDirty() {
    return !value.equals(lastSavedValue);
  }

  public boolean isModified() {
    return !value.equals(defaultValue);
  }

  public void deserialize(Object data) {
    setValue((D) data);
  }

  public Object serialize() {
    return value;
  }

  public final C createAndInitializeControl(OptionRow parent, int top, int left, int height, int width) {
    C control = createControl(parent, top, left, height, width);
    control.init();

    return control;
  }

  public final void subscribeToValueChanges(BiConsumer<D, D> listener) {
    this.valueChangeListeners.add(listener);
  }

  protected abstract C createControl(OptionRow parent, int top, int left, int height, int width);
}
