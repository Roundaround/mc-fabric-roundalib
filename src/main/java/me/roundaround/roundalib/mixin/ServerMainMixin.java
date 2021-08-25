package me.roundaround.roundalib.mixin;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import me.roundaround.roundalib.RoundaLibMod;
import me.roundaround.roundalib.data.DataGenModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.data.DataGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Mixin(net.minecraft.server.Main.class)
public abstract class ServerMainMixin {
    @ModifyVariable(method = "main", at = @At(value = "STORE", ordinal = 0))
    private static OptionParser afterOptionParserConstruction(OptionParser optionParser) {
        optionParser.allowsUnrecognizedOptions();
        return optionParser;
    }

    @Inject(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;startServer(Ljava/util/function/Function;)Lnet/minecraft/server/MinecraftServer;"), cancellable = true)
    private static void preServerStart(String[] args, CallbackInfo callbackInfo) {
        OptionParser optionParser = new OptionParser();
        OptionSpec<String> datagenOption = optionParser.accepts("datagen", "Generate data in the specified output folder").withRequiredArg().defaultsTo("generated");

        OptionSet optionSet = optionParser.parse(args);

        if (optionSet.has(datagenOption)) {
            RoundaLibMod.LOGGER.info("Running data generator in place of the Minecraft server!");

            try {
                Path path = Paths.get(datagenOption.value(optionSet));
                DataGenerator dataGenerator = new DataGenerator(path, new ArrayList<>());

                FabricLoader.getInstance().getEntrypointContainers("datagen", DataGenModInitializer.class).forEach(container -> {
                    String modId = container.getProvider().getMetadata().getId();
                    try {
                        DataGenModInitializer entrypoint = container.getEntrypoint();
                        entrypoint.onInitializeDataGen(dataGenerator);
                    } catch (Exception e) {
                        RoundaLibMod.LOGGER.error("Mod {} has provided an invalid implementation of ModDataGenInitializer", modId, e);
                    }
                });

                dataGenerator.run();
            } catch (Exception e) {
                RoundaLibMod.LOGGER.fatal("Failed to run data generator.", e);
            }

            callbackInfo.cancel();
        }
    }
}
