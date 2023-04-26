package me.roundaround.roundalib.config.option;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import me.roundaround.roundalib.config.ModConfig;
import net.minecraft.text.Text;

public abstract class ConfigOption<D, B extends ConfigOption.AbstractBuilder<D, B>> {
  private final ModConfig config;
  private final String id;
  private final Text label;
  private final boolean showInConfigScreen;
  private final List<String> comment;
  private final boolean useLabelAsCommentFallback;
  private final Supplier<Boolean> disabledSupplier;
  private final Queue<BiConsumer<D, D>> valueChangeListeners = new LinkedList<>();

  private D defaultValue;
  private D value;
  private D lastSavedValue;

  protected ConfigOption(B builder) {
    this.config = builder.config;
    this.id = builder.id;
    this.label = builder.label;
    this.defaultValue = builder.defaultValue;
    this.showInConfigScreen = builder.showInConfigScreen;
    this.comment = builder.comment;
    this.useLabelAsCommentFallback = builder.useLabelAsCommentFallback;
    this.disabledSupplier = builder.disabledSupplier;
    this.value = defaultValue;
  }

  protected ConfigOption(ConfigOption<D, B> other) {
    this.config = other.config;
    this.id = other.id;
    this.label = other.label;
    this.defaultValue = other.defaultValue;
    this.showInConfigScreen = other.showInConfigScreen;
    this.comment = other.comment;
    this.useLabelAsCommentFallback = other.useLabelAsCommentFallback;
    this.disabledSupplier = other.disabledSupplier;
    this.value = other.value;
  }

  public ModConfig getConfig() {
    return this.config;
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
    this.valueChangeListeners.forEach((listener) -> listener.accept(prev, value));
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

  public final void subscribeToValueChanges(BiConsumer<D, D> listener) {
    this.valueChangeListeners.add(listener);
  }

  public final void clearValueChangeListeners() {
    this.valueChangeListeners.clear();
  }

  public abstract ConfigOption<D, B> copy();

  public final ConfigOption<D, B> createWorkingCopy() {
    ConfigOption<D, B> workingCopy = this.copy();
    workingCopy.setDefault(this.value);
    return workingCopy;
  }

  public static abstract class AbstractBuilder<D, B extends AbstractBuilder<D, B>> {
    protected final ModConfig config;
    protected final String id;
    protected final Text label;
    protected D defaultValue;
    protected boolean showInConfigScreen = true;
    protected List<String> comment = List.of();
    protected boolean useLabelAsCommentFallback = true;
    protected Supplier<Boolean> disabledSupplier = () -> false;

    protected AbstractBuilder(ModConfig config, String id, String labelI18nKey, D defaultValue) {
      this(config, id, Text.translatable(labelI18nKey), defaultValue);
    }

    protected AbstractBuilder(ModConfig config, String id, Text label, D defaultValue) {
      this.config = config;
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

    public abstract ConfigOption<D, B> build();
  }
}
