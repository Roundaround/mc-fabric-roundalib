package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ModConfig;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class ConfigOption<D> {
  private final ModConfig modConfig;
  private final String id;
  private final Text label;
  private final D defaultValue;
  private final boolean noGui;
  private final List<String> comment;
  private final boolean useLabelAsCommentFallback;
  private final Consumer<ConfigOption<?>> onUpdate;
  private final HashSet<Consumer<D>> savedValueChangeListeners = new HashSet<>();
  private final HashSet<Consumer<D>> pendingValueChangeListeners = new HashSet<>();

  private boolean isDisabled;
  private D pendingValue;
  private D savedValue;
  private boolean isDirty;
  private boolean isPendingDefault;
  private boolean isDefault;

  protected ConfigOption(AbstractBuilder<D> builder) {
    this(builder.modConfig, builder.id, builder.label, builder.defaultValue, !builder.noGui, builder.comment,
        builder.useLabelAsCommentFallback, builder.onUpdate
    );
  }

  protected ConfigOption(
      ModConfig modConfig,
      String id,
      Text label,
      D defaultValue,
      boolean noGui,
      List<String> comment,
      boolean useLabelAsCommentFallback,
      Consumer<ConfigOption<?>> onUpdate
  ) {
    this.modConfig = modConfig;
    this.id = id;
    this.label = label;
    this.defaultValue = defaultValue;
    this.noGui = !noGui;
    this.comment = comment;
    this.useLabelAsCommentFallback = useLabelAsCommentFallback;
    this.onUpdate = onUpdate;

    this.pendingValue = this.defaultValue;
    this.savedValue = this.defaultValue;

    this.isDirty = false;
    this.isPendingDefault = true;
    this.isDefault = true;
  }

  public ModConfig getModConfig() {
    return this.modConfig;
  }

  public String getModId() {
    return this.modConfig.getModId();
  }

  public String getId() {
    return this.id;
  }

  public boolean shouldShowInConfigScreen() {
    return !this.noGui;
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

  public boolean isDisabled() {
    return this.isDisabled;
  }

  public D getValue() {
    return this.savedValue;
  }

  public D getPendingValue() {
    return this.pendingValue;
  }

  public D getDefaultValue() {
    return this.defaultValue;
  }

  /**
   * Whether this ConfigOption has pending changes to its value.
   */
  public boolean isDirty() {
    return this.isDirty;
  }

  /**
   * Whether this ConfigOption's pending value is equal to the default.
   */
  public boolean isPendingDefault() {
    return this.isPendingDefault;
  }

  /**
   * Whether this ConfigOption's value is different from the default.
   */
  public boolean isDefault() {
    return this.isDefault;
  }

  /**
   * By default, ConfigOptions will simply use {@link Objects#equals(Object, Object)} in change detection. In most
   * cases, this should be sufficient. Override this method to implement your own custom equality check.
   */
  protected boolean areValuesEqual(D a, D b) {
    return Objects.equals(a, b);
  }

  public void setValue(D pendingValue) {
    D prevPendingValue = this.getPendingValue();
    if (this.areValuesEqual(prevPendingValue, pendingValue)) {
      return;
    }

    this.pendingValue = pendingValue;
    this.isDirty = !this.areValuesEqual(this.getPendingValue(), this.getValue());
    this.isPendingDefault = this.areValuesEqual(this.getPendingValue(), this.getDefaultValue());

    this.modConfig.update();

    if (!this.areValuesEqual(prevPendingValue, this.getPendingValue())) {
      this.pendingValueChangeListeners.forEach((listener) -> listener.accept(this.getPendingValue()));
    }
  }

  /**
   * Marks the value as saved and updates any appropriate listeners.
   */
  public void commit() {
    D prevSavedValue = this.getValue();
    this.savedValue = this.getPendingValue();

    this.isDirty = false;
    this.isDefault = !this.areValuesEqual(this.getValue(), this.getDefaultValue());

    if (!this.areValuesEqual(prevSavedValue, this.getValue())) {
      this.savedValueChangeListeners.forEach((listener) -> listener.accept(this.getValue()));
    }
  }

  /**
   * Sets the ConfigOption to its default value using {@link #setValue}, meaning that the update won't take
   * effect until it is committed to file with {@link ModConfig#saveToFile}.
   */
  public void setDefault() {
    this.setValue(this.getDefaultValue());
  }

  /**
   * Reverts the pending ConfigOption back to the committed value.
   */
  public void revert() {
    if (!this.isDirty()) {
      return;
    }
    this.setValue(this.getValue());
  }

  public void setDisabled(boolean isDisabled) {
    this.isDisabled = isDisabled;
  }

  @SuppressWarnings("unchecked")
  public void deserialize(Object data) {
    setValue((D) data);
  }

  public Object serialize() {
    return pendingValue;
  }

  public void update() {
    this.onUpdate.accept(this);
  }

  public final void subscribe(Consumer<D> listener) {
    this.savedValueChangeListeners.add(listener);
  }

  public final void unsubscribe(Consumer<D> listener) {
    this.savedValueChangeListeners.remove(listener);
  }

  public final void subscribePending(Consumer<D> listener) {
    this.pendingValueChangeListeners.add(listener);
  }

  public final void unsubscribePending(Consumer<D> listener) {
    this.pendingValueChangeListeners.remove(listener);
  }

  public static abstract class AbstractBuilder<D> {
    protected final ModConfig modConfig;
    protected final String id;
    protected final Text label;
    protected D defaultValue;
    protected boolean noGui = false;
    protected List<String> comment = List.of();
    protected boolean useLabelAsCommentFallback = true;
    protected Consumer<ConfigOption<?>> onUpdate = (option) -> {
    };

    protected AbstractBuilder(ModConfig modConfig, String id, String labelI18nKey, D defaultValue) {
      this(modConfig, id, Text.translatable(labelI18nKey), defaultValue);
    }

    protected AbstractBuilder(ModConfig modConfig, String id, Text label, D defaultValue) {
      this.modConfig = modConfig;
      this.id = id;
      this.label = label;
      this.defaultValue = defaultValue;
    }

    public AbstractBuilder<D> hideFromConfigScreen() {
      this.noGui = true;
      return this;
    }

    public AbstractBuilder<D> setComment(String comment) {
      this.comment = List.of(comment);
      return this;
    }

    public AbstractBuilder<D> setComment(String... comment) {
      this.comment = List.of(comment);
      return this;
    }

    public AbstractBuilder<D> setComment(Collection<String> comment) {
      this.comment = List.copyOf(comment);
      return this;
    }

    public AbstractBuilder<D> setUseLabelAsCommentFallback(boolean useLabelAsCommentFallback) {
      this.useLabelAsCommentFallback = useLabelAsCommentFallback;
      return this;
    }

    public AbstractBuilder<D> onUpdate(Consumer<ConfigOption<?>> onUpdate) {
      this.onUpdate = onUpdate;
      return this;
    }

    public abstract ConfigOption<D> build();
  }
}
