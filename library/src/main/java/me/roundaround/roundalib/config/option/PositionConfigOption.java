package me.roundaround.roundalib.config.option;

import me.roundaround.roundalib.config.ModConfig;
import me.roundaround.roundalib.config.value.Position;
import net.minecraft.text.Text;

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
    Position value = getValue();
    return String.format("(%d,%d)", value.x(), value.y());
  }

  public static Builder builder(ModConfig modConfig, String id, String labelI18nKey, Position defaultValue) {
    return new Builder(modConfig, id, labelI18nKey, defaultValue);
  }

  public static Builder builder(ModConfig modConfig, String id, Text label, Position defaultValue) {
    return new Builder(modConfig, id, label, defaultValue);
  }

  public static class Builder extends ConfigOption.AbstractBuilder<Position> {
    private Builder(ModConfig modConfig, String id, String labelI18nKey, Position defaultValue) {
      super(modConfig, id, labelI18nKey, defaultValue);
    }

    private Builder(ModConfig modConfig, String id, Text label, Position defaultValue) {
      super(modConfig, id, label, defaultValue);
    }

    @Override
    public PositionConfigOption build() {
      return new PositionConfigOption(this);
    }
  }
}
