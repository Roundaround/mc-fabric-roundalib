package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.text.Text;

public class PositionConfigOption extends ConfigOption<Position, PositionConfigOption.Builder> {
  protected PositionConfigOption(Builder builder) {
    super(builder);
  }

  private PositionConfigOption(PositionConfigOption other) {
    super(other);
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
    Position value = getValue();
    return String.format("(%d,%d)", value.x(), value.y());
  }

  @Override
  public PositionConfigOption copy() {
    return new PositionConfigOption(this);
  }

  public static Builder builder(
      ModConfig config,
      String id,
      String labelI18nKey,
      Position defaultValue) {
    return new Builder(config, id, labelI18nKey, defaultValue);
  }

  public static Builder builder(
      ModConfig config,
      String id,
      Text label,
      Position defaultValue) {
    return new Builder(config, id, label, defaultValue);
  }

  public static class Builder extends ConfigOption.AbstractBuilder<Position, Builder> {
    private Builder(ModConfig config, String id, String labelI18nKey, Position defaultValue) {
      super(config, id, labelI18nKey, defaultValue);
    }

    private Builder(ModConfig config, String id, Text label, Position defaultValue) {
      super(config, id, label, defaultValue);
    }

    @Override
    public PositionConfigOption build() {
      return new PositionConfigOption(this);
    }
  }
}
