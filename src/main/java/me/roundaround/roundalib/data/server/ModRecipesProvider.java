package me.roundaround.roundalib.data.server;

import me.roundaround.roundalib.event.RecipeGenerateCallback;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.server.RecipesProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.tag.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public abstract class ModRecipesProvider {
    protected ModRecipesProvider() {
        RecipeGenerateCallback.EVENT.register(this::generate);
    }

    public abstract void generate(Consumer<RecipeJsonProvider> exporter);

    protected static void offerSingleOutputShapelessRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, @Nullable String group) {
        RecipesProvider.offerSingleOutputShapelessRecipe(exporter, output, input, group);
    }

    protected static void offerShapelessRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, @Nullable String group, int outputCount) {
        RecipesProvider.offerShapelessRecipe(exporter, output, input, group, outputCount);
    }

    protected static void offerSmelting(Consumer<RecipeJsonProvider> exporter, List<ItemConvertible> inputs, ItemConvertible output, float experience, int cookingTime, String group) {
        RecipesProvider.offerSmelting(exporter, inputs, output, experience, cookingTime, group);
    }

    protected static void offerBlasting(Consumer<RecipeJsonProvider> exporter, List<ItemConvertible> inputs, ItemConvertible output, float experience, int cookingTime, String group) {
        RecipesProvider.offerBlasting(exporter, inputs, output, experience, cookingTime, group);
    }

    protected static void offerStonecuttingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input) {
        RecipesProvider.offerRecipe(exporter, output, input);
    }

    protected static void offerStonecuttingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, int count) {
        RecipesProvider.offerRecipe(exporter, output, input, count);
    }

    protected static InventoryChangedCriterion.Conditions conditionsFromItem(NumberRange.IntRange count, ItemConvertible item) {
        return RecipesProvider.conditionsFromItem(count, item);
    }

    protected static InventoryChangedCriterion.Conditions conditionsFromItem(ItemConvertible item) {
        return RecipesProvider.conditionsFromItem(item);
    }

    protected static InventoryChangedCriterion.Conditions conditionsFromTag(Tag<Item> tag) {
        return RecipesProvider.conditionsFromTag(tag);
    }

    protected static InventoryChangedCriterion.Conditions conditionsFromItemPredicates(ItemPredicate... items) {
        return RecipesProvider.conditionsFromItemPredicates(items);
    }

    protected static String hasItem(ItemConvertible item) {
        return RecipesProvider.hasItem(item);
    }

    protected static String getItemPath(ItemConvertible item) {
        return RecipesProvider.getItemPath(item);
    }
}
