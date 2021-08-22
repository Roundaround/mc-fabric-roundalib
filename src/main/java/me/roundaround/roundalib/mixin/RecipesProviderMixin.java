package me.roundaround.roundalib.mixin;

import me.roundaround.roundalib.event.RecipeGenerateCallback;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.server.RecipesProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.predicate.item.ItemPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(RecipesProvider.class)
public abstract class RecipesProviderMixin {
    @Inject(method = "generate(Ljava/util/function/Consumer;)V", at = @At("TAIL"))
    private static void generate(Consumer<RecipeJsonProvider> exporter, CallbackInfo callbackInfo) {
        RecipeGenerateCallback.EVENT.invoker().interact(exporter);
    }

    @Invoker
    public static InventoryChangedCriterion.Conditions invokeConditionsFromItem(ItemConvertible item) {
        throw new AssertionError();
    }

    @Invoker
    public static InventoryChangedCriterion.Conditions invokeConditionsFromItemPredicates(ItemPredicate... items) {
        throw new AssertionError();
    }

    @Invoker
    public static String invokeHasItem(ItemConvertible item) {
        throw new AssertionError();
    }

    @Invoker
    public static String invokeGetItemPath(ItemConvertible item) {
        throw new AssertionError();
    }
}
