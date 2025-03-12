package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.manage.ModConfig;
import me.roundaround.roundalib.config.manage.ModConfigImpl;
import me.roundaround.roundalib.config.panic.IllegalArgumentPanic;
import me.roundaround.roundalib.config.panic.Panic;
import me.roundaround.roundalib.util.Observable;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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
  private final Consumer<ConfigOption<D>> onUpdate;
  private final DisabledValueBehavior disabledValueBehavior;
  private final Function<ConfigOption<D>, D> disabledValueSupplier;

  public final Observable<D> pendingValue;
  public final Observable<D> savedValue;
  public final Observable<Boolean> isDisabled;
  public final Observable<Boolean> isDirty;
  public final Observable<Boolean> isPendingDefault;
  public final Observable<Boolean> isDefault;

  private String modId;

  protected <C extends ConfigOption<D>, B extends AbstractBuilder<D, C, B>> ConfigOption(AbstractBuilder<D, C, B> builder) {
    this(builder.path, builder.label, builder.defaultValue, builder.noGui, builder.toStringFunction, builder.comment,
        builder.validators, builder.onUpdate, builder.disabledValueBehavior, builder.disabledValueSupplier
    );
  }

  protected <C extends ConfigOption<D>> ConfigOption(
      ConfigPath path,
      Text label,
      D defaultValue,
      boolean noGui,
      Function<D, String> toStringFunction,
      List<String> comment,
      List<Validator<D>> validators,
      Consumer<ConfigOption<D>> onUpdate,
      DisabledValueBehavior disabledValueBehavior,
      Function<ConfigOption<D>, D> disabledValueSupplier
  ) {
    this.path = path;
    this.label = label;
    this.defaultValue = defaultValue;
    this.noGui = noGui;
    this.toStringFunction = toStringFunction;
    this.comment = comment;
    this.validators = validators;
    this.onUpdate = onUpdate;
    this.disabledValueBehavior = disabledValueBehavior;
    this.disabledValueSupplier = disabledValueSupplier;

    this.pendingValue = Observable.of(this.defaultValue, this::areValuesEqual);
    this.savedValue = Observable.of(this.defaultValue, this::areValuesEqual);
    this.isDisabled = Observable.of(false);
    this.isDirty = Observable.computed(this.pendingValue, this.savedValue, (pendingValue, savedValue) -> {
      return !this.areValuesEqual(pendingValue, savedValue);
    });
    this.isPendingDefault = Observable.computed(this.pendingValue, (pendingValue) -> {
      return this.areValuesEqual(pendingValue, this.defaultValue);
    });
    this.isDefault = Observable.computed(this.savedValue, (savedValue) -> {
      return this.areValuesEqual(savedValue, this.defaultValue);
    });
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
    return this.isDisabled.get();
  }

  public D getValue() {
    return this.savedValue.get();
  }

  public D getPendingValue() {
    if (this.isDisabled()) {
      return switch (this.disabledValueBehavior) {
        case DEFAULT -> this.getDefaultValue();
        case STORED -> this.getValue();
        case PRODUCE -> this.disabledValueSupplier.apply(this);
      };
    }
    return this.pendingValue.get();
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
   * Mark whether this ConfigOption should be disabled.
   */
  public void setDisabled(boolean disabled) {
    this.isDisabled.set(disabled);
  }

  /**
   * Manually mark this ConfigOption is dirty, which will force it to write its value to the store.
   */
  public void markDirty() {
    this.isDirty.set(true);
  }

  /**
   * Whether this ConfigOption has pending changes to its value.
   */
  public boolean isDirty() {
    return this.isDirty.get();
  }

  /**
   * Whether this ConfigOption's pending value is equal to the default.
   */
  public boolean isPendingDefault() {
    return this.isPendingDefault.get();
  }

  /**
   * Whether this ConfigOption's value is different from the default.
   */
  public boolean isDefault() {
    return this.isDefault.get();
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
    if (this.isDisabled()) {
      return;
    }
    this.pendingValue.set(pendingValue);
  }

  /**
   * Marks the value as saved and updates any subscribed listeners. By default, this is called after
   * {@link ModConfig#writeToStore()} successfully writes any pending values to file. Usually you will
   * not need to call this yourself.
   */
  public void commit() {
    this.savedValue.set(this.getPendingValue());
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

  public enum DisabledValueBehavior {
    DEFAULT, STORED, PRODUCE
  }

  public static abstract class AbstractBuilder<D, C extends ConfigOption<D>, B extends AbstractBuilder<D, C, B>> {
    protected final ConfigPath path;
    protected Text label = null;
    protected D defaultValue;
    protected boolean noGui = false;
    protected Function<D, String> toStringFunction = Object::toString;
    protected List<String> comment = new ArrayList<>();
    protected List<Validator<D>> validators = new ArrayList<>();
    protected Consumer<ConfigOption<D>> onUpdate = (option) -> {
    };
    protected boolean allowNullDefault = false;
    private DisabledValueBehavior disabledValueBehavior = DisabledValueBehavior.STORED;
    private Function<ConfigOption<D>, D> disabledValueSupplier = null;

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

    public B setValidators(Collection<Validator<D>> validators) {
      this.validators = List.copyOf(validators);
      return this.self();
    }

    public B addValidator(Validator<D> validator) {
      this.validators.add(validator);
      return this.self();
    }

    public B onUpdate(Consumer<ConfigOption<D>> onUpdate) {
      this.onUpdate = onUpdate;
      return this.self();
    }

    public B allowNullDefaultValue() {
      this.allowNullDefault = true;
      return this.self();
    }

    public B defaultWhenDisabled() {
      this.disabledValueBehavior = DisabledValueBehavior.DEFAULT;
      return this.self();
    }

    public B storedWhenDisabled() {
      this.disabledValueBehavior = DisabledValueBehavior.STORED;
      return this.self();
    }

    public B valueWhenDisabled(Function<ConfigOption<D>, D> valueSupplier) {
      this.disabledValueBehavior = DisabledValueBehavior.PRODUCE;
      this.disabledValueSupplier = valueSupplier;
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
