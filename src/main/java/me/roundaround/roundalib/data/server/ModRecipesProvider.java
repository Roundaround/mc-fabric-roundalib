package me.roundaround.roundalib.data.server;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import me.roundaround.roundalib.data.ModDataGenerator;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.DataCache;
import net.minecraft.data.server.RecipesProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.data.server.recipe.SingleItemRecipeJsonFactory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public abstract class ModRecipesProvider extends ModDataProvider {
  public ModRecipesProvider(ModDataGenerator root) {
    super(root);
  }

  protected abstract void generateRecipes(Consumer<RecipeJsonProvider> exporter);

  @Override
  public String getName() {
    return "Recipes." + this.modId;
  }

  @Override
  protected final String getDataOutputDirectoryName() {
    return "recipes";
  }

  @Override
  public void run(DataCache cache) {
    Path rootPath = this.root.getOutput();
    Set<Identifier> set = Sets.newHashSet();

    this.generateRecipes(
        (recipeJsonProvider) -> {
          Identifier identifier = recipeJsonProvider.getRecipeId();

          if (!set.add(identifier)) {
            throw new IllegalStateException("Duplicate recipe " + identifier);
          }

          JsonObject recipe = recipeJsonProvider.toJson();
          this.saveJsonToFile(cache, rootPath, recipe, identifier);

          JsonObject advancement = recipeJsonProvider.toAdvancementJson();
          Identifier advIdentifier = recipeJsonProvider.getAdvancementId();
          if (advancement != null && advIdentifier != null) {
            this.saveJsonToFile(cache, rootPath, advancement, advIdentifier);
          }
        });
  }

  protected void offerSingleOutputShapelessRecipe(
      Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
    this.offerSingleOutputShapelessRecipe(exporter, output, input, null);
  }

  protected void offerSingleOutputShapelessRecipe(
      Consumer<RecipeJsonProvider> exporter,
      ItemConvertible output,
      ItemConvertible input,
      @Nullable String group) {
    RecipesProvider.offerSingleOutputShapelessRecipe(exporter, output, input, group);
  }

  protected void offerShapelessRecipe(
      Consumer<RecipeJsonProvider> exporter,
      ItemConvertible output,
      ItemConvertible input,
      int outputCount) {
    this.offerShapelessRecipe(exporter, output, input, null, outputCount);
  }

  protected void offerShapelessRecipe(
      Consumer<RecipeJsonProvider> exporter,
      ItemConvertible output,
      ItemConvertible input,
      @Nullable String group,
      int outputCount) {
    RecipesProvider.offerShapelessRecipe(exporter, output, input, group, outputCount);
  }

  protected void offerSmelting(
      Consumer<RecipeJsonProvider> exporter,
      List<ItemConvertible> inputs,
      ItemConvertible output,
      float experience,
      int cookingTime) {
    this.offerSmelting(exporter, inputs, output, experience, cookingTime, null);
  }

  protected void offerSmelting(
      Consumer<RecipeJsonProvider> exporter,
      List<ItemConvertible> inputs,
      ItemConvertible output,
      float experience,
      int cookingTime,
      @Nullable String group) {
    RecipesProvider.offerSmelting(exporter, inputs, output, experience, cookingTime, group);
  }

  protected void offerBlasting(
      Consumer<RecipeJsonProvider> exporter,
      List<ItemConvertible> inputs,
      ItemConvertible output,
      float experience,
      int cookingTime) {
    this.offerBlasting(exporter, inputs, output, experience, cookingTime, null);
  }

  protected void offerBlasting(
      Consumer<RecipeJsonProvider> exporter,
      List<ItemConvertible> inputs,
      ItemConvertible output,
      float experience,
      int cookingTime,
      @Nullable String group) {
    RecipesProvider.offerBlasting(exporter, inputs, output, experience, cookingTime, group);
  }

  protected void offerChiseledBlockRecipe(
      Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
    RecipesProvider.offerChiseledBlockRecipe(exporter, output, input);
  }

  protected ShapedRecipeJsonFactory createChiseledBlockRecipe(
      ItemConvertible output, Ingredient input) {
    return RecipesProvider.createChiseledBlockRecipe(output, input);
  }

  protected void offerStonecuttingRecipe(
      Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
    RecipesProvider.offerRecipe(exporter, output, input);
  }

  protected void offerStonecuttingRecipeAndSingularizeBricks(
      Consumer<RecipeJsonProvider> exporter,
      ItemConvertible output,
      ItemConvertible input,
      int count) {
    SingleItemRecipeJsonFactory.createStonecutting(Ingredient.ofItems(input), output, count)
        .criterion(hasItem(input).replaceAll("bricks", "brick"), conditionsFromItem(input))
        .offerTo(exporter, convertBetween(input, output).replaceAll("bricks", "brick"));
  }

  protected void offerStonecuttingRecipe(
      Consumer<RecipeJsonProvider> exporter,
      ItemConvertible output,
      ItemConvertible input,
      int count) {
    RecipesProvider.offerRecipe(exporter, output, input, count);
  }

  protected InventoryChangedCriterion.Conditions conditionsFromItem(
      NumberRange.IntRange count, ItemConvertible item) {
    return RecipesProvider.conditionsFromItem(count, item);
  }

  protected InventoryChangedCriterion.Conditions conditionsFromItem(ItemConvertible item) {
    return RecipesProvider.conditionsFromItem(item);
  }

  protected InventoryChangedCriterion.Conditions conditionsFromTag(Tag<Item> tag) {
    return RecipesProvider.conditionsFromTag(tag);
  }

  protected InventoryChangedCriterion.Conditions conditionsFromItemPredicates(
      ItemPredicate... items) {
    return RecipesProvider.conditionsFromItemPredicates(items);
  }

  protected String hasItem(ItemConvertible item) {
    return RecipesProvider.hasItem(item);
  }

  protected String getItemPath(ItemConvertible item) {
    return RecipesProvider.getItemPath(item);
  }

  protected String convertBetween(ItemConvertible from, ItemConvertible to) {
    return RecipesProvider.convertBetween(from, to);
  }
}
