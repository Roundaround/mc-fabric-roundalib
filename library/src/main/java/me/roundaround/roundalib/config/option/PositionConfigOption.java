package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.value.Position;

public class PositionConfigOption extends ConfigOption<Position> {
  protected PositionConfigOption(Builder builder) {
    super(builder);
  }

  @Override
  public void deserialize(Object data) {
    String value = (String) data;
    String[] split = value.substring(1, value.length() - 1).split(",");
    int x = Integer.parseInt(split[0]);
    int y = Integer.parseInt(split[1]);
    setValue(new Position(x, y));
  }

  @Override
  public Object serialize() {
    Position value = this.getPendingValue();
    return String.format("(%d,%d)", value.x(), value.y());
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
      return new PositionConfigOption(this);
    }
  }
}
