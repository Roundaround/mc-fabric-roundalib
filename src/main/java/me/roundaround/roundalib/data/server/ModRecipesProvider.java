package me.roundaround.roundalib.data.server;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
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

public abstract class ModRecipesProvider extends RecipesProvider {
  private final String modId;

  public ModRecipesProvider(DataGenerator root, String modId) {
    super(root);
    this.modId = modId;
  }

  @Override
  public void run(DataCache cache) {
    // TODO: Find a way to get rid of this with mixins.

    Path path = this.root.getOutput();
    Set<Identifier> set = Sets.newHashSet();

    generateRecipes(
        (recipeJsonProvider) -> {
          Identifier identifier = recipeJsonProvider.getRecipeId();

          if (!set.add(identifier)) {
            throw new IllegalStateException("Duplicate recipe " + identifier);
          } else {
            JsonObject recipe = recipeJsonProvider.toJson();
            saveRecipe(
                cache,
                recipe,
                path.resolve("data/" + this.modId + "/recipes/" + identifier.getPath() + ".json"));

            JsonObject advancement = recipeJsonProvider.toAdvancementJson();
            if (advancement != null) {
              saveRecipeAdvancement(
                  cache,
                  advancement,
                  path.resolve(
                      "data/"
                          + this.modId
                          + "/advancements/"
                          + recipeJsonProvider.getAdvancementId().getPath()
                          + ".json"));
            }
          }
        });
  }

  protected abstract void generateRecipes(Consumer<RecipeJsonProvider> exporter);

  public static void offerSingleOutputShapelessRecipe(
      Consumer<RecipeJsonProvider> exporter,
      ItemConvertible output,
      ItemConvertible input,
      @Nullable String group) {
    RecipesProvider.offerSingleOutputShapelessRecipe(exporter, output, input, group);
  }

  public static void offerShapelessRecipe(
      Consumer<RecipeJsonProvider> exporter,
      ItemConvertible output,
      ItemConvertible input,
      @Nullable String group,
      int outputCount) {
    RecipesProvider.offerShapelessRecipe(exporter, output, input, group, outputCount);
  }

  public static void offerSmelting(
      Consumer<RecipeJsonProvider> exporter,
      List<ItemConvertible> inputs,
      ItemConvertible output,
      float experience,
      int cookingTime,
      String group) {
    RecipesProvider.offerSmelting(exporter, inputs, output, experience, cookingTime, group);
  }

  public static void offerBlasting(
      Consumer<RecipeJsonProvider> exporter,
      List<ItemConvertible> inputs,
      ItemConvertible output,
      float experience,
      int cookingTime,
      String group) {
    RecipesProvider.offerBlasting(exporter, inputs, output, experience, cookingTime, group);
  }

  public static void offerChiseledBlockRecipe(
      Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
    RecipesProvider.offerChiseledBlockRecipe(exporter, output, input);
  }

  public static ShapedRecipeJsonFactory createChiseledBlockRecipe(
      ItemConvertible output, Ingredient input) {
    return RecipesProvider.createChiseledBlockRecipe(output, input);
  }

  public static void offerStonecuttingRecipe(
      Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
    RecipesProvider.offerRecipe(exporter, output, input);
  }

  public static void offerStonecuttingRecipeAndSingularizeBricks(
      Consumer<RecipeJsonProvider> exporter,
      ItemConvertible output,
      ItemConvertible input,
      int count) {
    SingleItemRecipeJsonFactory.createStonecutting(Ingredient.ofItems(input), output, count)
        .criterion(hasItem(input).replaceAll("bricks", "brick"), conditionsFromItem(input))
        .offerTo(exporter, convertBetween(input, output).replaceAll("bricks", "brick"));
  }

  public static void offerStonecuttingRecipe(
      Consumer<RecipeJsonProvider> exporter,
      ItemConvertible output,
      ItemConvertible input,
      int count) {
    RecipesProvider.offerRecipe(exporter, output, input, count);
  }

  public static InventoryChangedCriterion.Conditions conditionsFromItem(
      NumberRange.IntRange count, ItemConvertible item) {
    return RecipesProvider.conditionsFromItem(count, item);
  }

  public static InventoryChangedCriterion.Conditions conditionsFromItem(ItemConvertible item) {
    return RecipesProvider.conditionsFromItem(item);
  }

  public static InventoryChangedCriterion.Conditions conditionsFromTag(Tag<Item> tag) {
    return RecipesProvider.conditionsFromTag(tag);
  }

  public static InventoryChangedCriterion.Conditions conditionsFromItemPredicates(
      ItemPredicate... items) {
    return RecipesProvider.conditionsFromItemPredicates(items);
  }

  public static String hasItem(ItemConvertible item) {
    return RecipesProvider.hasItem(item);
  }

  public static String getItemPath(ItemConvertible item) {
    return RecipesProvider.getItemPath(item);
  }

  public static String convertBetween(ItemConvertible from, ItemConvertible to) {
    return RecipesProvider.convertBetween(from, to);
  }
}
