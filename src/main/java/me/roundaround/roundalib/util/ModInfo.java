package me.roundaround.roundalib.util;

public class ModInfo {
  private final String modId;
  private final String modVersion;
  private final String modNameI18nKey;

  public ModInfo(String modId, String modVersion) {
    this(modId, modVersion, modId + ".modname");
  }

  public ModInfo(
      String modId,
      String modVersion,
      String modNameI18nKey) {
    this.modId = modId;
    this.modVersion = modVersion;
    this.modNameI18nKey = modNameI18nKey;
  }

  public String getModId() {
    return modId;
  }

  public String getModVersion() {
    return modVersion;
  }

  public String getModNameI18nKey() {
    return modNameI18nKey;
  }
}
