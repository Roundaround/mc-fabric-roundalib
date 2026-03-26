package me.roundaround.roundalib.config.value;

import java.util.Arrays;

public enum GameMode implements EnumValue<GameMode> {
  SURVIVAL(net.minecraft.world.level.GameType.SURVIVAL),
  CREATIVE(net.minecraft.world.level.GameType.CREATIVE),
  ADVENTURE(net.minecraft.world.level.GameType.ADVENTURE),
  SPECTATOR(net.minecraft.world.level.GameType.SPECTATOR);

  private final String id;
  private final net.minecraft.world.level.GameType vanillaGameMode;

  GameMode(net.minecraft.world.level.GameType vanillaGameMode) {
    this.id = vanillaGameMode.getName();
    this.vanillaGameMode = vanillaGameMode;
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public String getI18nKey(String modId) {
    return "selectWorld.gameMode." + this.id;
  }

  @Override
  public GameMode getFromId(String id) {
    return Arrays.stream(GameMode.values()).filter(gamemode -> gamemode.id.equals(id)).findFirst().orElse(getDefault());
  }

  @Override
  public GameMode getNext() {
    return values()[(this.ordinal() + 1) % values().length];
  }

  @Override
  public GameMode getPrev() {
    return values()[(this.ordinal() + values().length - 1) % values().length];
  }

  public net.minecraft.world.level.GameType getVanillaGameMode() {
    return this.vanillaGameMode;
  }

  public static GameMode getDefault() {
    return SURVIVAL;
  }
}
