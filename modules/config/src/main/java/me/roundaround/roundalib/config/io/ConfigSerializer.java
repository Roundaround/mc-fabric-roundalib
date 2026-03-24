package me.roundaround.roundalib.config.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Common interface for reading and writing {@link ConfigDoc} instances to/from a
 * particular file format.
 */
public interface ConfigSerializer {
  ConfigDoc read(Reader reader) throws IOException;

  void write(ConfigDoc doc, Writer writer) throws IOException;
}
