package me.roundaround.roundalib.config.panic;

import me.roundaround.roundalib.util.LoggerOutputStream;
import me.roundaround.roundalib.RoundaLib;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.PrintStream;
import java.io.Serial;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Panic extends Throwable {
  @Serial
  private static final long serialVersionUID = 1406840684941742372L;

  public Panic() {
    super();
  }

  public Panic(String message) {
    super(message);
  }

  public Panic(String message, Throwable cause) {
    super(message, cause);
  }

  public Panic(Throwable cause) {
    super(cause);
  }

  public static void panic(Panic panic) {
    panic(panic, grabModIdFromPackage());
  }

  public static void panic(Panic panic, String modId) {
    panic(panic, modId, RoundaLib.LOGGER);
  }

  public static void panic(Panic panic, String modId, Logger logger) {
    Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modId);
    String modName = modContainer.map(container -> container.getMetadata().getName()).orElse(null);
    String issueTracker = modContainer.flatMap(container -> container.getMetadata().getContact().get("issues"))
        .orElse(null);

    logger.error(
        "The mod \"{}\" has encountered an unrecoverable error in its config setup due to a misuse of the RoundaLib " +
            "library. Please report this as a bug to the mod's developer.", modName == null ? modId : modName);

    if (issueTracker != null) {
      logger.error("Issue tracker: {}", issueTracker);
    }

    // Try and immediately throw in order to add this panic to the stack trace
    try {
      throw panic;
    } catch (Panic p) {
      p.printStackTrace(new PrintStream(new LoggerOutputStream(logger, Level.FATAL)));
    }

    System.exit(1);
  }

  private static String grabModIdFromPackage() {
    String packageName = Panic.class.getPackage().getName();
    Pattern pattern = Pattern.compile("([^.]+)\\.roundalib\\.config\\.panic");
    Matcher matcher = pattern.matcher(packageName);

    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }
}
