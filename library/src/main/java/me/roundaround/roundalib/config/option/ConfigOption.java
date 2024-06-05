package me.roundaround.roundalib.config.option;

import net.minecraft.text.Text;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public abstract class ConfigOption<D, B extends ConfigOption.AbstractBuilder<D, B>> {
  private final String modId;
  private final String id;
  private final Text label;
  private final boolean showInConfigScreen;
  private final List<String> comment;
  private final boolean useLabelAsCommentFallback;
  private final Supplier<Boolean> disabledSupplier;
  private final ValueChangeListeners<D> valueChangeListeners = new ValueChangeListeners<>();
  private final List<ConfigOption<?, ?>> dependencies;

  private D defaultValue;
  private D value;
  private D lastSavedValue;

  protected ConfigOption(B builder) {
    this.modId = builder.modId;
    this.id = builder.id;
    this.label = builder.label;
    this.defaultValue = builder.defaultValue;
    this.showInConfigScreen = builder.showInConfigScreen;
    this.comment = builder.comment;
    this.useLabelAsCommentFallback = builder.useLabelAsCommentFallback;
    this.disabledSupplier = builder.disabledSupplier;
    this.dependencies = builder.dependencies;
    this.value = defaultValue;

    this.dependencies.forEach(dependency -> dependency.valueChangeListeners.add(null, this::dependencyChanged));
  }

  protected ConfigOption(ConfigOption<D, B> other) {
    this.modId = other.modId;
    this.id = other.id;
    this.label = other.label;
    this.defaultValue = other.defaultValue;
    this.showInConfigScreen = other.showInConfigScreen;
    this.comment = other.comment;
    this.useLabelAsCommentFallback = other.useLabelAsCommentFallback;
    this.disabledSupplier = other.disabledSupplier;
    this.dependencies = other.dependencies;
    this.value = other.value;
    this.lastSavedValue = other.lastSavedValue;
  }

  public String getModId() {
    return this.modId;
  }

  public String getId() {
    return this.id;
  }

  public boolean shouldShowInConfigScreen() {
    return this.showInConfigScreen;
  }

  public Text getLabel() {
    return this.label;
  }

  public List<String> getComment() {
    return this.comment;
  }

  public boolean getUseLabelAsCommentFallback() {
    return this.useLabelAsCommentFallback;
  }

  public D getValue() {
    return this.value;
  }

  public void setValue(D value) {
    D prev = this.value;
    this.value = value;
    this.valueChangeListeners.invoke(prev, value);
  }

  public D getDefault() {
    return this.defaultValue;
  }

  public void setDefault(D defaultValue) {
    this.defaultValue = defaultValue;
  }

  public void resetToDefault() {
    this.setValue(this.defaultValue);
  }

  public void markValueAsSaved() {
    this.lastSavedValue = value;
  }

  public boolean isDirty() {
    return !this.value.equals(this.lastSavedValue);
  }

  public boolean isModified() {
    return !this.value.equals(this.defaultValue);
  }

  public boolean isDisabled() {
    return this.disabledSupplier.get();
  }

  @SuppressWarnings("unchecked")
  public void deserialize(Object data) {
    setValue((D) data);
  }

  public Object serialize() {
    return value;
  }

  public final void subscribeToValueChanges(Integer hashCode, BiConsumer<D, D> listener) {
    this.valueChangeListeners.add(hashCode, listener);
  }

  public final void clearValueChangeListeners(Integer hashCode) {
    this.valueChangeListeners.clear(hashCode);
  }

  protected void dependencyChanged(Object prev, Object curr) {
    this.valueChangeListeners.invoke(this.getValue(), this.getValue());
  }

  public abstract ConfigOption<D, B> copy();

  public final ConfigOption<D, B> createWorkingCopy() {
    return this.copy();
  }

  public static abstract class AbstractBuilder<D, B extends AbstractBuilder<D, B>> {
    protected final String modId;
    protected final String id;
    protected final Text label;
    protected D defaultValue;
    protected boolean showInConfigScreen = true;
    protected List<String> comment = List.of();
    protected boolean useLabelAsCommentFallback = true;
    protected Supplier<Boolean> disabledSupplier = () -> false;
    protected List<ConfigOption<?, ?>> dependencies = List.of();

    protected AbstractBuilder(String modId, String id, String labelI18nKey, D defaultValue) {
      this(modId, id, Text.translatable(labelI18nKey), defaultValue);
    }

    protected AbstractBuilder(String modId, String id, Text label, D defaultValue) {
      this.modId = modId;
      this.id = id;
      this.label = label;
      this.defaultValue = defaultValue;
    }

    @SuppressWarnings("unchecked")
    public B hideFromConfigScreen() {
      this.showInConfigScreen = false;
      return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B setComment(String comment) {
      this.comment = List.of(comment);
      return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B setComment(String... comment) {
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

    @SuppressWarnings("unchecked")
    public B dependsOn(ConfigOption<?, ?>... dependencies) {
      this.dependencies = List.of(dependencies);
      return (B) this;
    }

    public abstract ConfigOption<D, B> build();
  }

  private static class ValueChangeListeners<D> {
    private final HashMap<Integer, Queue<BiConsumer<D, D>>> listeners = new HashMap<>();

    public void add(Integer hashCode, BiConsumer<D, D> listener) {
      if (!this.listeners.containsKey(hashCode)) {
        this.listeners.put(hashCode, new LinkedList<>());
      }
      this.listeners.get(hashCode).add(listener);
    }

    public void remove(Integer hashCode, BiConsumer<D, D> listener) {
      if (!this.listeners.containsKey(hashCode)) {
        return;
      }
      this.listeners.get(hashCode).remove(listener);
    }

    public void clear(Integer hashCode) {
      this.listeners.remove(hashCode);
    }

    public void invoke(D prev, D curr) {
      this.listeners.keySet().forEach((hashCode) -> invoke(hashCode, prev, curr));
    }

    public void invoke(Integer hashCode, D prev, D curr) {
      if (!this.listeners.containsKey(hashCode)) {
        return;
      }
      this.listeners.get(hashCode).forEach((listener) -> listener.accept(prev, curr));
    }
  }
}
