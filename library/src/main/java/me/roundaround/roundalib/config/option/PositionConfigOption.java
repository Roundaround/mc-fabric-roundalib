package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.value.Position;

import java.util.List;

public class PositionConfigOption extends ConfigOption<Position> {
  protected PositionConfigOption(Builder builder) {
    super(builder);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void deserialize(Object data) {
    if (data instanceof List<?> arrayData) {
      this.setValue(Position.fromList((List<Integer>) arrayData));
    } else {
      this.setValue(Position.fromString((String) data));
    }
  }

  @Override
  public Object serialize() {
    Position value = this.getPendingValue();
    return List.of(value.x(), value.y());
  }

  public static Builder builder(ConfigPath path) {
    return new Builder(path);
  }

  public static class Builder extends ConfigOption.AbstractBuilder<Position, PositionConfigOption, Builder> {
    private Builder(ConfigPath path) {
      super(path);

      this.setDefaultValue(new Position(0, 0));
    }

    @Override
    protected PositionConfigOption buildInternal() {
      return new PositionConfigOption(this);
    }
  }
}
