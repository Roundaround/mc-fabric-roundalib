package me.roundaround.roundalib;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;

public class LoggerOutputStream extends OutputStream {
  public final Logger logger;
  public final Level level;

  private StringBuffer mem = new StringBuffer();

  /**
   * Creates a new log output stream which logs bytes to the specified logger with the specified
   * level.
   *
   * @param logger the logger where to log the written bytes
   * @param level  the level
   */
  public LoggerOutputStream(Logger logger, Level level) {
    this.logger = logger;
    this.level = level;
  }

  /**
   * Writes a byte to the output stream. This method flushes automatically at the end of a line.
   */
  @Override
  public void write(int b) {
    if ((char) b == '\n') {
      this.flush();
      return;
    }
    this.mem.append((char) b);
  }

  /**
   * Flushes the output stream.
   */
  @Override
  public void flush() {
    this.logger.log(this.level, this.mem);
    this.mem = new StringBuffer();
  }
}
