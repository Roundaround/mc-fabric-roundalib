package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ModConfig;
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

  public static Builder builder(ModConfig modConfig, String id) {
    return new Builder(modConfig, id);
  }

  public static class Builder extends ConfigOption.AbstractBuilder<Position, Builder> {
    private Builder(ModConfig modConfig, String id) {
      super(modConfig, id);

      this.setDefaultValue(new Position(0, 0));
    }

    @Override
    public PositionConfigOption build() {
      this.preBuild();
      return new PositionConfigOption(this);
    }
  }
}
