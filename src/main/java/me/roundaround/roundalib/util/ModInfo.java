package me.roundaround.roundalib.util;

public class ModInfo {
  private final String modId;
  private final String modVersion;
  private final int configVersion;
  private final String modNameI18nKey;
  private final String configScreenTitleI18nKey;

  public ModInfo(String modId, String modVersion, int configVersion) {
    this(modId, modVersion, configVersion, "modname", "config.title");
  }

  public ModInfo(
      String modId,
      String modVersion,
      int configVersion,
      String modNameI18nKey,
      String configScreenTitleI18nKey) {
    this.modId = modId;
    this.modVersion = modVersion;
    this.configVersion = configVersion;
    this.modNameI18nKey = modNameI18nKey;
    this.configScreenTitleI18nKey = configScreenTitleI18nKey;
  }

  public String getModId() {
    return modId;
  }

  public String getModVersion() {
    return modVersion;
  }

  public int getConfigVersion() {
    return configVersion;
  }

  public String getModNameI18nKey() {
    return modNameI18nKey;
  }

  public String getConfigScreenTitleI18nKey() {
    return configScreenTitleI18nKey;
  }
}
