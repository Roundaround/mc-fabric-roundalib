package me.roundaround.roundalib.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.roundaround.roundalib.RoundaLibMod;
import me.roundaround.roundalib.config.option.ConfigOption;
import me.roundaround.roundalib.util.JsonUtil;
import me.roundaround.roundalib.util.ModInfo;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModConfig {
    private final ModInfo modInfo;
    private final HashMap<String, ConfigOption<?>> configOptions = new HashMap<>();

    public ModConfig(ModInfo modInfo) {
        this.modInfo = modInfo;
    }

    public void registerConfigOption(ConfigOption<?> configOption) {
        this.configOptions.put(configOption.getId(), configOption);
    }

    public void registerConfigOptions(ConfigOption<?>... configOptions) {
        this.configOptions.putAll(Arrays.stream(configOptions)
                .collect(Collectors.toMap(ConfigOption::getId, Function.identity())));
    }

    public void loadFromFile() {
        JsonElement element = JsonUtil.parseJsonFile(this.getConfigFile());

        if (element != null && element.isJsonObject()) {
            JsonObject root = element.getAsJsonObject();

            String configVersion = JsonUtil.getStringOrDefault(root, "config_version", "0.0.1");
            // TODO: Upgrade versions as necessary.

            this.configOptions.values().forEach(configOption -> configOption.readFromJsonRoot(root));
        } else {
            this.configOptions.values().forEach(ConfigOption::resetToDefault);
        }
    }

    public boolean saveToFile() {
        JsonObject root = new JsonObject();
        root.add("config_version", new JsonPrimitive(this.modInfo.getConfigVersion()));

        this.configOptions.values().forEach((configOption -> configOption.writeToJsonRoot(root)));

        return JsonUtil.writeJsonToFile(root, this.getConfigFile());
    }

    public File getConfigDirectory() {
        File dir = FabricLoader.getInstance().getConfigDir().toFile();

        if (!dir.exists() && !dir.mkdirs()) {
            RoundaLibMod.LOGGER.warn("Failed to create config directory '{}'", dir.getAbsolutePath());
        }

        return dir;
    }

    public String getConfigFileName() {
        return this.modInfo.getModId() + ".json";
    }

    public File getConfigFile() {
        return new File(this.getConfigDirectory(), this.getConfigFileName());
    }

    public ModInfo getModInfo() {
        return this.modInfo;
    }
}
