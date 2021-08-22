package me.roundaround.roundalib.mixin;

import me.roundaround.roundalib.event.RecipeGenerateCallback;
import net.minecraft.data.server.RecipesProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import org.spongepowered.asm.mixin.Mixin;
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
}
