package me.roundaround.roundalib.data.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.roundaround.roundalib.data.ModDataGenerator;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

public abstract class ModDataProvider implements DataProvider {
  protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

  protected final ModDataGenerator root;
  protected final String modId;
  protected final Logger logger;

  public ModDataProvider(ModDataGenerator root) {
    this.root = root;
    this.modId = this.root.getModId();
    this.logger = LogManager.getLogger(this.modId + ":" + this.getName());
  }

  protected abstract String getDataOutputDirectoryName();

  protected void saveJsonToFile(
      DataCache cache, Path rootPath, JsonObject json, Identifier identifier) {
    String path =
        String.format(
            "data/%s/%s/%s.json",
            this.modId, this.getDataOutputDirectoryName(), identifier.getPath());
    this.saveJsonToFile(cache, json, rootPath.resolve(path));
  }

  protected void saveJsonToFile(DataCache cache, JsonObject json, Path path) {
    try {
      DataProvider.writeToPath(GSON, cache, json, path);
    } catch (IOException e) {
      this.logger.error("Failed to write JSON to file {}", path, e);
    }
  }
}
