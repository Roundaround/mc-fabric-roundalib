package me.roundaround.roundalib.config.value;

import java.util.Arrays;

public enum GameMode implements EnumValue<GameMode> {
  SURVIVAL(net.minecraft.world.GameMode.SURVIVAL),
  CREATIVE(net.minecraft.world.GameMode.CREATIVE),
  ADVENTURE(net.minecraft.world.GameMode.ADVENTURE),
  SPECTATOR(net.minecraft.world.GameMode.SPECTATOR);

  private final String id;
  private final net.minecraft.world.GameMode vanillaGameMode;

  GameMode(net.minecraft.world.GameMode vanillaGameMode) {
    this.id = vanillaGameMode.getId();
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

  public net.minecraft.world.GameMode getVanillaGameMode() {
    return this.vanillaGameMode;
  }

  public static GameMode getDefault() {
    return SURVIVAL;
  }
}
