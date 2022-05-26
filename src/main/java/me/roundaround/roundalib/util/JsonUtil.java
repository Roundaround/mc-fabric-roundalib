package me.roundaround.roundalib.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.Nullable;

import me.roundaround.roundalib.RoundaLibMod;

public class JsonUtil {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  @Nullable
  public static JsonElement parseJsonFile(File file) {
    if (file != null && file.exists() && file.isFile() && file.canRead()) {
      String fileName = file.getAbsolutePath();

      try {
        JsonParser parser = new JsonParser();
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);

        JsonElement element = parser.parse(reader);
        reader.close();

        return element;
      } catch (Exception e) {
        RoundaLibMod.LOGGER.error("Failed to parse the JSON file '{}'", fileName, e);
      }
    }

    return null;
  }

  public static boolean writeJsonToFile(JsonElement root, File file) {
    OutputStreamWriter writer = null;
    File tempFile = new File(file.getParentFile(), UUID.randomUUID() + ".json.tmp");

    try {
      writer = new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8);
      writer.write(GSON.toJson(root));
      writer.close();

      if (file.exists() && file.isFile() && !file.delete()) {
        RoundaLibMod.LOGGER.error(
            "Failed to overwrite existing JSON file '{}'", file.getAbsolutePath());
      }

      return tempFile.renameTo(file);
    } catch (Exception e) {
      RoundaLibMod.LOGGER.error("Failed to save the JSON file '{}'", file.getAbsolutePath(), e);
    } finally {
      try {
        if (writer != null) {
          writer.close();
        }
      } catch (IOException e) {
        RoundaLibMod.LOGGER.warn("Failed to close JSON file stream", e);
      }
    }

    return false;
  }

  @Nullable
  public static String getStringOrDefault(
      JsonObject obj, String name, @Nullable String defaultValue) {
    if (obj.has(name) && obj.get(name).isJsonPrimitive()) {
      try {
        return obj.get(name).getAsString();
      } catch (Exception ignore) {
      }
    }

    return defaultValue;
  }

  public static int getIntOrDefault(JsonObject obj, String name, int defaultValue) {
    if (obj.has(name) && obj.get(name).isJsonPrimitive()) {
      try {
        return obj.get(name).getAsInt();
      } catch (Exception ignore) {
      }
    }

    return defaultValue;
  }
}
