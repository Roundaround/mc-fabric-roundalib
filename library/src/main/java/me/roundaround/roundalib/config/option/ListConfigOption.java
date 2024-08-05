package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ConfigPath;

import java.util.List;

public class ListConfigOption<T> extends ConfigOption<List<T>> {
  protected ListConfigOption(Builder<T> builder) {
    super(builder);
  }

  public static <T> Builder<T> builder(ConfigPath path) {
    return new Builder<>(path);
  }

  public static class Builder<T> extends ConfigOption.AbstractBuilder<List<T>, ListConfigOption<T>, Builder<T>> {
    private Builder(ConfigPath path) {
      super(path);

      this.setDefaultValue(List.of());
    }

    @Override
    protected ListConfigOption<T> buildInternal() {
      return new ListConfigOption<>(this);
    }
  }
}
