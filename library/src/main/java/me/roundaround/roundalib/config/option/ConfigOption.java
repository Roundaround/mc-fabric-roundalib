package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.PendingValueListener;
import me.roundaround.roundalib.config.SavedValueListener;
import me.roundaround.roundalib.config.manage.ModConfig;
import me.roundaround.roundalib.config.manage.ModConfigImpl;
import me.roundaround.roundalib.config.panic.IllegalArgumentPanic;
import me.roundaround.roundalib.config.panic.Panic;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Base container class for a mod configuration option.
 *
 * @param <D> The data type represented by this config option. Must be immutable.
 */
public abstract class ConfigOption<D> {
  private final ConfigPath path;
  private final Text label;
  private final D defaultValue;
  private final boolean noGui;
  private final Function<D, String> toStringFunction;
  private final List<String> comment;
  private final List<Validator<D>> validators;
  private final Consumer<ConfigOption<?>> onUpdate;
  private final HashSet<SavedValueListener<D>> savedValueChangeListeners = new HashSet<>();
  private final HashSet<PendingValueListener<D>> pendingValueChangeListeners = new HashSet<>();

  private String modId;
  private boolean isDisabled;
  private D pendingValue;
  private D savedValue;
  private boolean isDirty;
  private boolean isPendingDefault;
  private boolean isDefault;

  protected ConfigOption(AbstractBuilder<D, ?, ?> builder) {
    this(builder.path, builder.label, builder.defaultValue, builder.noGui, builder.toStringFunction, builder.comment,
        builder.validators, builder.onUpdate
    );
  }

  protected ConfigOption(
      ConfigPath path,
      Text label,
      D defaultValue,
      boolean noGui,
      Function<D, String> toStringFunction,
      List<String> comment,
      List<Validator<D>> validators,
      Consumer<ConfigOption<?>> onUpdate
  ) {
    this.path = path;
    this.label = label;
    this.defaultValue = defaultValue;
    this.noGui = noGui;
    this.toStringFunction = toStringFunction;
    this.comment = comment;
    this.validators = validators;
    this.onUpdate = onUpdate;

    this.pendingValue = this.defaultValue;
    this.savedValue = this.defaultValue;

    this.isDirty = false;
    this.isPendingDefault = true;
    this.isDefault = true;
  }

  public void setModId(String modId) {
    this.modId = modId;
  }

  public String getModId() {
    return this.modId;
  }

  public String getGroup() {
    return this.getPath().getGroup();
  }

  public String getId() {
    return this.getPath().getId();
  }

  public ConfigPath getPath() {
    return this.path;
  }

  public Text getLabel() {
    if (this.label == null) {
      return this.getDefaultLabel();
    }
    return this.label;
  }

  public List<String> getComment() {
    return this.comment;
  }

  public boolean hasGuiControl() {
    return !this.noGui;
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

  public final String getPendingValueAsString() {
    return this.getValueAsString(this.getPendingValue());
  }

  public String getValueAsString(D value) {
    return this.toStringFunction.apply(value);
  }

  public D getDefaultValue() {
    return this.defaultValue;
  }

  /**
   * Manually mark this ConfigOption is dirty, which will force it to write its value to the store.
   */
  public void markDirty() {
    this.isDirty = true;
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

  protected Text getDefaultLabel() {
    return Text.translatable(this.modId + "." + this.path.toString(".") + ".label");
  }

  public void setValue(D pendingValue) {
    D prevPendingValue = this.getPendingValue();
    if (this.areValuesEqual(prevPendingValue, pendingValue)) {
      return;
    }

    this.pendingValue = pendingValue;
    this.isDirty = !this.areValuesEqual(this.getPendingValue(), this.getValue());
    this.isPendingDefault = this.areValuesEqual(this.getPendingValue(), this.getDefaultValue());
    this.pendingValueChangeListeners.forEach((listener) -> listener.onPendingValueChange(this.getPendingValue()));
  }

  /**
   * Marks the value as saved and updates any subscribed listeners. By default, this is called after
   * {@link ModConfig#writeToStore()} successfully writes any pending values to file. Usually you will
   * not need to call this yourself.
   */
  public void commit() {
    D prevSavedValue = this.getValue();
    this.savedValue = this.getPendingValue();

    this.isDirty = false;
    this.isDefault = !this.areValuesEqual(this.getValue(), this.getDefaultValue());

    if (!this.areValuesEqual(prevSavedValue, this.getValue())) {
      this.savedValueChangeListeners.forEach((listener) -> listener.onSavedValueChange(this.getValue()));
    }
  }

  /**
   * Sets the ConfigOption to its default value using {@link #setValue}, meaning that the update won't take
   * effect until it is committed to file with {@link ModConfigImpl#writeToStore()}.
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

  public boolean validate(D value) {
    if (this.validators.isEmpty()) {
      return true;
    }
    return this.validators.stream().allMatch((validator) -> validator.validate(value, this));
  }

  @SuppressWarnings("unchecked")
  public void deserialize(Object data) {
    this.setValue((D) data);
  }

  public Object serialize() {
    return this.getPendingValue();
  }

  public void update() {
    this.onUpdate.accept(this);
  }

  public final void subscribe(SavedValueListener<D> listener) {
    this.savedValueChangeListeners.add(listener);
  }

  public final void unsubscribe(SavedValueListener<D> listener) {
    this.savedValueChangeListeners.remove(listener);
  }

  public final void subscribePending(PendingValueListener<D> listener) {
    this.pendingValueChangeListeners.add(listener);
  }

  public final void unsubscribePending(PendingValueListener<D> listener) {
    this.pendingValueChangeListeners.remove(listener);
  }

  public static abstract class AbstractBuilder<D, C extends ConfigOption<D>, B extends AbstractBuilder<D, C, B>> {
    protected final ConfigPath path;
    protected Text label = null;
    protected D defaultValue;
    protected boolean noGui = false;
    protected Function<D, String> toStringFunction = Object::toString;
    protected List<String> comment = new ArrayList<>();
    protected List<Validator<D>> validators = new ArrayList<>();
    protected Consumer<ConfigOption<?>> onUpdate = (option) -> {
    };
    protected boolean allowNullDefault = false;

    protected AbstractBuilder(ConfigPath path) {
      this.path = path;
    }

    public B setLabel(String i18nKey) {
      this.label = Text.translatable(i18nKey);
      return this.self();
    }

    public B setLabel(Text label) {
      this.label = label;
      return this.self();
    }

    public B setDefaultValue(D defaultValue) {
      this.defaultValue = defaultValue;
      return this.self();
    }

    public B hideFromConfigScreen() {
      this.noGui = true;
      return this.self();
    }

    public B setToStringFunction(Function<D, String> toStringFunction) {
      this.toStringFunction = toStringFunction;
      return this.self();
    }

    public B setComment(String comment) {
      this.comment = List.of(comment);
      return this.self();
    }

    public B setComment(String... comment) {
      this.comment = List.of(comment);
      return this.self();
    }

    public B setComment(Collection<String> comment) {
      this.comment = List.copyOf(comment);
      return this.self();
    }

    public <O extends ConfigOption<D>> B setValidators(Collection<Validator<D>> validators) {
      this.validators = List.copyOf(validators);
      return this.self();
    }

    public <O extends ConfigOption<D>> B addValidator(Validator<D> validator) {
      this.validators.add(validator);
      return this.self();
    }

    public B onUpdate(Consumer<ConfigOption<?>> onUpdate) {
      this.onUpdate = onUpdate;
      return this.self();
    }

    public B allowNullDefaultValue() {
      this.allowNullDefault = true;
      return this.self();
    }

    protected void validate() {
      if (!this.allowNullDefault && this.defaultValue == null) {
        Panic.panic(new IllegalArgumentPanic(
            "All config options must have a non-null default value or explicitly set the flag allowing null"));
      }
    }

    @SuppressWarnings("unchecked")
    private B self() {
      return (B) this;
    }

    protected abstract C buildInternal();

    public final C build() {
      this.validate();
      return this.buildInternal();
    }
  }

  @FunctionalInterface
  public interface Validator<D> {
    boolean validate(D value, ConfigOption<D> option);
  }
}
