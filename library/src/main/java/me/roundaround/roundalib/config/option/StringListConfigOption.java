package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ConfigPath;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class StringListConfigOption extends ConfigOption<List<String>> {
  protected StringListConfigOption(Builder builder) {
    super(builder);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void deserialize(Object data) {
    // NightConfig only deserializes to List<Object>, not List<String>
    this.setValue(((List<Object>) data).stream().map(String::valueOf).toList());
  }

  @Override
  public void setValue(List<String> pendingValue) {
    // Copy on set so the caller can't then modify their local copy and corrupt the config
    super.setValue(List.copyOf(pendingValue));
  }

  @Override
  public List<String> getValue() {
    // Only ever give the current value as an immutable view of the actual data
    return Collections.unmodifiableList(super.getValue());
  }

  @Override
  protected boolean areValuesEqual(List<String> a, List<String> b) {
    if ((a == null) != (b == null)) {
      return false;
    }

    if (a == null) {
      return true;
    }

    if (a.size() != b.size()) {
      return false;
    }

    Iterator<String> iterA = a.iterator();
    Iterator<String> iterB = b.iterator();
    while (iterA.hasNext() && iterB.hasNext()) {
      if (!Objects.equals(iterA.next(), iterB.next())) {
        return false;
      }
    }
    return true;
  }

  public static Builder builder(ConfigPath path) {
    return new Builder(path);
  }

  public static class Builder extends ConfigOption.AbstractBuilder<List<String>, StringListConfigOption, Builder> {
    private Builder(ConfigPath path) {
      super(path);

      this.setDefaultValue(List.of());
    }

    @Override
    protected StringListConfigOption buildInternal() {
      return new StringListConfigOption(this);
    }
  }
}
