package me.roundaround.roundalib.data.server.json;

import com.google.gson.JsonObject;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;

public class LootTableJsonProvider {
    private final Identifier identifier;
    private final LootTable lootTable;

    public LootTableJsonProvider(Identifier identifier, LootTable lootTable) {
        this.identifier = identifier;
        this.lootTable = lootTable;
    }

    public JsonObject toJson() {
        return LootManager.toJson(this.lootTable).getAsJsonObject();
    }

    public Identifier getId() {
        return this.identifier;
    }
}
