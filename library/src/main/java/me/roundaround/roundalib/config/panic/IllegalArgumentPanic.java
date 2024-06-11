package me.roundaround.roundalib.config.panic;

import java.io.Serial;

public class IllegalArgumentPanic extends Panic {
  @Serial
  private static final long serialVersionUID = 8008786330067063013L;

  public IllegalArgumentPanic() {
    super();
  }

  public IllegalArgumentPanic(String message) {
    super(message);
  }

  public IllegalArgumentPanic(String message, Throwable cause) {
    super(message, cause);
  }

  public IllegalArgumentPanic(Throwable cause) {
    super(cause);
  }
}
