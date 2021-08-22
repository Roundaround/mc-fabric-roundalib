package me.roundaround.roundalib.data.server;

import me.roundaround.roundalib.event.RecipeGenerateCallback;
import me.roundaround.roundalib.mixin.RecipesProviderMixin;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.predicate.item.ItemPredicate;

import java.util.function.Consumer;

public abstract class ModRecipesProvider {
    protected ModRecipesProvider() {
        RecipeGenerateCallback.EVENT.register(this::generate);
    }

    public abstract void generate(Consumer<RecipeJsonProvider> exporter);

    protected static InventoryChangedCriterion.Conditions conditionsFromItem(ItemConvertible item) {
        return RecipesProviderMixin.invokeConditionsFromItem(item);
    }

    protected static InventoryChangedCriterion.Conditions conditionsFromItemPredicates(ItemPredicate... items) {
        return RecipesProviderMixin.invokeConditionsFromItemPredicates(items);
    }

    protected static String hasItem(ItemConvertible item) {
        return RecipesProviderMixin.invokeHasItem(item);
    }

    protected static String getItemPath(ItemConvertible item) {
        return RecipesProviderMixin.invokeGetItemPath(item);
    }
}
