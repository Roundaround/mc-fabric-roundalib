package me.roundaround.roundalib.config.option;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import net.minecraft.text.Text;

public abstract class ConfigOption<D, B extends ConfigOption.Builder<D, B>> {
  private final String id;
  private final boolean showInConfigScreen;
  private final Text label;
  private final List<String> comment;
  private final boolean useLabelAsCommentFallback;
  private final D defaultValue;
  private final Supplier<Boolean> disabledSupplier;
  private final Queue<BiConsumer<D, D>> valueChangeListeners = new LinkedList<>();

  private D value;
  private D lastSavedValue;

  // TODO: Create library for registering custom commands
  // TODO: Create commands for managing the config

  protected ConfigOption(B builder) {
    id = builder.id;
    showInConfigScreen = builder.showInConfigScreen;
    label = builder.label;
    comment = builder.comment;
    useLabelAsCommentFallback = builder.useLabelAsCommentFallback;
    defaultValue = builder.defaultValue;
    disabledSupplier = builder.disabledSupplier;
    value = defaultValue;
  }

  protected ConfigOption(ConfigOption<D, B> other) {
    id = other.id;
    showInConfigScreen = other.showInConfigScreen;
    label = other.label;
    comment = other.comment;
    useLabelAsCommentFallback = other.useLabelAsCommentFallback;
    defaultValue = other.defaultValue;
    disabledSupplier = other.disabledSupplier;
    value = other.value;
  }

  public String getId() {
    return id;
  }

  public boolean shouldShowInConfigScreen() {
    return showInConfigScreen;
  }

  public Text getLabel() {
    return label;
  }

  public List<String> getComment() {
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

  public D getDefault() {
    return defaultValue;
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

  public boolean isDisabled() {
    return disabledSupplier.get();
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

  public abstract ConfigOption<D, B> copy();

  public static abstract class Builder<D2, B extends Builder<D2, B>> {
    protected String id;
    protected boolean showInConfigScreen = true;
    protected Text label;
    protected List<String> comment = List.of();
    protected boolean useLabelAsCommentFallback = true;
    protected D2 defaultValue;
    protected Supplier<Boolean> disabledSupplier = () -> false;

    protected Builder(String id, String labelI18nKey, D2 defaultValue) {
      this(id, Text.translatable(labelI18nKey), defaultValue);
    }

    protected Builder(String id, Text label, D2 defaultValue) {
      this.id = id;
      this.label = label;
      this.defaultValue = defaultValue;
    }

    @SuppressWarnings("unchecked")
    public B hideFromConfigScreen() {
      showInConfigScreen = false;
      return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B setComment(String comment) {
      this.comment = List.of(comment);
      return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B setComment(String...comment) {
      this.comment = List.of(comment);
      return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B setComment(Collection<String> comment) {
      this.comment = List.copyOf(comment);
      return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B setUseLabelAsCommentFallback(boolean useLabelAsCommentFallback) {
      this.useLabelAsCommentFallback = useLabelAsCommentFallback;
      return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B setDisabledSupplier(Supplier<Boolean> disabledSupplier) {
      this.disabledSupplier = disabledSupplier;
      return (B) this;
    }

    public abstract ConfigOption<D2, B> build();
  }
}
