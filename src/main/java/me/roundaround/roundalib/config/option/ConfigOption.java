package me.roundaround.roundalib.config.option;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.function.BiConsumer;

import me.roundaround.roundalib.config.gui.control.Control;
import me.roundaround.roundalib.config.gui.widget.OptionRowWidget;
import me.roundaround.roundalib.config.gui.widget.Widget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public abstract class ConfigOption<D, C extends Widget & Control<?>> {
  private final String id;
  private final Text label;
  private final Optional<Text> comment;
  private final boolean useLabelAsCommentFallback;
  private final D defaultValue;
  private final Queue<BiConsumer<D, D>> valueChangeListeners = new LinkedList<>();

  private D value;
  private D lastSavedValue;

  protected ConfigOption(Builder<D, C> builder) {
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

  public Optional<Text> getComment() {
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

  public void deserialize(Object data) {
    setValue((D) data);
  }

  public Object serialize() {
    return value;
  }

  public final C createAndInitializeControl(OptionRowWidget parent, int top, int left, int height, int width) {
    C control = createControl(parent, top, left, height, width);
    control.init();

    return control;
  }

  public final void subscribeToValueChanges(BiConsumer<D, D> listener) {
    this.valueChangeListeners.add(listener);
  }

  protected abstract C createControl(OptionRowWidget parent, int top, int left, int height, int width);

  public static abstract class Builder<D2, C2 extends Widget & Control<?>> {
    protected String id;
    protected Text label;
    protected Optional<Text> comment = Optional.empty();
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

    public Builder<D2, C2> setComment(String i18nKey) {
      comment = Optional.of(new TranslatableText(i18nKey));
      return this;
    }

    public Builder<D2, C2> setComment(Text comment) {
      this.comment = Optional.of(comment);
      return this;
    }

    public Builder<D2, C2> setUseLabelAsCommentFallback(boolean useLabelAsCommentFallback) {
      this.useLabelAsCommentFallback = useLabelAsCommentFallback;
      return this;
    }

    public abstract ConfigOption<D2, C2> build();
  }
}
