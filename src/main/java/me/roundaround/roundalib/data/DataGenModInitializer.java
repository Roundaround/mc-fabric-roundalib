package me.roundaround.roundalib.data;

import net.minecraft.data.DataGenerator;

public interface DataGenModInitializer {
  /** Runs the mod initializer. */
  void onInitializeDataGen(DataGenerator dataGenerator);
}
