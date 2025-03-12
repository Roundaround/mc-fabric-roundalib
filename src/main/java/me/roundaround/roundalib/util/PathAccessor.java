package me.roundaround.roundalib.util;

import me.roundaround.roundalib.client.event.MinecraftServerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorage;

import java.nio.file.Path;

public class PathAccessor {
  private static PathAccessor instance = null;

  private Path gameDirectory = null;
  private Path configDirectory = null;
  private Path worldDirectory = null;

  private PathAccessor() {
    MinecraftServerEvents.RESOURCE_MANAGER_CREATING.register(this::beforeResourceManagerCreated);
    ServerLifecycleEvents.SERVER_STOPPED.register(this::serverStopped);
  }

  public static void init() {
    // Init code is actually in the constructor so just force instance creation
    getInstance();
  }

  public static PathAccessor getInstance() {
    if (instance == null) {
      instance = new PathAccessor();
    }
    return instance;
  }

  public Path getGameDir() {
    if (this.gameDirectory == null) {
      this.gameDirectory = FabricLoader.getInstance().getGameDir();
    }
    return this.gameDirectory;
  }

  public Path getModDir(String modId) {
    return this.getGameDir().resolve(modId);
  }

  public Path getConfigDir() {
    if (this.configDirectory == null) {
      this.configDirectory = FabricLoader.getInstance().getConfigDir();
    }
    return this.configDirectory;
  }

  public Path getModConfigFile(String modId, ConfigFormat format) {
    return this.getModConfigFile(modId, format, false);
  }

  public Path getModConfigFile(String modId, ConfigFormat format, boolean directory) {
    Path dir = this.getConfigDir();
    if (directory) {
      dir = dir.resolve(modId);
    }
    return this.getConfigFile(dir, modId, format);
  }

  public Path getModConfigDir(String modId) {
    Path dir = this.getConfigDir();
    if (dir == null) {
      return null;
    }
    return dir.resolve(modId);
  }

  public boolean isWorldDirAccessible() {
    return this.worldDirectory != null;
  }

  public Path getWorldDir() {
    return this.worldDirectory;
  }

  public Path getPerWorldModDir(String modId) {
    Path dir = this.getWorldDir();
    if (dir == null) {
      return null;
    }
    return dir.resolve(modId);
  }

  public Path getPerWorldConfigDir() {
    Path dir = this.getWorldDir();
    if (dir == null) {
      return null;
    }
    return dir.resolve("config");
  }

  public Path getPerWorldModConfigFile(String modId, ConfigFormat format) {
    return this.getPerWorldModConfigFile(modId, format, false);
  }

  public Path getPerWorldModConfigFile(String modId, ConfigFormat format, boolean directory) {
    Path dir = this.getPerWorldConfigDir();
    if (dir == null) {
      return null;
    }
    if (directory) {
      dir = dir.resolve(modId);
    }
    return this.getConfigFile(dir, modId, format);
  }

  public Path getPerWorldModConfigDir(String modId) {
    Path dir = this.getPerWorldConfigDir();
    if (dir == null) {
      return null;
    }
    return dir.resolve(modId);
  }

  public Path getConfigFile(Path configDir, String modId, ConfigFormat format) {
    if (configDir == null) {
      return null;
    }
    return configDir.resolve(modId + format.getFileExtension());
  }

  private void beforeResourceManagerCreated(LevelStorage.Session session) {
    this.worldDirectory = session.getDirectory().path();
  }

  private void serverStopped(MinecraftServer server) {
    this.worldDirectory = null;
  }

  public enum ConfigFormat {
    TOML, JSON, YAML, PROPERTIES, INI;

    public String getFileExtension() {
      return "." + this.name().toLowerCase();
    }
  }
}
