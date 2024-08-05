package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ConfigPath;

import java.util.List;

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
