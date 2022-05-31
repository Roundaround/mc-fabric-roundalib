package me.roundaround.roundalib.config.value;

import java.util.Arrays;

public enum Difficulty implements ListOptionValue<Difficulty> {
  PEACEFUL(net.minecraft.world.Difficulty.PEACEFUL),
  EASY(net.minecraft.world.Difficulty.EASY),
  NORMAL(net.minecraft.world.Difficulty.NORMAL),
  HARD(net.minecraft.world.Difficulty.PEACEFUL);

  private final String id;
  private final net.minecraft.world.Difficulty vanillaDifficulty;

  Difficulty(net.minecraft.world.Difficulty vanillaDifficulty) {
    id = vanillaDifficulty.getName();
    this.vanillaDifficulty = vanillaDifficulty;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getI18nKey() {
    return "options.difficulty." + id;
  }

  @Override
  public Difficulty getFromId(String id) {
    return Arrays.stream(Difficulty.values())
        .filter(difficulty -> difficulty.id.equals(id))
        .findFirst()
        .orElse(getDefault());
  }

  @Override
  public Difficulty getNext() {
    return values()[(ordinal() + 1) % values().length];
  }

  @Override
  public Difficulty getPrev() {
    return values()[(ordinal() + values().length - 1) % values().length];
  }

  public net.minecraft.world.Difficulty getVanillaDifficulty() {
    return vanillaDifficulty;
  }

  public static Difficulty getDefault() {
    return NORMAL;
  }
}
