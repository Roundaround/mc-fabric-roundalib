package me.roundaround.roundalib.data.server;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import me.roundaround.roundalib.data.ModDataGenerator;
import net.minecraft.data.DataCache;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

public abstract class ModLootTablesProvider extends ModDataProvider {
  public ModLootTablesProvider(ModDataGenerator root) {
    super(root);
  }

  protected abstract void generateLootTables(Consumer<LootTableJsonProvider> exporter);

  @Override
  public void run(DataCache cache) throws IOException {
    Path rootPath = this.root.getOutput();
    Set<Identifier> set = Sets.newHashSet();

    this.generateLootTables((lootTableJsonProvider) -> {
      Identifier identifier = lootTableJsonProvider.getId();

      if (!set.add(identifier)) {
        throw new IllegalStateException("Duplicate loot table " + identifier);
      }

      // TODO: Some other kind of validation?

      JsonObject lootTable = lootTableJsonProvider.toJson();
      this.saveJsonToFile(cache, rootPath, lootTable, identifier);
    });
  }

  @Override
  public String getName() {
    return "LootTables." + this.modId;
  }
}
