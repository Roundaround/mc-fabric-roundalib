package me.roundaround.testmod.config;

import com.electronwill.nightconfig.core.Config;
import me.roundaround.roundalib.config.ConfigPath;
import me.roundaround.roundalib.config.GameScopedConfig;
import me.roundaround.roundalib.config.option.*;
import me.roundaround.roundalib.config.value.Difficulty;
import me.roundaround.roundalib.config.value.Position;
import me.roundaround.testmod.TestMod;

import java.util.Arrays;
import java.util.Map;

public class TestModConfig extends GameScopedConfig {
  private static TestModConfig instance = null;

  public static TestModConfig getInstance() {
    if (instance == null) {
      instance = new TestModConfig();
    }
    return instance;
  }

  public final BooleanConfigOption first;
  public final BooleanConfigOption second;
  public final OptionListConfigOption<Difficulty> third;
  public final StringConfigOption fourth;
  public final IntConfigOption fifth;
  public final IntConfigOption sixth;
  public final IntConfigOption seventh;
  public final FloatConfigOption eighth;
  public final FloatConfigOption ninth;
  public final PositionConfigOption tenth;
  public final PositionConfigOption eleventh;
  public final IntConfigOption twelfth;
  public final BooleanConfigOption thirteenth;

  private TestModConfig() {
    super(TestMod.MOD_ID, 3);

    this.first = this.registerConfigOption(
        BooleanConfigOption.builder(this, ConfigPath.of("group0", "testOption0")).setDefaultValue(true).build());

    this.second = this.registerConfigOption(BooleanConfigOption.builder(this, ConfigPath.of("group0", "testOption1"))
        .setDefaultValue(true)
        .onUpdate((option) -> option.setDisabled(!this.first.getPendingValue()))
        .build());

    this.third = this.registerConfigOption(OptionListConfigOption.builder(this, ConfigPath.of("group0", "testOption2"),
            Arrays.stream(Difficulty.values()).toList()
        )
        .setDefaultValue(Difficulty.getDefault())
        .build());

    this.fourth = this.registerConfigOption(StringConfigOption.builder(this, ConfigPath.of("group0", "testOption3"))
        .setDefaultValue("foo")
        .setMinLength(3)
        .setMaxLength(12)
        .onUpdate((option) -> option.setDisabled(!this.first.getPendingValue()))
        .build());

    this.fifth = this.registerConfigOption(IntConfigOption.builder(this, ConfigPath.of("group1", "testOption4"))
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setStep(5)
        .addValidator((value, option) -> value % 25 != 0)
        .onUpdate((option) -> option.setDisabled(!this.first.getPendingValue()))
        .build());

    this.sixth = this.registerConfigOption(IntConfigOption.builder(this, ConfigPath.of("group1", "testOption5"))
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setStep(null)
        .addValidator((value, option) -> value % 25 != 0)
        .onUpdate((option) -> option.setDisabled(!this.first.getPendingValue()))
        .build());

    this.seventh = this.registerConfigOption(IntConfigOption.builder(this, ConfigPath.of("group1", "testOption6"))
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setStep(5)
        .setUseSlider(true)
        .onUpdate((option) -> option.setDisabled(!this.first.getPendingValue()))
        .build());

    this.eighth = this.registerConfigOption(FloatConfigOption.builder(this, ConfigPath.of("group1", "testOption7"))
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .build());

    this.ninth = this.registerConfigOption(FloatConfigOption.builder(this, ConfigPath.of("group1", "testOption8"))
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setUseSlider(true)
        .build());

    this.tenth = this.registerConfigOption(
        PositionConfigOption.builder(this, ConfigPath.of("group2", "testOption9")).build());

    this.eleventh = this.registerConfigOption(
        PositionConfigOption.builder(this, ConfigPath.of("group2", "testOption10"))
            .setDefaultValue(new Position(50, 50))
            .build());

    this.twelfth = this.registerConfigOption(IntConfigOption.builder(this, ConfigPath.of("group2", "testOption11"))
        .setDefaultValue(5)
        .setMinValue(0)
        .setMaxValue(100)
        .setStep(5)
        .setUseSlider(true)
        .build());

    this.thirteenth = this.registerConfigOption(
        BooleanConfigOption.builder(this, ConfigPath.of("group3", "testOption12")).setDefaultValue(true).build());
  }

  @Override
  protected boolean updateConfigVersion(int version, Config inMemoryConfigSnapshot) {
    if (version == 1) {
      // Added a new group1.testOption5 so everything after needs shifting.
      // testOption8 is now also part of group1 rather than group2

      inMemoryConfigSnapshot.set(this.getLegacyPath(2, 11), inMemoryConfigSnapshot.get(this.getLegacyPath(2, 10)));
      inMemoryConfigSnapshot.set(this.getLegacyPath(2, 10), inMemoryConfigSnapshot.get(this.getLegacyPath(2, 9)));
      inMemoryConfigSnapshot.set(this.getLegacyPath(2, 9), inMemoryConfigSnapshot.get(this.getLegacyPath(2, 8)));
      inMemoryConfigSnapshot.remove(this.getLegacyPath(2, 8));
      inMemoryConfigSnapshot.set(this.getLegacyPath(1, 8), inMemoryConfigSnapshot.get(this.getLegacyPath(1, 7)));
      inMemoryConfigSnapshot.set(this.getLegacyPath(1, 7), inMemoryConfigSnapshot.get(this.getLegacyPath(1, 6)));
      inMemoryConfigSnapshot.set(this.getLegacyPath(1, 6), inMemoryConfigSnapshot.get(this.getLegacyPath(1, 5)));
      inMemoryConfigSnapshot.remove(this.getLegacyPath(1, 5));

      return this.updateConfigVersion(2, inMemoryConfigSnapshot);
    }

    if (version == 2) {
      // Removed modId prefixing on paths

      Map.copyOf(inMemoryConfigSnapshot.valueMap()).forEach((path, value) -> {
        if (path.startsWith(this.modId + ".")) {
          inMemoryConfigSnapshot.set(this.removeFirstSegment(path), value);
          inMemoryConfigSnapshot.remove(path);
        }
      });

      return true;
    }

    return false;
  }

  private ConfigPath getPath(int groupNum, int idNum) {
    return ConfigPath.of("group" + groupNum, "testOption" + idNum);
  }

  private String removeFirstSegment(String path) {
    return ConfigPath.parse(path).getId();
  }

  private String getLegacyPath(int groupNum, int idNum) {
    return this.modId + "." + this.getPath(groupNum, idNum);
  }
}
