package me.roundaround.roundalib.data.server;

import me.roundaround.roundalib.data.ModDataGenerator;
import me.roundaround.roundalib.data.ModDataProvider;
import net.minecraft.data.DataCache;

import java.io.IOException;

public abstract class ModBlockTagsProvider extends ModDataProvider {
    public ModBlockTagsProvider(ModDataGenerator root) {
        super(root);
    }

    @Override
    public String getName() {
        return "BlockTags." + this.modId;
    }

    @Override
    protected final String getDataOutputDirectoryName() {
        return "tags/blocks";
    }

    @Override
    public void run(DataCache cache) throws IOException {

    }
}
