package me.roundaround.roundalib.config.panic;

import java.io.Serial;

public class IllegalStatePanic extends Panic {
  @Serial
  private static final long serialVersionUID = -8975007819611387388L;

  public IllegalStatePanic() {
    super();
  }

  public IllegalStatePanic(String message) {
    super(message);
  }

  public IllegalStatePanic(String message, Throwable cause) {
    super(message, cause);
  }

  public IllegalStatePanic(Throwable cause) {
    super(cause);
  }
}
