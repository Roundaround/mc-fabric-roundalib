package me.roundaround.roundalib.config.option;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.function.BiConsumer;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public abstract class ConfigOption<D> {
  private final String id;
  private final Text label;
  private final Optional<String> comment;
  private final boolean useLabelAsCommentFallback;
  private final D defaultValue;
  private final Queue<BiConsumer<D, D>> valueChangeListeners = new LinkedList<>();

  private D value;
  private D lastSavedValue;

  protected ConfigOption(Builder<D> builder) {
    id = builder.id;
    label = builder.label;
    comment = builder.comment;
    useLabelAsCommentFallback = builder.useLabelAsCommentFallback;
    defaultValue = builder.defaultValue;
    value = defaultValue;
  }

  public String getId() {
    return id;
  }

  public Text getLabel() {
    return label;
  }

  public Optional<String> getComment() {
    return comment;
  }

  public boolean getUseLabelAsCommentFallback() {
    return useLabelAsCommentFallback;
  }

  public D getValue() {
    return value;
  }

  public void setValue(D value) {
    D prev = this.value;
    this.value = value;
    valueChangeListeners.forEach((listener) -> listener.accept(prev, value));
  }

  public void resetToDefault() {
    setValue(defaultValue);
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

  @SuppressWarnings("unchecked")
  public void deserialize(Object data) {
    setValue((D) data);
  }

  public Object serialize() {
    return value;
  }

  public final void subscribeToValueChanges(BiConsumer<D, D> listener) {
    this.valueChangeListeners.add(listener);
  }

  public static abstract class Builder<D2> {
    protected String id;
    protected Text label;
    protected Optional<String> comment = Optional.empty();
    protected boolean useLabelAsCommentFallback = true;
    protected D2 defaultValue;

    protected Builder(String id, String labelI18nKey, D2 defaultValue) {
      this(id, new TranslatableText(labelI18nKey), defaultValue);
    }

    protected Builder(String id, Text label, D2 defaultValue) {
      this.id = id;
      this.label = label;
      this.defaultValue = defaultValue;
    }

    public Builder<D2> setComment(String comment) {
      // TODO: Allow passing an array of strings for multi-line comments, prefix
      // each line with a space for nice formatting
      this.comment = Optional.of(comment);
      return this;
    }

    public Builder<D2> setUseLabelAsCommentFallback(boolean useLabelAsCommentFallback) {
      this.useLabelAsCommentFallback = useLabelAsCommentFallback;
      return this;
    }

    public abstract ConfigOption<D2> build();
  }
}
