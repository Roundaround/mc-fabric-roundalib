package me.roundaround.roundalib.config.value;

import java.util.Arrays;

import me.roundaround.roundalib.config.ModConfig;

public enum GameMode implements ListOptionValue<GameMode> {
  SURVIVAL(net.minecraft.world.GameMode.SURVIVAL),
  CREATIVE(net.minecraft.world.GameMode.CREATIVE),
  ADVENTURE(net.minecraft.world.GameMode.ADVENTURE),
  SPECTATOR(net.minecraft.world.GameMode.SPECTATOR);

  private final String id;
  private final net.minecraft.world.GameMode vanillaGameMode;

  GameMode(net.minecraft.world.GameMode vanillaGameMode) {
    id = vanillaGameMode.getName();
    this.vanillaGameMode = vanillaGameMode;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getI18nKey(ModConfig config) {
    return "selectWorld.gameMode." + id;
  }

  @Override
  public GameMode getFromId(String id) {
    return Arrays.stream(GameMode.values())
        .filter(gamemode -> gamemode.id.equals(id))
        .findFirst()
        .orElse(getDefault());
  }

  @Override
  public GameMode getNext() {
    return values()[(ordinal() + 1) % values().length];
  }

  @Override
  public GameMode getPrev() {
    return values()[(ordinal() + values().length - 1) % values().length];
  }

  public net.minecraft.world.GameMode getVanillaGameMode() {
    return vanillaGameMode;
  }

  public static GameMode getDefault() {
    return SURVIVAL;
  }
}
