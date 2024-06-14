package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.PendingValueListener;
import me.roundaround.roundalib.config.SavedValueListener;
import me.roundaround.roundalib.config.panic.IllegalArgumentPanic;
import me.roundaround.roundalib.config.panic.IllegalStatePanic;
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
  private final ModConfig modConfig;
  private final String group;
  private final String id;
  private final Text label;
  private final D defaultValue;
  private final boolean noGui;
  private final Function<D, String> toStringFunction;
  private final List<String> comment;
  private final List<Validator<D>> validators;
  private final Consumer<ConfigOption<?>> onUpdate;
  private final HashSet<SavedValueListener<D>> savedValueChangeListeners = new HashSet<>();
  private final HashSet<PendingValueListener<D>> pendingValueChangeListeners = new HashSet<>();

  private boolean isDisabled;
  private D pendingValue;
  private D savedValue;
  private boolean isDirty;
  private boolean isPendingDefault;
  private boolean isDefault;

  protected ConfigOption(AbstractBuilder<D, ?> builder) {
    this(builder.modConfig, builder.group, builder.id, builder.label, builder.defaultValue, builder.noGui,
        builder.toStringFunction, builder.comment, builder.validators, builder.onUpdate
    );

    if (!builder.preBuildCalled) {
      this.getModConfig()
          .panic(new IllegalStatePanic(
              "Any builder classes extending ConfigOption.AbstractBuilder must call `preBuild` before passing the " +
                  "instance to the ConfigOption constructor."));
    }
  }

  protected ConfigOption(
      ModConfig modConfig,
      String group,
      String id,
      Text label,
      D defaultValue,
      boolean noGui,
      Function<D, String> toStringFunction,
      List<String> comment,
      List<Validator<D>> validators,
      Consumer<ConfigOption<?>> onUpdate
  ) {
    this.modConfig = modConfig;
    this.group = group;
    this.id = id;
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

  public ModConfig getModConfig() {
    return this.modConfig;
  }

  public String getModId() {
    return this.modConfig.getModId();
  }

  public String getId() {
    return this.id;
  }

  public String getGroup() {
    return this.group;
  }

  public Text getLabel() {
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
      this.pendingValueChangeListeners.forEach((listener) -> listener.onPendingValueChange(this.getPendingValue()));
    }
  }

  /**
   * Marks the value as saved and updates any subscribed listeners. By default, this is called after
   * {@link ModConfig#saveToFile()} successfully writes any pending values to file. Usually you will
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

  @SuppressWarnings("unchecked")
  public static abstract class AbstractBuilder<D, B extends AbstractBuilder<D, B>> {
    protected final ModConfig modConfig;
    protected final String id;
    protected String group = null;
    protected Text label = null;
    protected D defaultValue;
    protected boolean noGui = false;
    protected Function<D, String> toStringFunction = Object::toString;
    protected List<String> comment = new ArrayList<>();
    protected List<Validator<D>> validators = new ArrayList<>();
    protected Consumer<ConfigOption<?>> onUpdate = (option) -> {
    };
    protected boolean allowNullDefault = false;

    private boolean preBuildCalled = false;

    protected AbstractBuilder(ModConfig modConfig, String id) {
      this.modConfig = modConfig;
      this.id = id;
    }

    protected Text getDefaultLabel() {
      StringBuilder builder = new StringBuilder();
      builder.append(this.modConfig.getModId()).append(".");

      if (this.group != null) {
        builder.append(this.group).append(".");
      }

      builder.append(this.id).append(".label");
      return Text.translatable(builder.toString());
    }

    public B setGroup(String group) {
      this.group = group;
      return (B) this;
    }

    public B setLabel(String i18nKey) {
      this.label = Text.translatable(i18nKey);
      return (B) this;
    }

    public B setLabel(Text label) {
      this.label = label;
      return (B) this;
    }

    public B setDefaultValue(D defaultValue) {
      this.defaultValue = defaultValue;
      return (B) this;
    }

    public B hideFromConfigScreen() {
      this.noGui = true;
      return (B) this;
    }

    public B setToStringFunction(Function<D, String> toStringFunction) {
      this.toStringFunction = toStringFunction;
      return (B) this;
    }

    public B setComment(String comment) {
      this.comment = List.of(comment);
      return (B) this;
    }

    public B setComment(String... comment) {
      this.comment = List.of(comment);
      return (B) this;
    }

    public B setComment(Collection<String> comment) {
      this.comment = List.copyOf(comment);
      return (B) this;
    }

    public <O extends ConfigOption<D>> B setValidators(Collection<Validator<D>> validators) {
      this.validators = List.copyOf(validators);
      return (B) this;
    }

    public <O extends ConfigOption<D>> B addValidator(Validator<D> validator) {
      this.validators.add(validator);
      return (B) this;
    }

    public B onUpdate(Consumer<ConfigOption<?>> onUpdate) {
      this.onUpdate = onUpdate;
      return (B) this;
    }

    public B allowNullDefaultValue() {
      this.allowNullDefault = true;
      return (B) this;
    }

    protected final void preBuild() {
      if (this.label == null) {
        this.label = this.getDefaultLabel();
      }

      if (!this.allowNullDefault && this.defaultValue == null) {
        this.modConfig.panic(new IllegalArgumentPanic(
            "All config options must have a non-null default value or explicitly set the flag allowing null"));
      }

      this.preBuildCalled = true;
    }

    public abstract ConfigOption<D> build();
  }

  @FunctionalInterface
  public interface Validator<D> {
    boolean validate(D value, ConfigOption<D> option);
  }
}
