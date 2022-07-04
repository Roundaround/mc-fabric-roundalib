package me.roundaround.roundalib.config.option;

import java.util.List;

import com.electronwill.nightconfig.core.utils.StringUtils;

import me.roundaround.roundalib.config.value.Position;
import net.minecraft.text.Text;

public class PositionConfigOption extends ConfigOption<Position, PositionConfigOption.Builder> {
  protected PositionConfigOption(Builder builder) {
    super(builder);
  }

  @Override
  public void deserialize(Object data) {
    String value = (String) data;
    value = value.substring(1, value.length() - 1);// removes the parentheses
    List<String> split = StringUtils.split(value, ',');// splits the string
    int x = Integer.parseInt(split.get(0));
    int y = Integer.parseInt(split.get(1));
    setValue(new Position(x, y));
  }

  @Override
  public Object serialize() {
    Position value = getValue();
    return "(" + value.x() + "," + value.y() + ")";
  }

  public static Builder builder(String id, String labelI18nKey, Position defaultValue) {
    return new Builder(id, labelI18nKey, defaultValue);
  }

  public static Builder builder(String id, Text label, Position defaultValue) {
    return new Builder(id, label, defaultValue);
  }

  public static class Builder extends ConfigOption.Builder<Position, Builder> {
    private Builder(String id, String labelI18nKey, Position defaultValue) {
      super(id, labelI18nKey, defaultValue);
    }

    private Builder(String id, Text label, Position defaultValue) {
      super(id, label, defaultValue);
    }

    @Override
    public PositionConfigOption build() {
      return new PositionConfigOption(this);
    }
  }
}
