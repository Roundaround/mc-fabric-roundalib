package me.roundaround.roundalib.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.data.server.recipe.RecipeJsonProvider;

import java.util.function.Consumer;

public interface RecipeGenerateCallback {
    Event<RecipeGenerateCallback> EVENT = EventFactory.createArrayBacked(RecipeGenerateCallback.class, (listeners) -> (exporter) -> {
        for (RecipeGenerateCallback listener : listeners) {
            listener.interact(exporter);
        }
    });

    void interact(Consumer<RecipeJsonProvider> exporter);
}
