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
  private final String group;
  private final String id;
  private final Text label;
  private final D defaultValue;
  private final boolean noGui;
  private final List<String> comment;
  private final Consumer<ConfigOption<?>> onUpdate;
  private final HashSet<Consumer<D>> savedValueChangeListeners = new HashSet<>();
  private final HashSet<Consumer<D>> pendingValueChangeListeners = new HashSet<>();

  private boolean isDisabled;
  private D pendingValue;
  private D savedValue;
  private boolean isDirty;
  private boolean isPendingDefault;
  private boolean isDefault;

  protected ConfigOption(AbstractBuilder<D, ?> builder) {
    this(builder.modConfig, builder.group, builder.id, builder.label, builder.defaultValue, !builder.noGui,
        builder.comment, builder.onUpdate
    );
  }

  protected ConfigOption(
      ModConfig modConfig,
      String group,
      String id,
      Text label,
      D defaultValue,
      boolean noGui,
      List<String> comment,
      Consumer<ConfigOption<?>> onUpdate
  ) {
    this.modConfig = modConfig;
    this.group = group;
    this.id = id;
    this.label = label;
    this.defaultValue = defaultValue;
    this.noGui = !noGui;
    this.comment = comment;
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

  @SuppressWarnings("unchecked")
  public static abstract class AbstractBuilder<D, B extends AbstractBuilder<D, B>> {
    protected final ModConfig modConfig;
    protected final String id;
    protected String group = null;
    protected Text label;
    protected D defaultValue;
    protected boolean noGui = false;
    protected List<String> comment = List.of();
    protected Consumer<ConfigOption<?>> onUpdate = (option) -> {
    };

    private boolean hasDefaultLabel = true;

    protected AbstractBuilder(ModConfig modConfig, String id) {
      this.modConfig = modConfig;
      this.id = id;

      this.label = this.getDefaultLabel();
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
      if (this.hasDefaultLabel) {
        this.label = this.getDefaultLabel();
      }
      return (B) this;
    }

    public B setLabel(String i18nKey) {
      this.label = Text.translatable(i18nKey);
      this.hasDefaultLabel = false;
      return (B) this;
    }

    public B setLabel(Text label) {
      this.label = label;
      this.hasDefaultLabel = false;
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

    public B onUpdate(Consumer<ConfigOption<?>> onUpdate) {
      this.onUpdate = onUpdate;
      return (B) this;
    }

    public abstract ConfigOption<D> build();
  }
}
