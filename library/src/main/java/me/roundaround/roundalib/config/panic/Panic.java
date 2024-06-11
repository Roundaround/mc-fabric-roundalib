package me.roundaround.roundalib.config.panic;

import java.io.Serial;

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
}
