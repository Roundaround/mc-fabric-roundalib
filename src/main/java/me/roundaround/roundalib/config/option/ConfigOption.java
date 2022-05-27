package me.roundaround.roundalib.config.option;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.BiConsumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.roundaround.roundalib.config.gui.OptionRow;
import me.roundaround.roundalib.config.gui.Widget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public abstract class ConfigOption<D, C extends Widget> {
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
    this.value = defaultValue;
  }

  public String getId() {
    return this.id;
  }

  public Text getLabel() {
    return new TranslatableText(this.labelI18nKey);
  }

  public D getValue() {
    return this.value;
  }

  public void setValue(D value) {
    D prev = this.value;
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

  public final C createAndInitializeControl(OptionRow parent, int top, int left, int height, int width) {
    C control = createControl(parent, top, left, height, width);
    control.init();

    return control;
  }

  public final void subscribeToValueChanges(BiConsumer<D, D> listener) {
    this.valueChangeListeners.add(listener);
  }

  protected abstract C createControl(OptionRow parent, int top, int left, int height, int width);

  public abstract D deserializeFromJson(JsonElement data);

  public abstract JsonElement serializeToJson();
}
