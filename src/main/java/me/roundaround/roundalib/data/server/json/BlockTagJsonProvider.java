package me.roundaround.roundalib.data.server.json;

import com.google.gson.JsonObject;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class BlockTagJsonProvider {
    private final Identifier identifier;
    private final Tag.Builder tagBuilder;

    public BlockTagJsonProvider(Identifier identifier, Tag.Builder tagBuilder) {
        this.identifier = identifier;
        this.tagBuilder = tagBuilder;
    }

    public JsonObject toJson() {
        return tagBuilder.toJson();
    }

    public Identifier getId() {
        return this.identifier;
    }
}
