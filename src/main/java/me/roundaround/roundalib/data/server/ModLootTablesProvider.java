package me.roundaround.roundalib.data.server;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import me.roundaround.roundalib.data.ModDataGenerator;
import net.minecraft.block.Block;
import net.minecraft.data.DataCache;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ModLootTablesProvider extends ModDataProvider {
  public ModLootTablesProvider(ModDataGenerator root) {
    super(root);
  }

  protected abstract void generateLootTables(Consumer<LootTableJsonProvider> exporter);

  @Override
  public String getName() {
    return "LootTables." + this.modId;
  }

  @Override
  protected final String getDataOutputDirectoryName() {
    return "loot_tables";
  }

  @Override
  public void run(DataCache cache) throws IOException {
    Path rootPath = this.root.getOutput();
    Set<Identifier> set = Sets.newHashSet();

    this.generateLootTables(
        (lootTableJsonProvider) -> {
          Identifier identifier = lootTableJsonProvider.getId();

          if (!set.add(identifier)) {
            throw new IllegalStateException("Duplicate loot table " + identifier);
          }

          // TODO: Some other kind of validation?

          JsonObject lootTable = lootTableJsonProvider.toJson();
          this.saveJsonToFile(cache, rootPath, lootTable, identifier);
        });
  }

  protected void offerBlockLootTable(
      Consumer<LootTableJsonProvider> exporter, Identifier identifier, ItemConvertible block) {
    exporter.accept(
        new LootTableJsonProvider(identifier, BlockLootTableGenerator.drops(block).build()));
  }

  protected void offerBlockLootTable(
          Consumer<LootTableJsonProvider> exporter, Block block) {
    this.offerBlockLootTable(exporter, block.getLootTableId(), block, null);
  }

  protected void offerBlockLootTable(
          Consumer<LootTableJsonProvider> exporter, Identifier identifier, Block block) {
    this.offerBlockLootTable(exporter, identifier, block, null);
  }

  protected void offerBlockLootTable(
          Consumer<LootTableJsonProvider> exporter,
          Block block,
          @Nullable Function<Block, LootTable.Builder> lootTableFunction) {
    this.offerBlockLootTable(exporter, block.getLootTableId(), block, lootTableFunction);
  }

  protected void offerBlockLootTable(
      Consumer<LootTableJsonProvider> exporter,
      Identifier identifier,
      Block block,
      @Nullable Function<Block, LootTable.Builder> lootTableFunction) {
    if (lootTableFunction == null) {
      lootTableFunction = BlockLootTableGenerator::drops;
    }
    exporter.accept(new LootTableJsonProvider(identifier, lootTableFunction.apply(block).build()));
  }

  protected void offerSlabsBlockLootTable(
      Consumer<LootTableJsonProvider> exporter, Identifier identifier, Block block) {
    this.offerBlockLootTable(exporter, identifier, block, BlockLootTableGenerator::slabDrops);
  }
}
