package me.roundaround.roundalib.data.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import me.roundaround.roundalib.data.ModDataGenerator;
import me.roundaround.roundalib.data.ModDataProvider;
import me.roundaround.roundalib.data.server.json.BlockTagJsonProvider;
import net.minecraft.block.Block;
import net.minecraft.data.DataCache;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class ModBlockTagsProvider extends ModDataProvider {
  public ModBlockTagsProvider(ModDataGenerator root) {
    super(root);
  }

  protected abstract void generateBlockTags(Consumer<BlockTagJsonProvider> exporter);

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
    Path rootPath = this.root.getOutput();
    Set<Identifier> set = Sets.newHashSet();

    this.generateBlockTags(
        (blockTagJsonProvider) -> {
          Identifier identifier = blockTagJsonProvider.getId();

          if (!set.add(identifier)) {
            throw new IllegalStateException("Duplicate block tag " + identifier);
          }

          // TODO: Introduce validation.
          // - Require that there are no duplicates.
          // - Replicate vanilla validation.
          // - Confirm that listed blocks have been registered.
          // - Only allow adding child tags when the Identifier passed also already exists.

          JsonObject lootTable = blockTagJsonProvider.toJson();
          this.saveJsonToFile(cache, rootPath, lootTable, identifier);
        });
  }

  protected BuilderWrapper createBuilderWrapper(String id) {
    return new BuilderWrapper(new Identifier(this.modId, id));
  }

  protected void offerBlockTag(
      Consumer<BlockTagJsonProvider> exporter, String identifier, Block... blocks) {
    this.offerBlockTag(exporter, new Identifier(this.modId, identifier), blocks);
  }

  protected void offerBlockTag(
      Consumer<BlockTagJsonProvider> exporter, String identifier, Tag.Builder tagBuilder) {
    this.offerBlockTag(exporter, new Identifier(this.modId, identifier), tagBuilder);
  }

  protected void offerBlockTag(
      Consumer<BlockTagJsonProvider> exporter, Identifier identifier, Block... blocks) {
    this.offerBlockTag(exporter, new BuilderWrapper(identifier).add(blocks));
  }

  protected void offerBlockTag(
      Consumer<BlockTagJsonProvider> exporter, Identifier identifier, Tag.Builder tagBuilder) {
    this.offerBlockTag(exporter, new BuilderWrapper(identifier, tagBuilder));
  }

  protected void offerBlockTag(Consumer<BlockTagJsonProvider> exporter, BuilderWrapper builderWrapper) {
    exporter.accept(new BlockTagJsonProvider(builderWrapper.identifier, builderWrapper.builder));
  }

  protected BuilderWrapper createCommonBuilderWrapper(String id) {
    return new BuilderWrapper(new Identifier("c", id));
  }

  protected void offerCommonBlockTag(
          Consumer<BlockTagJsonProvider> exporter, String identifier, Block... blocks) {
    this.offerBlockTag(exporter, new Identifier("c", identifier), blocks);
  }

  protected void offerCommonBlockTag(
          Consumer<BlockTagJsonProvider> exporter, String identifier, Tag.Builder tagBuilder) {
    this.offerBlockTag(exporter, new Identifier("c", identifier), tagBuilder);
  }

  protected void offerCommonBlockTag(Consumer<BlockTagJsonProvider> exporter, BuilderWrapper builderWrapper) {
    exporter.accept(new BlockTagJsonProvider(builderWrapper.identifier, builderWrapper.builder));
  }

  protected static class BuilderWrapper {
    private final Identifier identifier;
    private final Tag.Builder builder;

    private BuilderWrapper(Identifier identifier) {
      this(identifier, Tag.Builder.create());
    }

    protected BuilderWrapper(Identifier identifier, Tag.Builder builder) {
      this.identifier = identifier;
      this.builder = builder;
    }

    protected BuilderWrapper add(Block block) {
      this.builder.add(Registry.BLOCK.getId(block), this.identifier.getNamespace());
      return this;
    }

    protected BuilderWrapper addOptional(Identifier id) {
      this.builder.addOptional(id, this.identifier.getNamespace());
      return this;
    }

    protected BuilderWrapper addTag(Identifier id) {
      this.builder.addTag(id, this.identifier.getNamespace());
      return this;
    }

    /** Add a vanilla Block tag directly from BlockTags as a member of this tag. **/
    protected BuilderWrapper addTag(Tag.Identified<Block> identified) {
      this.builder.addTag(identified.getId(), this.identifier.getNamespace());
      return this;
    }

    protected BuilderWrapper addOptionalTag(Identifier id) {
      this.builder.addOptionalTag(id, this.identifier.getNamespace());
      return this;
    }

    protected BuilderWrapper add(Block... blocks) {
      Stream.of(blocks)
          .map(Registry.BLOCK::getId)
          .forEach(
              (id) -> {
                this.builder.add(id, this.identifier.getNamespace());
              });
      return this;
    }
  }
}
