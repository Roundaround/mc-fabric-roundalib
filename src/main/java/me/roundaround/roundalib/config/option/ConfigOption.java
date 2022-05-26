package me.roundaround.roundalib.config.option;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.BiConsumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.gui.control.ControlWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public abstract class ConfigOption<T> {
  private final String id;
  private final String labelI18nKey;
  private final T defaultValue;
  private final ControlFactory<T> controlFactory;
  private final Queue<BiConsumer<T, T>> valueChangeListeners = new LinkedList<>();

  private T value;
  private T lastSavedValue;

  public ConfigOption(String id, String labelI18nKey, T defaultValue) {
    this.id = id;
    this.labelI18nKey = labelI18nKey;
    this.defaultValue = defaultValue;
    this.value = defaultValue;
    this.controlFactory = this.getDefaultControlFactory();
  }

  public ConfigOption(String id, String labelI18nKey, T defaultValue, ControlFactory<T> controlFactory) {
    this.id = id;
    this.labelI18nKey = labelI18nKey;
    this.defaultValue = defaultValue;
    this.value = defaultValue;
    this.controlFactory = controlFactory;
  }

  public String getId() {
    return this.id;
  }

  public Text getLabel() {
    return new TranslatableText(this.labelI18nKey);
  }

  public T getValue() {
    return this.value;
  }

  public void setValue(T value) {
    T prev = this.value;
    this.value = value;
    this.valueChangeListeners.forEach((listener) -> listener.accept(prev, this.value));
  }

  public void resetToDefault() {
    this.setValue(this.defaultValue);
  }

  public void markValueAsSaved() {
    this.lastSavedValue = this.value;
  }

  public boolean isDirty() {
    return !this.value.equals(this.lastSavedValue);
  }

  public boolean isModified() {
    return !this.value.equals(this.defaultValue);
  }

  public void readFromJsonRoot(JsonObject root) {
    this.setValue(this.deserializeFromJson(root.get(this.id)));
    this.lastSavedValue = this.value;
  }

  public void writeToJsonRoot(JsonObject root) {
    root.add(this.id, this.serializeToJson());
    this.markValueAsSaved();
  }

  public final ControlWidget<T> createControl(OptionRow parent, int top, int left, int height, int width) {
    ControlWidget<T> control = this.controlFactory.apply(parent, top, left, height, width);
    control.setConfigOption(this);
    control.init();

    return control;
  }

  public final void subscribeToValueChanges(BiConsumer<T, T> listener) {
    this.valueChangeListeners.add(listener);
  }

  public abstract T deserializeFromJson(JsonElement data);

  public abstract JsonElement serializeToJson();

  public abstract ControlFactory<T> getDefaultControlFactory();
}
