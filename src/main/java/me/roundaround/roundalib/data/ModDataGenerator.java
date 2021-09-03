package me.roundaround.roundalib.data;

import net.minecraft.data.DataGenerator;

import java.nio.file.Path;
import java.util.ArrayList;

public class ModDataGenerator extends DataGenerator {
    private final String modId;

    public ModDataGenerator(Path output, String modId) {
        super(output, new ArrayList<>());
        this.modId = modId;
    }

    public String getModId() {
        return this.modId;
    }
}
